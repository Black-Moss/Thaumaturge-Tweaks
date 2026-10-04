package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectKnowledgeAccess;
import com.leclowndu93150.thaumaturge.client.render.GuiBlend;
import com.leclowndu93150.thaumaturge.client.render.aspect.AspectTagRenderer;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import net.minecraft.client.gui.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import org.jetbrains.annotations.NotNull;

public final class AspectEntryRenderer implements EntryRenderer<AspectInstance> {
    public static final AspectEntryRenderer INSTANCE = new AspectEntryRenderer();

    private AspectEntryRenderer() {
    }

    @Override
    public void render(
            @NotNull EntryStack<AspectInstance> entry,
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
        // 必须用 GuiBlend.withAlphaBlend 包裹：AspectTagRenderer 内部走 GuiBlend.blitTinted/blitAdditive，
        // 只调用 enableBlend() 而不设置 blendFunc，且 blit 是入队绘制、真正提交发生在 flush。
        // 若 flush 时混合已被前面的 widget 关掉，图标半透明像素会丢失、糊成纯色色块。
        // withAlphaBlend 会 flush→设混合→绘制→再 flush，保证几何体在混合开启时被提交。
        if (known) {
            GuiBlend.withAlphaBlend(graphics, () -> AspectTagRenderer.render(graphics, x, y, value.aspect()));
        } else {
            GuiBlend.withAlphaBlend(
                    graphics, () -> AspectTagRenderer.renderUnknownChip(graphics, x, y, value.aspect()));
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
