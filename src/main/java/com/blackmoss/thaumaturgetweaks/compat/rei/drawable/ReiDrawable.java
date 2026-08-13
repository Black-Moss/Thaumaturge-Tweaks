// 对 Thaumaturge 纹理的 REI 背景包装，语义完全对标本体 JEI 的 AlphaDrawable。
// padding 语义与 AlphaDrawable 一致：实际绘制位置 = x + paddingLeft, y + paddingTop，
// 实际占位尺寸 = width + paddingLeft + paddingRight, height + paddingTop + paddingBottom。
// 注意：不能使用 Widgets.createDrawableWidget（会把鼠标坐标当绘制位置），
// 必须用固定坐标的 Widgets.createTexturedWidget，见 toWidget()。
package com.blackmoss.thaumaturgetweaks.compat.rei.drawable;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import net.minecraft.resources.Identifier;

public final class ReiDrawable {

    // 待绘制纹理的命名空间路径。
    private final Identifier texture;
    // 源纹理左上角 UV 偏移（相对整图像素）。
    private final int u;
    private final int v;
    // 源 UV 区域尺寸（不含 padding）。
    private final int width;
    private final int height;
    // 源纹理整图尺寸（用于归一化 UV）。
    private final int textureWidth;
    private final int textureHeight;
    // 四周内边距（与 AlphaDrawable 同序：上、下、左、右）。
    private final int paddingTop;
    private final int paddingBottom;
    private final int paddingLeft;
    private final int paddingRight;
    // 额外平移偏移（与 AlphaDrawable 的 draw(xOffset, yOffset) 等价，叠加在 padding 之上）。
    private final int offsetX;
    private final int offsetY;

    public ReiDrawable(
            Identifier texture,
            int u,
            int v,
            int width,
            int height,
            int textureWidth,
            int textureHeight) {
        this(texture, u, v, width, height, textureWidth, textureHeight, 0, 0, 0, 0, 0, 0);
    }

    public ReiDrawable(
            Identifier texture,
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
        this(texture, u, v, width, height, textureWidth, textureHeight, paddingTop, paddingBottom, paddingLeft,
                paddingRight, 0, 0);
    }

    public ReiDrawable(
            Identifier texture,
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

    // 占位宽度（含左右内边距）。
    public int getWidth() {
        return width + paddingLeft + paddingRight;
    }

    // 占位高度（含上下内边距）。
    public int getHeight() {
        return height + paddingTop + paddingBottom;
    }

    // 生成固定坐标的纹理 widget，base 为 display 面板左上角。
    // 关键：使用 Widgets.createTexturedWidget，其 consumer 持有固定坐标，不随鼠标移动。
    public Widget toWidget(int baseX, int baseY) {
        int drawX = baseX + paddingLeft + offsetX;
        int drawY = baseY + paddingTop + offsetY;
        return Widgets.createTexturedWidget(
                texture, drawX, drawY, u, v, width, height, width, height, textureWidth, textureHeight);
    }
}
