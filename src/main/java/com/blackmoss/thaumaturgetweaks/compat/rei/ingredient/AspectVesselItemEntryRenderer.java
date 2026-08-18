// 按住 Shift 时，把 REI 界面中要素安瓿 / 要素水晶碎片的物品本体替换成对应要素图标。
// 与背包 GUI 的 AspectSlotAnnotations 行为一致：先画圆形背景盖住物品本体，再画要素符号；
// 未按 Shift 或无法取得要素时，委托给 REI 默认的物品条目渲染器。
package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.blackmoss.thaumaturgetweaks.client.AspectSlotAnnotations;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class AspectVesselItemEntryRenderer implements EntryRenderer<ItemStack> {

    private final EntryRenderer<ItemStack> fallback;

    public AspectVesselItemEntryRenderer(EntryRenderer<ItemStack> fallback) {
        this.fallback = fallback;
    }

    @Override
    public void render(@NotNull EntryStack<ItemStack> entry,
                       @NotNull GuiGraphics graphics,
                       @NotNull Rectangle bounds,
                       int mouseX,
                       int mouseY,
                       float delta) {
        ItemStack stack = entry.getValue();
        // Shift 按下且是要素容器 → 替换为要素图标；否则走 REI 默认渲染。
        if (Minecraft.getInstance().hasShiftDown()
                && AspectSlotAnnotations.renderAspectIcon(graphics, bounds.x, bounds.y, stack)) {
            return;
        }
        if (fallback != null) {
            fallback.render(entry, graphics, bounds, mouseX, mouseY, delta);
        }
    }

    @Override
    @NotNull
    public Tooltip getTooltip(@NotNull EntryStack<ItemStack> entry, @NotNull TooltipContext context) {
        if (fallback != null) {
            return fallback.getTooltip(entry, context);
        }
        return Tooltip.create();
    }
}
