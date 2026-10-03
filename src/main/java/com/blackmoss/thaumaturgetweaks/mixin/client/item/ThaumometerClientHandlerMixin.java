package com.blackmoss.thaumaturgetweaks.mixin.client.item;

import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanHelper;
import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanRules;
import com.blackmoss.thaumaturgetweaks.containerscan.ServerboundScanContainerPayload;
import com.leclowndu93150.thaumaturge.client.item.ThaumometerClientHandler;
import com.leclowndu93150.thaumaturge.content.item.ThaumometerItem;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
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
        if (ThaumometerItem.resolveTarget(level, player) instanceof BlockPos pos
                && ContainerScanHelper.isContainerBlock(level, pos)) {
            PacketDistributor.sendToServer(new ServerboundScanContainerPayload(pos));
            thaumaturgetweaks$requestSent = true;
        }
    }

    @Redirect(method = "tickScanning", at = @At(
            value = "INVOKE",
            target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;isThingStillScannable(Lnet/minecraft/world/entity/player/Player;Ljava/lang/Object;)Z"))
    private static boolean thaumaturgetweaks$allowContainerRescan(Player player, Object target) {
        return ContainerScanRules.allowsScan(player, target);
    }
}
