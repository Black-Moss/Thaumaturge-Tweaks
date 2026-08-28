// 放宽魔导透镜对容器方块的扫描前置条件：
// 本体要求目标本身仍可扫描，否则无法"起手"也无法完成扫描；
// 容器方块被扫描过之后，里面新放进去的物品就再也扫不到了。这里在容器仍有可扫描内容时放行。
package com.blackmoss.thaumaturgetweaks.mixin;

import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanRules;
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

    // 起手扫描：允许对有可扫描内容的容器方块按住使用。
    // 注意：注入点依赖父模组 Thaumaturge 0.1.4 的 ThaumometerItem#beginScan(Level, Player, InteractionHand)。
    @Redirect(
            method = "beginScan",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;isThingStillScannable(Lnet/minecraft/world/entity/player/Player;Ljava/lang/Object;)Z"))
    private static boolean thaumaturgetweaks$allowContainerBegin(Player player, @Nullable Object target) {
        return ContainerScanRules.allowsScan(player, target);
    }

    // 完成扫描：允许对有可扫描内容的容器方块结束扫描并结算。
    @Redirect(
            method = "releaseUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/leclowndu93150/thaumaturge/api/research/scan/ScanningManager;isThingStillScannable(Lnet/minecraft/world/entity/player/Player;Ljava/lang/Object;)Z"))
    private static boolean thaumaturgetweaks$allowContainerRelease(Player player, @Nullable Object target) {
        return ContainerScanRules.allowsScan(player, target);
    }
}
