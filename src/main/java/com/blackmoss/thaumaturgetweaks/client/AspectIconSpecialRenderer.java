package com.blackmoss.thaumaturgetweaks.client;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Consumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Holder;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// 按住 Shift 时把安瓿/水晶碎片渲染成对应要素图标（含要素颜色）。
// 参考 Avaritia 奇点：按住 Shift 时用另一个渲染替换物品本体，松开恢复默认纹理。
public final class AspectIconSpecialRenderer implements SpecialModelRenderer<Holder<IAspect>> {

    public static final AspectIconSpecialRenderer INSTANCE = new AspectIconSpecialRenderer();

    @Override
    public void submit(
            @Nullable Holder<IAspect> aspect,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int i1,
            boolean b,
            int i2) {
        if (aspect == null) {
            return;
        }
        IAspect value = aspect.value();
        int color = 0xFF000000 | value.color();
        // 用不透明（cutout）渲染，避免半透明混合导致图标偏暗/发灰。
        RenderType renderType = RenderTypes.entityCutout(value.texture());
        submitNodeCollector.submitCustomGeometry(
                poseStack, renderType, (pose, buffer) -> aspectIconQuad(buffer, pose, color));
    }

    // 物品模型空间范围：flat item（GUI 图标）为 0~1（z=0 平面）。
    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> consumer) {
        consumer.accept(new Vector3f(0.0F, 0.0F, 0.0F));
        consumer.accept(new Vector3f(1.0F, 1.0F, 1.0F));
    }

    // 手动通过 setupSpecialModel(renderer, aspect) 传入参数，不依赖从 ItemStack 解析。
    @Override
    public @Nullable Holder<IAspect> extractArgument(@NonNull ItemStack itemStack) {
        return null;
    }

    // 参考 Thaumaturge 的 AspectTagWorldRenderer.addQuadVertex：setColor 必须在最前。
    private static void aspectIconQuad(VertexConsumer buffer, PoseStack.Pose pose, int color) {
        addVertex(buffer, pose, 0.0F, 0.0F, 0.0F, 1.0F, color);
        addVertex(buffer, pose, 1.0F, 0.0F, 1.0F, 1.0F, color);
        addVertex(buffer, pose, 1.0F, 1.0F, 1.0F, 0.0F, color);
        addVertex(buffer, pose, 0.0F, 1.0F, 0.0F, 0.0F, color);
    }

    private static void addVertex(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            float x,
            float y,
            float u,
            float v,
            int color) {
        buffer.addVertex(pose, x, y, (float) 1.0)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightCoordsUtil.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 0.0F, -1.0F);
    }
}
