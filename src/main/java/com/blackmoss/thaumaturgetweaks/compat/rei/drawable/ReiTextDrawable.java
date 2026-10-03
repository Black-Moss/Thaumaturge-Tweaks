package com.blackmoss.thaumaturgetweaks.compat.rei.drawable;

import me.shedaniel.rei.api.client.gui.DrawableConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public final class ReiTextDrawable implements DrawableConsumer {
    private final Component text;
    private final float scale;
    private final int color;
    private final float x;
    private final float y;
    private final boolean centerX;

    public ReiTextDrawable(Component text, float scale, int color, float x, float y) {
        this(text, scale, color, x, y, false);
    }

    public ReiTextDrawable(Component text, float scale, int color, float x, float y, boolean centerX) {
        this.text = text;
        this.scale = scale;
        this.color = color;
        this.x = x;
        this.y = y;
        this.centerX = centerX;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(text);
        float drawX = centerX ? x - textWidth * scale / 2.0F : x;
        graphics.pose().pushMatrix();
        graphics.pose().translate(drawX, y);
        graphics.pose().scale(scale, scale);
        graphics.text(font, text, 0, 0, color, false);
        graphics.pose().popMatrix();
    }
}
