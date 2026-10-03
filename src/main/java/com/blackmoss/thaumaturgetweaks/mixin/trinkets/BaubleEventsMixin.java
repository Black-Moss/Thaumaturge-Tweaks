package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.content.equipment.bauble.BaubleEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaubleEvents.class)
public class BaubleEventsMixin {
    @Redirect(
            method = {"onFall", "onDeath", "onPickupXp"},
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryMod(ModList modList, String modTarget) {
        if (modList.isLoaded(modTarget)) {
            return true;
        }
        return modTarget.equals(TCIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = {"onFall", "onPickupXp"},
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;isCurioEquipped(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Z"))
    private static boolean thaumaturgetweaks$equippedInEitherMod(LivingEntity entity, Item item) {
        return AccessoryCompat.isEquipped(entity, item);
    }

    @Redirect(
            method = "onDeath",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;extractCurio(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/Item;)Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack thaumaturgetweaks$extractFromEitherMod(LivingEntity entity, Item item) {
        return AccessoryCompat.extractEquipped(entity, item);
    }
}
