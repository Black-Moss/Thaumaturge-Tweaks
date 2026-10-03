package com.blackmoss.thaumaturgetweaks.mixin.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.AccessoryCompat;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.content.equipment.bauble.AmuletVisItem;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AmuletVisItem.class)
public class AmuletVisItemMixin {
    @Redirect(
            method = "wornTick",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private static boolean thaumaturgetweaks$anyAccessoryMod(ModList modList, String modId) {
        if (modList.isLoaded(modId)) {
            return true;
        }
        return modId.equals(TCIds.CURIOS) && AccessoryCompat.isAnyAccessoryModLoaded();
    }

    @Redirect(
            method = "wornTick",
            at = @At(value = "INVOKE", target = "Lcom/leclowndu93150/thaumaturge/compat/curio/ThaumaturgeCuriosCompat;rechargeFirstCurio(Lnet/minecraft/world/entity/player/Player;)Z"))
    private static boolean thaumaturgetweaks$rechargeEitherMod(Player player) {
        return AccessoryCompat.rechargeFirstEquipped(player);
    }
}
