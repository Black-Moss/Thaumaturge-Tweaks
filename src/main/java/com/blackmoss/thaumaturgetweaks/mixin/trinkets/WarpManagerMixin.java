package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.content.warp.WarpManager;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WarpManager.class)
public class WarpManagerMixin {
    @Redirect(
            method = "getWarpFromGear",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryMod(ModList modList, String modId) {
        if (modList.isLoaded(modId)) {
            return true;
        }
        return modId.equals(TCIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = "getWarpFromGear",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;equippedCurios(Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/List;"))
    private static List<ItemStack> thaumaturgetweaks$equippedInEitherMod(LivingEntity entity) {
        return AccessoryCompat.equippedInEither(entity);
    }
}
