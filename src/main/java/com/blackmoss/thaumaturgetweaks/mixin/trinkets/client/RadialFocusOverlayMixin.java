package com.blackmoss.thaumaturgetweaks.mixin.trinkets.client;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.client.casters.RadialFocusOverlay;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Predicate;

@Mixin(RadialFocusOverlay.class)
public class RadialFocusOverlayMixin {
    @Redirect(
            method = "getFociInfo",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryMod(ModList modList, String modId) {
        if (modList.isLoaded(modId)) {
            return true;
        }
        return modId.equals(TCIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = "getFociInfo",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;equippedPouches(Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Predicate;)Ljava/util/List;"))
    private static List<ThaumaturgeCuriosCompat.CurioPouchRef> thaumaturgetweaks$pouches(
            LivingEntity entity, Predicate<ItemStack> predicate) {
        return AccessoryCompat.equippedPouchesInEither(entity, predicate);
    }
}
