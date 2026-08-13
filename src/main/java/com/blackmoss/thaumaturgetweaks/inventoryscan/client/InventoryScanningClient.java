// 物品栏扫描客户端：手持魔导透镜（鼠标携带）悬停在容器槽位物品或自己身上时，
// 累计悬停时间自动扫描；渲染扫描进度与已发现要素。功能对标本体 ThaumicInventoryScanning。
package com.blackmoss.thaumaturgetweaks.inventoryscan.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.inventoryscan.ServerboundScanSelfPayload;
import com.blackmoss.thaumaturgetweaks.inventoryscan.ServerboundScanSlotPayload;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import com.leclowndu93150.thaumaturge.client.render.aspect.AspectTagRenderer;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import com.leclowndu93150.thaumaturge.registry.TCSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class InventoryScanningClient {

    private static final int SCAN_TICKS = 25;
    private static final int SOUND_TICKS = 3;
    private static final int SOUND_TICK_INTERVAL = 4;
    private static final float SCAN_TICK_VOLUME = 0.2F;
    private static final float SCAN_TICK_PITCH_BASE = 0.45F;
    private static final float SCAN_TICK_PITCH_SPREAD = 0.1F;
    private static final int TAG_SPACING = 18;

    // 玩家模型悬停区域（相对 InventoryScreen 的 leftPos/topPos）。
    private static final int PLAYER_AREA_X = 30;
    private static final int PLAYER_AREA_Y = 5;
    private static final int PLAYER_AREA_WIDTH = 100;
    private static final int PLAYER_AREA_HEIGHT = 85;

    private static boolean serverAvailable;
    private static Slot mouseSlot;
    private static Slot lastScannedSlot;
    private static int ticksHovered;
    private static Object currentScan;
    private static boolean isHoveringPlayer;

    private InventoryScanningClient() {
    }

    // 由网络处理器调用：服务器已安装本模组。
    public static void markServerAvailable() {
        serverAvailable = true;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.isPaused() || !serverAvailable) {
            return;
        }
        tickScanning(player);
    }

    private static void tickScanning(LocalPlayer player) {
        if (isHoldingThaumometer(player)) {
            tickScanningWhileHeld(player);
        } else {
            // 未手持魔导透镜：清空扫描状态。
            ticksHovered = 0;
            currentScan = null;
            lastScannedSlot = null;
        }
    }

    private static void tickScanningWhileHeld(LocalPlayer player) {
        boolean hoveringSlot = mouseSlot != null
                && !mouseSlot.getItem().isEmpty()
                && mouseSlot.mayPickup(player)
                && mouseSlot != lastScannedSlot
                && !(mouseSlot instanceof ResultSlot);
        boolean hovering = (isHoveringPlayer && currentScan != null) || hoveringSlot;
        if (!hovering) {
            return;
        }
        ticksHovered++;
        if (currentScan == null) {
            currentScan = isHoveringPlayer ? player : mouseSlot.getItem();
        }
        if (!ScanningManager.isThingStillScannable(player, currentScan)) {
            currentScan = null;
            lastScannedSlot = mouseSlot;
            ticksHovered = 0;
            return;
        }
        if (ticksHovered > SOUND_TICKS && ticksHovered % SOUND_TICK_INTERVAL == 0) {
            player.level()
                    .playLocalSound(
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            TCSounds.CAMERA_TICKS.get(),
                            SoundSource.PLAYERS,
                            SCAN_TICK_VOLUME,
                            SCAN_TICK_PITCH_BASE
                                    + player.level().getRandom().nextFloat() * SCAN_TICK_PITCH_SPREAD,
                            false);
        }
        if (ticksHovered >= SCAN_TICKS) {
            if (isHoveringPlayer) {
                ClientPacketDistributor.sendToServer(ServerboundScanSelfPayload.INSTANCE);
            } else if (mouseSlot != null) {
                int slotIndex = player.containerMenu.slots.indexOf(mouseSlot);
                if (slotIndex >= 0) {
                    ClientPacketDistributor.sendToServer(new ServerboundScanSlotPayload(slotIndex));
                }
            }
            ticksHovered = 0;
            lastScannedSlot = mouseSlot;
            currentScan = null;
        }
    }

    @SubscribeEvent
    public static void onRenderPost(ScreenEvent.Render.Post event) {
        if (!serverAvailable || !(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) {
            return;
        }
        int mouseX = event.getMouseX();
        int mouseY = event.getMouseY();

        boolean oldHoveringPlayer = isHoveringPlayer;
        isHoveringPlayer = isHoveringPlayer(screen, mouseX, mouseY);
        if (!isHoveringPlayer) {
            Slot oldMouseSlot = mouseSlot;
            mouseSlot = screen.getHoveredSlot();
            if (oldMouseSlot != mouseSlot) {
                ticksHovered = 0;
                currentScan = null;
            }
        }
        if (oldHoveringPlayer != isHoveringPlayer) {
            ticksHovered = 0;
            if (isHoveringPlayer) {
                currentScan = player;
                if (!ScanningManager.isThingStillScannable(player, player)) {
                    currentScan = null;
                }
            }
        }

        if (!isHoldingThaumometer(player)) {
            return;
        }
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        if (mouseSlot != null && !mouseSlot.getItem().isEmpty()) {
            if (currentScan != null) {
                renderScanningProgress(graphics, mouseX, mouseY, ticksHovered / (float) SCAN_TICKS);
            }
            if (!ScanningManager.isThingStillScannable(player, mouseSlot.getItem())) {
                renderAspects(graphics, mouseX + 8, mouseY - 26, ScanningManager.itemAspects(mouseSlot.getItem()));
            }
        } else if (isHoveringPlayer) {
            if (currentScan != null) {
                renderScanningProgress(graphics, mouseX, mouseY, ticksHovered / (float) SCAN_TICKS);
            }
            if (!ScanningManager.isThingStillScannable(player, player)) {
                renderAspects(graphics, mouseX + 17, mouseY - 26, ScanningManager.entityAspects(player));
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!serverAvailable || !event.getItemStack().is(TCItems.THAUMOMETER.get())) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.translatable("thaumaturgetweaks.inventoryscan.thaumometer_tooltip")
                .withStyle(ChatFormatting.GOLD));
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isShiftKeyDown()) {
            String[] lines = Component.translatable("thaumaturgetweaks.inventoryscan.thaumometer_tooltip_more")
                    .getString()
                    .split("\\\\n");
            for (String line : lines) {
                tooltip.add(Component.literal(line).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }

    private static boolean isHoldingThaumometer(Player player) {
        return player.containerMenu.getCarried().is(TCItems.THAUMOMETER.get());
    }

    private static boolean isHoveringPlayer(AbstractContainerScreen<?> screen, int mouseX, int mouseY) {
        if (!(screen instanceof InventoryScreen)) {
            return false;
        }
        int x = screen.getLeftPos() + PLAYER_AREA_X;
        int y = screen.getTopPos() + PLAYER_AREA_Y;
        return mouseX >= x
                && mouseX < x + PLAYER_AREA_WIDTH
                && mouseY >= y
                && mouseY < y + PLAYER_AREA_HEIGHT;
    }

    private static void renderScanningProgress(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float progress) {
        String dots = progress >= 0.75F ? "..." : progress >= 0.5F ? ".." : progress >= 0.25F ? "." : "";
        Component text = Component.translatable("thaumaturgetweaks.inventoryscan.scanning").append(dots);
        graphics.text(Minecraft.getInstance().font, text, mouseX, mouseY - 30, 0xFFFFFFFF, true);
    }

    private static void renderAspects(GuiGraphicsExtractor graphics, int startX, int y, AspectList aspects) {
        if (aspects.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        int x = startX;
        for (AspectInstance instance : aspects.sortedByAmount()) {
            AspectTagRenderer.render(graphics, font, x, y, instance.aspect(), instance.amount());
            x += TAG_SPACING;
        }
    }
}
