package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;

public final class ContainerScanRules {
    private ContainerScanRules() {
    }

    public static boolean allowsScan(Player player, Object target) {
        Objects.requireNonNull(player, "player");
        if (ScanningManager.isThingStillScannable(player, target)) {
            return true;
        }
        if (!(target instanceof BlockPos pos)) {
            return false;
        }
        Level level = player.level();
        return level.isClientSide()
                ? ContainerScanHelper.isContainerBlock(level, pos)
                : ContainerScanHelper.hasScannableContents(player, pos);
    }
}
