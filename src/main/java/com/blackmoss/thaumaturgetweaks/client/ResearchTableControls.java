// 研究台界面（ResearchTableScreen）操作增强：
// - 要素合成参考（帮助面板）打开时：滚轮 / PageUp / PageDown / 左右方向键 = 翻帮助页
// - 帮助面板关闭时：鼠标在调色板区域滚轮 = 翻调色板页；PageUp / PageDown / 左右方向键 = 翻调色板页
// 通过 Mixin accessor（ResearchTableScreenAccessor）读写私有字段，避免反射。
package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.mixin.client.screen.research.ResearchTableScreenAccessor;
import com.leclowndu93150.thaumaturge.client.screen.research.ResearchTableScreen;
import com.leclowndu93150.thaumaturge.registry.TCSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ResearchTableControls {

    // 调色板区域（相对界面左上角），与 ResearchTableScreen 常量一致。
    private static final int PALETTE_X = 10;
    private static final int PALETTE_Y = 40;
    private static final int PALETTE_W = 80;
    private static final int PALETTE_H = 80;
    private static final int PALETTE_SLOTS = 25;
    private static final int PAGE_STEP = 5;

    // 要素合成参考（帮助面板）所在纸张区域。
    private static final int SHEET_X = 94;
    private static final int SHEET_Y = 8;
    private static final int SHEET_SIZE = 150;
    private static final int HELPER_ROWS = 7;

    private ResearchTableControls() {
    }

    @SubscribeEvent
    public static void onMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (!(event.getScreen() instanceof ResearchTableScreen screen)) {
            return;
        }
        int step = scrollStep(event.getScrollDeltaY());
        if (step == 0) {
            return;
        }
        int mouseX = (int) event.getMouseX();
        int mouseY = (int) event.getMouseY();
        int left = screen.getLeftPos();
        int top = screen.getTopPos();
        ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
        if (accessor.thaumaturgetweaks$helperOpen()) {
            // 帮助面板打开时：鼠标位于纸张区域滚动翻帮助页。
            if (inRect(mouseX, mouseY, left + SHEET_X, top + SHEET_Y, SHEET_SIZE, SHEET_SIZE)
                    && scrollHelper(screen, step)) {
                event.setCanceled(true);
            }
            return;
        }
        // 帮助面板关闭时：鼠标位于调色板区域滚动翻调色板页。
        if (inRect(mouseX, mouseY, left + PALETTE_X, top + PALETTE_Y, PALETTE_W, PALETTE_H)
                && scrollPalette(screen, step)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof ResearchTableScreen screen)) {
            return;
        }
        ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
        // 退格键：要素合成参考（帮助面板）打开时关闭它。
        if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE && accessor.thaumaturgetweaks$helperOpen()) {
            closeHelper(screen);
            event.setCanceled(true);
            return;
        }
        int key = event.getKeyCode();
        int step;
        if (key == GLFW.GLFW_KEY_PAGE_DOWN || key == GLFW.GLFW_KEY_RIGHT) {
            step = 1;
        } else if (key == GLFW.GLFW_KEY_PAGE_UP || key == GLFW.GLFW_KEY_LEFT) {
            step = -1;
        } else {
            return;
        }
        boolean flipped = accessor.thaumaturgetweaks$helperOpen()
                ? scrollHelper(screen, step)
                : scrollPalette(screen, step);
        if (flipped) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof ResearchTableScreen screen)) {
            return;
        }
        // 鼠标右键：要素合成参考（帮助面板）打开时关闭它。
        if (event.getButton() == 1) {
            ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
            if (accessor.thaumaturgetweaks$helperOpen()) {
                closeHelper(screen);
                event.setCanceled(true);
            }
        }
    }

    // 滚轮增量换算为页步进：上滚=上一页(-1)，下滚=下一页(+1)。
    private static int scrollStep(double delta) {
        return delta > 0 ? -1 : delta < 0 ? 1 : 0;
    }

    // 调色板翻页：步进 -1=上一页 1=下一页，越界时不做任何事。
    private static boolean scrollPalette(ResearchTableScreen screen, int step) {
        ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
        int page = accessor.thaumaturgetweaks$page();
        int lastPage = lastPage(accessor.thaumaturgetweaks$discoveredAspects().size());
        int next = page + step;
        if (next < 0 || next > lastPage) {
            return false;
        }
        accessor.thaumaturgetweaks$setPage(next);
        playKeySound();
        return true;
    }

    // 要素合成参考（帮助面板）翻页：步进 -1=上一页 1=下一页，越界时不做任何事。
    private static boolean scrollHelper(ResearchTableScreen screen, int step) {
        ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
        int page = accessor.thaumaturgetweaks$helperPage();
        int lastPage = Math.max(0, (accessor.thaumaturgetweaks$discoveredCompounds().size() - 1) / HELPER_ROWS);
        int next = page + step;
        if (next < 0 || next > lastPage) {
            return false;
        }
        accessor.thaumaturgetweaks$setHelperPage(next);
        playKeySound();
        return true;
    }

    // 关闭要素合成参考（帮助面板），页码复位到第一页。
    private static void closeHelper(ResearchTableScreen screen) {
        ResearchTableScreenAccessor accessor = (ResearchTableScreenAccessor) (Object) screen;
        accessor.thaumaturgetweaks$setHelperOpen(false);
        accessor.thaumaturgetweaks$setHelperPage(0);
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(TCSounds.KEY.get(), 0.3F, 1.0F);
        }
    }

    // 与 ResearchTableScreen.lastPage 相同：25 格窗口，每页步进 5。
    private static int lastPage(int count) {
        return Math.max(0, (count - (PALETTE_SLOTS - PAGE_STEP)) / PAGE_STEP);
    }

    private static boolean inRect(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static void playKeySound() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(TCSounds.KEY.get(), 0.3F, 1.0F);
        }
    }
}
