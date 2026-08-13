// 带缩放与固定坐标的文本绘制，复刻本体 JEI category 中 pose().scale() 的文本效果。
// 坐标基于 display 面板原点（x/y 为缩放后实际像素位置；centerX 时 x 为居中参考点）。
// 不使用 REI 传入的鼠标坐标，因此不会随鼠标移动。
package com.blackmoss.thaumaturgetweaks.compat.rei.drawable;

import me.shedaniel.rei.api.client.gui.DrawableConsumer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
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
