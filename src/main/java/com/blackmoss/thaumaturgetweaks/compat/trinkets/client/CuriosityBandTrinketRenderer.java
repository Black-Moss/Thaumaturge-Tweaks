package com.blackmoss.thaumaturgetweaks.compat.trinkets.client;

import com.leclowndu93150.thaumaturge.TCIds;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class CuriosityBandTrinketRenderer implements TrinketRenderer {
    private static final Identifier TEXTURE = TCIds.rl("textures/item/curiosity_band_worn.png");
    private static final float FACE_Z = -0.26F;
    private static final float HELMET_LIFT = 0.06F;

    private static void vertex(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float u, float v,
            int light) {
        buffer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 0.0F, -1.0F);
    }

    @Override
    public void submit(
            ItemStack stack,
            TrinketSlotAccess slotReference,
            EntityModel<? extends LivingEntityRenderState> contextModel,
            PoseStack poseStack,
            SubmitNodeCollector submit,
            int light,
            LivingEntityRenderState state,
            float limbAngle,
            float limbDistance) {
        if (!(contextModel instanceof HumanoidModel<?> humanoid)) {
            return;
        }
        LivingEntity wearer = slotReference.inventory().getAttachment().getEntity();
        boolean helmeted = wearer != null && !wearer.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        poseStack.pushPose();
        humanoid.head.translateAndRotate(poseStack);
        float z = FACE_Z - (helmeted ? HELMET_LIFT : 0.0F);
        submit.submitCustomGeometry(poseStack, RenderTypes.entityCutout(TEXTURE), (pose, buffer) -> {
            vertex(buffer, pose, -0.25F, -0.5F, z, 0.0F, 0.0F, light);
            vertex(buffer, pose, -0.25F, 0.3125F, z, 0.0F, 1.0F, light);
            vertex(buffer, pose, 0.25F, 0.3125F, z, 1.0F, 1.0F, light);
            vertex(buffer, pose, 0.25F, -0.5F, z, 1.0F, 0.0F, light);
        });
        poseStack.popPose();
    }
}
