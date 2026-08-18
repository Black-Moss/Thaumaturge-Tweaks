// 按住 Shift 时，把要素安瓿 / 要素水晶碎片的物品本体替换成对应要素图标（含要素颜色）。
// 仅在容器 GUI 中生效（参考 Avaritia 奇点限定 GUI）。用 Thaumaturge 的 AspectTagRenderer 在
// 槽位上绘制 16×16 要素图标覆盖物品（GUI_TEXTURED 管线，亮度正常），松开 Shift 恢复默认贴图。
package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class AspectSlotAnnotations {

    // 需要替换渲染的目标物品（注册 ID）。
    private static final Identifier PHIAL_ID = Identifier.fromNamespaceAndPath("thaumaturge", "phial");
    private static final Identifier ESSENTIA_CRYSTAL_ID =
            Identifier.fromNamespaceAndPath("thaumaturge", "essentia_crystal");
    // 要素图标的圆形背景纹理（与普通要素图标同尺寸），用于盖住物品本体。
    private static final Identifier ASPECT_BACK_TEXTURE =
            Identifier.fromNamespaceAndPath("thaumaturge", "textures/aspects/_back.png");

    private AspectSlotAnnotations() {
    }

    // 供 REI 条目渲染器复用：判断 stack 是否是需要替换渲染的要素容器（安瓿/水晶碎片）。
    public static boolean isAspectVessel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.equals(PHIAL_ID) || id.equals(ESSENTIA_CRYSTAL_ID);
    }

    // 供 REI 条目渲染器复用：要素图标的圆形背景纹理。
    public static Identifier aspectBackTexture() {
        return ASPECT_BACK_TEXTURE;
    }

    // 在 (x, y) 处以 16×16 绘制该物品的要素图标（圆形背景 + 要素符号）。
    // 若 stack 不是要素容器或无法取得要素则返回 false，调用方应回退到默认渲染。
    public static boolean renderAspectIcon(GuiGraphicsExtractor graphics, int x, int y, ItemStack stack) {
        if (graphics == null || stack == null || stack.isEmpty()) {
            return false;
        }
        Holder<IAspect> aspect = aspectOf(stack);
        if (aspect == null) {
            return false;
        }
        // 先画圆形背景盖住物品本体，再画要素符号（均 16×16，源 32×32 等比缩放）。
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, ASPECT_BACK_TEXTURE, x, y, 0.0F, 0.0F, 16, 16, 32, 32, 32, 32, 0xFFFFFFFF);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                aspect.value().texture(),
                x,
                y,
                0.0F,
                0.0F,
                16,
                16,
                32,
                32,
                32,
                32,
                0xFF000000 | aspect.value().color());
        return true;
    }

    @SubscribeEvent
    public static void onContainerRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!Minecraft.getInstance().hasShiftDown()) {
            return;
        }
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        // Foreground 事件触发时 graphics 的 pose 已平移到界面原点，故用相对坐标 slot.x/slot.y。
        for (Slot slot : screen.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            renderAspectIcon(graphics, slot.x, slot.y, stack);
        }
    }

    // 若 stack 是要素安瓿或要素水晶碎片，返回其主要要素；否则返回 null。
    public static Holder<IAspect> aspectOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!id.equals(PHIAL_ID) && !id.equals(ESSENTIA_CRYSTAL_ID)) {
            return null;
        }
        AspectList aspects = AspectIndexAccess.of(stack);
        if (aspects == null || aspects.isEmpty()) {
            return null;
        }
        AspectInstance primary = aspects.entries().getFirst();
        if (primary == null || primary.aspect() == null) {
            return null;
        }
        return primary.aspect();
    }
}
