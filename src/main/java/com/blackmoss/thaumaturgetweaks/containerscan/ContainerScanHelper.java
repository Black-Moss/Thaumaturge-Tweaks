package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

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
        ResourceHandler<ItemResource> handler = findHandler(player.level(), pos);
        if (handler == null) {
            return false;
        }
        for (int index = 0; index < handler.size() && index < MAX_SLOTS; index++) {
            ItemResource resource = handler.getResource(index);
            if (resource.isEmpty()) {
                continue;
            }
            if (ScanningManager.isThingStillScannable(player, resource.toStack(handler.getAmountAsInt(index)))) {
                return true;
            }
        }
        return false;
    }

    public static void scanContents(Player player, BlockPos pos) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(pos, "pos");
        ResourceHandler<ItemResource> handler = findHandler(player.level(), pos);
        if (handler == null) {
            return;
        }
        for (int index = 0; index < handler.size() && index < MAX_SLOTS; index++) {
            ItemResource resource = handler.getResource(index);
            if (resource.isEmpty()) {
                continue;
            }
            ItemStack stack = resource.toStack(handler.getAmountAsInt(index));
            if (!ScanningManager.isThingStillScannable(player, stack)) {
                continue;
            }
            ScanningManager.scanTheThing(player, stack);
        }
    }

    private static @Nullable ResourceHandler<ItemResource> findHandler(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            ResourceHandler<ItemResource> handler = level.getCapability(Capabilities.Item.BLOCK, pos, direction);
            if (handler != null) {
                return handler;
            }
        }
        return level.getCapability(Capabilities.Item.BLOCK, pos, null);
    }
}
