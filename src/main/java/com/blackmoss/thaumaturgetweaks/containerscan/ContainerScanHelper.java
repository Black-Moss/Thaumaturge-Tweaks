// 容器方块扫描：扫描容器方块时连带扫描其内部物品。
// 本体 ScanningManager 已支持扫描 BlockPos 时遍历其物品栏，但仅在方块本身仍可扫描时才会触发；
// 方块一旦被扫描过，扫描流程根本无法启动，容器内的物品就永远扫不到。此处补齐该场景。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public final class ContainerScanHelper {

    // 单次扫描遍历的最大槽位数，防止超大容器拖垮服务端。
    private static final int MAX_SLOTS = 512;

    private ContainerScanHelper() {
    }

    // 该位置是否是可扫描内容的容器方块（不关心内容，双端通用）。
    public static boolean isContainerBlock(Level level, BlockPos pos) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(pos, "pos");
        return findHandler(level, pos) != null;
    }

    // 容器中是否还有玩家尚未扫描的物品（仅服务端有意义，客户端容器内容不同步）。
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

    // 扫描容器中所有仍可扫描的物品。
    // 只扫描仍可扫描的物品，避免与本体已有的容器扫描重复触发研究奖励。
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

    // 本体只查询 Direction.UP，这里遍历所有方向以兼容只向侧面暴露物品栏的容器。
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
