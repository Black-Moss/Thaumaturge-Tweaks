package com.blackmoss.thaumaturgetweaks.mixin;

import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanRules;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanTarget;
import com.leclowndu93150.thaumaturge.content.item.ThaumometerItem;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ThaumometerItem.class)
public abstract class ThaumometerItemMixin {
    private ThaumometerItemMixin() {
    }

    @Redirect(method = "beginScanAt", at = @At(
            value = "INVOKE",
            target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;"
                    + "isStillScannable(Lnet/minecraft/world/entity/player/Player;"
                    + "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanTarget;)Z"))
    private static boolean thaumaturgetweaks$allowContainerBegin(Player player, @Nullable ScanTarget target) {
        return ContainerScanRules.allowsScan(player, target);
    }

    @Redirect(method = "releaseUsing", at = @At(
            value = "INVOKE",
            target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;"
                    + "isStillScannable(Lnet/minecraft/world/entity/player/Player;"
                    + "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanTarget;)Z"))
    private static boolean thaumaturgetweaks$allowContainerRelease(Player player, @Nullable ScanTarget target) {
        return ContainerScanRules.allowsScan(player, target);
    }
}