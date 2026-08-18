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
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class AspectSlotAnnotations {

    // 需要替换渲染的目标物品（注册 ID）。
    private static final Identifier PHIAL_ID = Identifier.fromNamespaceAndPath("thaumaturge", "phial");
    private static final Identifier ESSENTIA_CRYSTAL_ID =
            Identifier.fromNamespaceAndPath("thaumaturge", "essentia_crystal");

    private AspectSlotAnnotations() {
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (!Minecraft.getInstance().hasShiftDown()) {
            return;
        }
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        // getGuiLeft/getGuiTop 在 26.1.2 标记为待移除，但仍是访问界面原点的公开 API。
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        for (Slot slot : screen.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            Holder<IAspect> aspect = aspectOf(stack);
            if (aspect == null) {
                continue;
            }
            // 用 GUI 管线在槽位上画 16×16 要素图标（源 32×32 等比缩放），覆盖安瓿/水晶。
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    aspect.value().texture(),
                    slot.x + left,
                    slot.y + top,
                    0.0F,
                    0.0F,
                    16,
                    16,
                    32,
                    32,
                    32,
                    32,
                    0xFF000000 | aspect.value().color());
        }
    }

    // 若 stack 是要素安瓿或要素水晶碎片，返回其主要要素；否则返回 null。
    private static Holder<IAspect> aspectOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null || (!id.equals(PHIAL_ID) && !id.equals(ESSENTIA_CRYSTAL_ID))) {
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
