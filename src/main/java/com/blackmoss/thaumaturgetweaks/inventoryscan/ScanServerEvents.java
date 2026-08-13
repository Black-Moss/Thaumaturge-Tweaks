// 服务器端：玩家登录后告知客户端本模组可用，客户端才会上报扫描请求。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class ScanServerEvents {

    private ScanServerEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, ClientboundScanAvailablePayload.INSTANCE);
        }
    }
}
