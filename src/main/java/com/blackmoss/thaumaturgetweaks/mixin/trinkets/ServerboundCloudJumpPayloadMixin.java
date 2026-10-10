package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.network.ServerboundCloudJumpPayload;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerboundCloudJumpPayload.class)
public class ServerboundCloudJumpPayloadMixin {
    @Redirect(
            method = "lambda$handle$0",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryMod(ModList modList, String modId) {
        if (modList.isLoaded(modId)) {
            return true;
        }
        return modId.equals(TTIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = "lambda$handle$0",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;isCurioEquipped(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"))
    private static boolean thaumaturgetweaks$ringEquipped(LivingEntity entity, Item item) {
        return AccessoryCompat.isEquipped(entity, item);
    }
}