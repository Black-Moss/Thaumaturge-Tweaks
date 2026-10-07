package com.blackmoss.thaumaturgetweaks.mixin.client.item;

import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanHelper;
import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanRules;
import com.blackmoss.thaumaturgetweaks.containerscan.ServerboundScanContainerPayload;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanTarget;
import com.leclowndu93150.thaumaturge.api.research.scan.ScannedBlock;
import com.leclowndu93150.thaumaturge.client.item.ThaumometerClientHandler;
import com.leclowndu93150.thaumaturge.content.item.ThaumometerItem;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThaumometerClientHandler.class)
public abstract class ThaumometerClientHandlerMixin {
    @Unique
    private static boolean thaumaturgetweaks$requestSent;

    private ThaumometerClientHandlerMixin() {
    }

    @Inject(method = "tickScanning", at = @At("HEAD"))
    private static void thaumaturgetweaks$requestContainerScan(Minecraft mc, LocalPlayer player, CallbackInfo ci) {
        if (player == null) {
            return;
        }
        if (!player.isUsingItem() || !player.getUseItem().is(TCItems.THAUMOMETER.get())) {
            thaumaturgetweaks$requestSent = false;
            return;
        }
        if (thaumaturgetweaks$requestSent
                || player.getTicksUsingItem() < ThaumometerItem.SCAN_COMPLETE_ELAPSED_TICKS) {
            return;
        }
        Level level = player.level();
        ScanTarget target = ThaumometerItem.resolveTarget(level, player);
        if (target instanceof ScannedBlock(
                BlockPos pos
        ) && ContainerScanHelper.isContainerBlock(level, pos)) {
            ClientPacketDistributor.sendToServer(new ServerboundScanContainerPayload(pos));
            thaumaturgetweaks$requestSent = true;
        }
    }

    @Redirect(method = "tickScanning", at = @At(
            value = "INVOKE",
            target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;"
                    + "isStillScannable(Lnet/minecraft/world/entity/player/Player;"
                    + "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanTarget;)Z"))
    private static boolean thaumaturgetweaks$allowContainerRescan(Player player, @Nullable ScanTarget target) {
        return ContainerScanRules.allowsScan(player, target);
    }
}