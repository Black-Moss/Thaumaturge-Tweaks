package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.GogglesTrinketHandler;
import com.blackmoss.thaumaturgetweaks.compat.trinkets.ThaumaturgeTrinketsCompat;
import com.leclowndu93150.thaumaturge.api.items.GogglesAccess;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让通过 Trinkets 头饰槽佩戴的揭示眼镜，也被父模组的揭示判定认可
 * （父模组的 {@code GogglesAccess$Bindings} 只认它自己那套装备槽）。
 * <p>
 * 26.1.2 的 API 变动：
 * - {@code wearsGoggles} 更名为 {@code wearsRevealingGear}；
 * - 「是否揭示眼镜」不再由物品实现 {@code IGoggles} 接口决定，
 * 改由物品上的 {@code GOGGLES_UPGRADE} 组件（{@code DataComponentType<Unit>}）判定，
 * 统一走 {@code GogglesAccess.isRevealing(ItemStack)}；
 * - 原先的 {@code revealsNodes} / {@code IRevealer.showNodes}（揭示 aura 节点）在 26.1.2
 * 已整体移除、父模组源码里也搜不到，没有替代 API，因此对应的注入一并移除。
 */
@Mixin(GogglesAccess.class)
public abstract class GogglesAccessMixin {
    private GogglesAccessMixin() {
    }

    @Inject(method = "wearsRevealingGear", at = @At("HEAD"), cancellable = true)
    private static void thaumaturgetweaks$trinketsWearsRevealingGear(
            LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ThaumaturgeTrinketsCompat.isActive() && GogglesTrinketHandler.wearsGoggles(entity)) {
            cir.setReturnValue(true);
        }
    }
}