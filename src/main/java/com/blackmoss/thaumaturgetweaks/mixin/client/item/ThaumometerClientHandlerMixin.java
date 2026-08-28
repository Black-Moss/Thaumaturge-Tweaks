// 客户端魔导透镜扫描：允许对容器方块完成扫描，并在扫描完成时请求服务器扫描容器内的物品。
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
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThaumometerClientHandler.class)
public abstract class ThaumometerClientHandlerMixin {

    // 一次按住使用只请求一次容器扫描。
    private static boolean thaumaturgetweaks$requestSent;

    private ThaumometerClientHandlerMixin() {
    }

    // 在本体判定是否中断扫描之前请求容器扫描；此时玩家仍处于使用状态。
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
        if (level == null) {
            return;
        }
        if (ThaumometerItem.resolveTarget(level, player) instanceof BlockPos pos
                && ContainerScanHelper.isContainerBlock(level, pos)) {
            ClientPacketDistributor.sendToServer(new ServerboundScanContainerPayload(pos));
            thaumaturgetweaks$requestSent = true;
        }
    }

    // 扫描过程中不要因为容器方块本身已扫描过就中断。
    @Redirect(
            method = "tickScanning",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;isThingStillScannable(Lnet/minecraft/world/entity/player/Player;Ljava/lang/Object;)Z"))
    private static boolean thaumaturgetweaks$allowContainerRescan(Player player, @Nullable Object target) {
        return ContainerScanRules.allowsScan(player, target);
    }
}
