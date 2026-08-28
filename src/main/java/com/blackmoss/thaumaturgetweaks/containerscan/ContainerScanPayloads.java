// 容器方块扫描请求的服务端处理器。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.leclowndu93150.thaumaturge.registry.TCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ContainerScanPayloads {

    // 距离校验的额外容差，抵消客户端与服务端刻不同步带来的偏差。
    private static final double REACH_TOLERANCE = 1.5;

    private ContainerScanPayloads() {
    }

    // 服务器：扫描目标容器方块内的所有物品。
    public static void handleScanContainer(ServerboundScanContainerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            BlockPos pos = payload.pos();
            if (!isHoldingThaumometer(player) || !withinReach(player, pos)) {
                return;
            }
            ContainerScanHelper.scanContents(player, pos);
        });
    }

    private static boolean isHoldingThaumometer(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        return mainHand.is(TCItems.THAUMOMETER.get()) || offHand.is(TCItems.THAUMOMETER.get());
    }

    private static boolean withinReach(ServerPlayer player, BlockPos pos) {
        Vec3 eyes = player.getEyePosition();
        double reach = player.blockInteractionRange() + REACH_TOLERANCE;
        return pos.getCenter().distanceTo(eyes) <= reach;
    }
}
