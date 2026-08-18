package com.blackmoss.thaumaturgetweaks.mixin.client.renderer;

import com.blackmoss.thaumaturgetweaks.client.AspectIconSpecialRenderer;
import com.blackmoss.thaumaturgetweaks.client.AspectSlotAnnotations;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {

    // 仅在按住 Shift 且目标是安瓿/水晶碎片时，把渲染替换成要素图标。
    @Inject(method = "appendItemLayers", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$replaceAspectItemLayer(
            ItemStackRenderState output,
            ItemStack item,
            ItemDisplayContext displayContext,
            @Nullable Level level,
            @Nullable ItemOwner owner,
            int seed,
            CallbackInfo ci) {
        boolean shift = Minecraft.getInstance().hasShiftDown();
        Holder<IAspect> aspect = shift ? AspectSlotAnnotations.aspectOf(item) : null;
        if (aspect == null) {
            return;
        }
        // 此时 output 已被 updateForTopItem 清空，直接追加要素图标图层并跳过原模型解析。
        // appendModelIdentityElement 让 GUI 的 TrackingItemStackRenderState 缓存把不同要素的
        // 安瓿/水晶视为不同渲染身份，避免所有物品串用同一个要素图标。
        output.appendModelIdentityElement(aspect);
        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setupSpecialModel(AspectIconSpecialRenderer.INSTANCE, aspect);
        ci.cancel();
    }
}
