package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanTarget;
import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ContainerScanHelper {
    private static final int MAX_SLOTS = 512;

    private ContainerScanHelper() {
    }

    public static boolean isContainerBlock(Level level, BlockPos pos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        return findHandler(level, pos) != null;
    }

    public static boolean hasScannableContents(Player player, BlockPos pos) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pos, "pos");
        IItemHandler handler = findHandler(player.level(), pos);
        if (handler == null) {
            return false;
        }
        for (int index = 0; index < handler.getSlots() && index < MAX_SLOTS; index++) {
            ItemStack stack = handler.getStackInSlot(index);
            if (stack.isEmpty()) {
                continue;
            }
            if (ScanningManager.isStillScannable(player, ScanTarget.stack(stack))) {
                return true;
            }
        }
        return false;
    }

    public static void scanContents(Player player, BlockPos pos) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pos, "pos");
        IItemHandler handler = findHandler(player.level(), pos);
        if (handler == null) {
            return;
        }
        List<ScanTarget> targets = new ArrayList<>();
        for (int index = 0; index < handler.getSlots() && index < MAX_SLOTS; index++) {
            ItemStack stack = handler.getStackInSlot(index);
            if (stack.isEmpty()) {
                continue;
            }
            ScanTarget target = ScanTarget.stack(stack);
            if (!ScanningManager.isStillScannable(player, target)) {
                continue;
            }
            targets.add(target);
        }
        if (!targets.isEmpty()) {
            ScanningManager.scanSilently(player, targets);
        }
    }

    private static @Nullable IItemHandler findHandler(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction);
            if (handler != null) {
                return handler;
            }
        }
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
    }
}
