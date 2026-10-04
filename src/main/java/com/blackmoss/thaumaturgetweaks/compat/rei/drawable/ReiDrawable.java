package com.blackmoss.thaumaturgetweaks.compat.rei.drawable;

import com.leclowndu93150.thaumaturge.client.render.GuiBlend;
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

    // 层级完全由 setupDisplay 里的添加顺序决定：MC 1.21 的 GUI 用的是 2D 矩阵栈
    // （Matrix3x2fStack），translate 的 z 参数无效，所以抬高 z 起不到任何作用。
    // REI 自己的 EntryWidget 里那句 translate(0, 0, 100) 同理无效。
    // 结论：drawable 必须添加在所有 Slot 之前，槽位（物品）才会画在它上面。
    // 两个方法名用于表达语义（背景 / 装饰），当前实现一致。
    public Widget toBackgroundWidget(int baseX, int baseY) {
        return toWidget(baseX, baseY);
    }

    public Widget toWidget(int baseX, int baseY) {
        int drawX = baseX + paddingLeft + offsetX;
        int drawY = baseY + paddingTop + offsetY;
        // 必须用父模组的 GuiBlend.withAlphaBlend 包裹，不能直接 blit。
        //
        // 病因：GuiGraphics.blit 在 1.21.1 是「入队」而非立即绘制，真正绘制发生在之后的 flush。
        // 而 blend 开关是立即生效的 GL 状态。于是同一 display 里前面的自定义 widget
        // （多方块 3D 预览的 buffers.endBatch()、ReiTextDrawable 的文本绘制）改掉 blend 状态后，
        // 我们这份几何体到 flush 时已经没有混合了 —— 半透明像素被直接写入帧缓冲，
        // 细节全部丢失，只剩不透明的底色，表现为「纯色色块」（物品不透明所以看着正常）。
        // 换 createTexturedWidget / 换 blit 重载都无效，因为问题不在绘制方式，而在 flush 时的混合状态。
        //
        // withAlphaBlend 做的事：flush 掉残留几何体 → enableBlend + defaultBlendFunc
        // → 绘制 → 再 flush（保证几何体在混合开启时被提交）→ 复位 color/blendFunc → disableBlend。
        return Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> GuiBlend.withAlphaBlend(
                graphics,
                () -> graphics.blit(
                        texture, drawX, drawY, (float) u, (float) v, width, height, textureWidth, textureHeight)));
    }
}
