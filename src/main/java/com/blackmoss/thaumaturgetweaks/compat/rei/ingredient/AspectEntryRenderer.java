package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectKnowledgeAccess;
import com.leclowndu93150.thaumaturge.client.render.aspect.AspectTagRenderer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.jetbrains.annotations.NotNull;

public final class AspectEntryRenderer implements EntryRenderer<AspectInstance> {
    public static final AspectEntryRenderer INSTANCE = new AspectEntryRenderer();

    private AspectEntryRenderer() {
    }

    @Override
    public void render(@NotNull EntryStack<AspectInstance> entry,
                       @NotNull GuiGraphics graphics,
                       @NotNull Rectangle bounds,
                       int mouseX,
                       int mouseY,
                       float delta) {
        AspectInstance value = entry.getValue();
        if (value == null || value.aspect() == null) {
            return;
        }
        int x = bounds.x;
        int y = bounds.y;
        boolean known = AspectKnowledgeAccess.isKnown(value.aspect());
        if (known) {
            AspectTagRenderer.render(graphics, x, y, value.aspect());
        } else {
            AspectTagRenderer.renderUnknownChip(graphics, x, y, value.aspect());
        }
    }

    @Override
    @NotNull
    public Tooltip getTooltip(@NotNull EntryStack<AspectInstance> entry, @NotNull TooltipContext context) {
        AspectInstance value = entry.getValue();
        if (value == null || value.aspect() == null) {
            return Tooltip.create();
        }
        return Tooltip.create(AspectComponents.name(value.aspect()));
    }
}
