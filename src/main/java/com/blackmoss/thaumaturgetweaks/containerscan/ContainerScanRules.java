// 容器方块扫描的放行规则：判断一次魔导透镜扫描是否应当被允许。
// 本体在目标"已无可学内容"时会直接中断扫描，导致已扫描过的容器方块内部物品永远扫不到；
// 这里在目标是"仍有可扫描内容的容器方块"时放行，让扫描流程可以正常启动并完成。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class ContainerScanRules {

    private ContainerScanRules() {
    }

    // 目标仍可扫描，或目标是有可扫描内容的容器方块时，允许本次扫描。
    public static boolean allowsScan(Player player, @Nullable Object target) {
        Objects.requireNonNull(player, "player");
        if (ScanningManager.isThingStillScannable(player, target)) {
            return true;
        }
        if (!(target instanceof BlockPos pos)) {
            return false;
        }
        Level level = player.level();
        // 客户端容器内容不同步，只能判断"是否是容器方块"；服务端可以判断是否真有未扫描物品。
        return level.isClientSide()
                ? ContainerScanHelper.isContainerBlock(level, pos)
                : ContainerScanHelper.hasScannableContents(player, pos);
    }
}
