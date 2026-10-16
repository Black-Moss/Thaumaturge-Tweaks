package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.GogglesTrinketHandler;
import com.blackmoss.thaumaturgetweaks.compat.trinkets.ThaumaturgeTrinketsCompat;
import com.leclowndu93150.thaumaturge.api.items.GogglesAccess;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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