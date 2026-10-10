package com.blackmoss.thaumaturgetweaks.mixin.trinkets.client;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.client.equipment.CloudRingClientHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CloudRingClientHandler.class)
public class CloudRingClientHandlerMixin {
    @Redirect(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryModLoaded(ModList modList, String modId) {
        if (modList.isLoaded(modId)) {
            return true;
        }
        return modId.equals(TTIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;isCurioEquipped(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"))
    private static boolean thaumaturgetweaks$ringEquippedInEitherMod(LivingEntity entity, Item item) {
        return AccessoryCompat.isEquipped(entity, item);
    }
}
