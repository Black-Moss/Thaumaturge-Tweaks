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

    // 层级完全由 setupDisplay 里的添加顺序决定：MC 26.1 的 GUI 用的是 2D 矩阵栈
    // （Matrix3x2fStack，父模组 AspectTagRenderer 也是 translate(x, y) 两参数），
    // translate 的 z 参数无效，所以抬高 z 起不到任何作用。REI 自己的
    // EntryWidget#drawCurrentEntry 里那句 translate(0, 0, 100) 同理无效。
    // 结论：drawable 必须添加在所有 Slot 之前，槽位（物品）才会画在它上面。
    // 两个方法名用于表达语义（背景 / 装饰），当前实现一致。
    public Widget toBackgroundWidget(int baseX, int baseY) {
        return toWidget(baseX, baseY);
    }

    public Widget toWidget(int baseX, int baseY) {
        int drawX = baseX + paddingLeft + offsetX;
        int drawY = baseY + paddingTop + offsetY;
        // 用 REI 官方的 createTexturedWidget，由 REI 内部负责纹理绘制。
        // 不要自己调 graphics.blit：GuiGraphics 在本版本只有三个重载
        // ((RL;IIFFIIII) / (RL;IIIIII) / (RL;IIIIIIIIII))，参数语义极易搞错，
        // 实测分别表现为"纯色色块"、"背景错乱"、"缩小并平铺"。
        // 参数顺序（见 Widgets#createTexturedWidget 最终重载）：
        //   texture, x, y, u, v, 绘制宽, 绘制高, 采样区域宽, 采样区域高, 纹理宽, 纹理高
        return Widgets.createTexturedWidget(
                texture,
                drawX, drawY,
                (float) u, (float) v,
                width, height,
                width, height,
                textureWidth, textureHeight);
    }
}
