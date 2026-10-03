package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.GogglesTrinketHandler;
import com.blackmoss.thaumaturgetweaks.compat.trinkets.ThaumaturgeTrinketsCompat;
import com.leclowndu93150.thaumaturge.api.items.GogglesAccess;
import com.leclowndu93150.thaumaturge.api.items.IRevealer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GogglesAccess.class)
public abstract class GogglesAccessMixin {
    private GogglesAccessMixin() {
    }

    @Inject(method = "wearsGoggles", at = @At("HEAD"), cancellable = true)
    private static void thaumaturgetweaks$trinketsWearsGoggles(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ThaumaturgeTrinketsCompat.isActive() && GogglesTrinketHandler.wearsGoggles(entity)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "revealsNodes", at = @At("HEAD"), cancellable = true)
    private static void thaumaturgetweaks$trinketsRevealsNodes(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity != null
                && ThaumaturgeTrinketsCompat.isActive()
                && ThaumaturgeTrinketsCompat.anyTrinketMatches(entity, stack -> thaumaturgeTweaks$showsNodes(stack, entity))) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean thaumaturgeTweaks$showsNodes(ItemStack stack, LivingEntity entity) {
        return stack.getItem() instanceof IRevealer revealer && revealer.showNodes(stack, entity);
    }
}
