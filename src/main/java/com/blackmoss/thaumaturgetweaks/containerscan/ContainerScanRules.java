package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanTarget;
import com.leclowndu93150.thaumaturge.api.research.scan.ScannedBlock;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public final class ContainerScanRules {
    private ContainerScanRules() {
    }

    public static boolean allowsScan(Player player, @Nullable ScanTarget target) {
        Objects.requireNonNull(player, "player");
        if (target != null && ScanningManager.isStillScannable(player, target)) {
            return true;
        }
        if (!(target instanceof ScannedBlock(BlockPos pos))) {
            return false;
        }
        Level level = player.level();
        return level.isClientSide()
                ? ContainerScanHelper.isContainerBlock(level, pos)
                : ContainerScanHelper.hasScannableContents(player, pos);
    }
}