package com.blackmoss.thaumaturgetweaks.compat.rei.drawable;

import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.resources.ResourceLocation;

public final class ReiDrawable {
    private final ResourceLocation texture;
    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final int textureWidth;
    private final int textureHeight;
    private final int paddingTop;
    private final int paddingBottom;
    private final int paddingLeft;
    private final int paddingRight;
    private final int offsetX;
    private final int offsetY;

    public ReiDrawable(
            ResourceLocation texture,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight) {
        this(texture, u, v, width, height, textureWidth, textureHeight, 0, 0, 0, 0, 0, 0);
    }

    public ReiDrawable(
            ResourceLocation texture,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            int paddingTop,
            int paddingBottom,
            int paddingLeft,
            int paddingRight) {
        this(texture, u, v, width, height, textureWidth, textureHeight, paddingTop, paddingBottom, paddingLeft, paddingRight, 0, 0);
    }

    public ReiDrawable(
            ResourceLocation texture,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight,
            int paddingTop,
            int paddingBottom,
            int paddingLeft,
            int paddingRight,
            int offsetX,
            int offsetY) {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.paddingTop = paddingTop;
        this.paddingBottom = paddingBottom;
        this.paddingLeft = paddingLeft;
        this.paddingRight = paddingRight;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public int getWidth() {
        return width + paddingLeft + paddingRight;
    }

    public int getHeight() {
        return height + paddingTop + paddingBottom;
    }

    public Widget toWidget(int baseX, int baseY) {
        int drawX = baseX + paddingLeft + offsetX;
        int drawY = baseY + paddingTop + offsetY;
        return Widgets.createTexturedWidget(
                texture, drawX, drawY, u, v, width, height, width, height, textureWidth, textureHeight);
    }
}
