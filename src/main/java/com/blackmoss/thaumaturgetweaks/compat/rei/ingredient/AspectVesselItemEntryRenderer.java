package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.blackmoss.thaumaturgetweaks.client.AspectSlotAnnotations;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import net.minecraft.client.gui.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Nullable;

public final class AspectVesselItemEntryRenderer implements EntryRenderer<ItemStack> {
    private final EntryRenderer<ItemStack> fallback;

    public AspectVesselItemEntryRenderer(EntryRenderer<ItemStack> fallback) {
        this.fallback = fallback;
    }

    @Override
    public void render(
            @NotNull EntryStack<ItemStack> entry,
            @NotNull GuiGraphics graphics,
            @NotNull Rectangle bounds,
            int mouseX,
            int mouseY,
            float delta) {
        ItemStack stack = entry.getValue();
        if (Minecraft.getInstance().hasShiftDown()
                && AspectSlotAnnotations.renderAspectIcon(graphics, bounds.x, bounds.y, stack)) {
            return;
        }
        if (fallback != null) {
            fallback.render(entry, graphics, bounds, mouseX, mouseY, delta);
        }
    }

    @Override
    public @Nullable Tooltip getTooltip(@NotNull EntryStack<ItemStack> entry, @NotNull TooltipContext context) {
        if (fallback != null) {
            return fallback.getTooltip(entry, context);
        }
        return Tooltip.create();
    }
}
