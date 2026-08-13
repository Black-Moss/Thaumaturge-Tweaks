// 物品栏扫描 payload 注册（MOD 事件总线）。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class InventoryScanPayloadRegistration {

    private InventoryScanPayloadRegistration() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ServerboundScanSlotPayload.TYPE,
                ServerboundScanSlotPayload.STREAM_CODEC,
                InventoryScanPayloads::handleScanSlot);
        registrar.playToServer(
                ServerboundScanSelfPayload.TYPE,
                ServerboundScanSelfPayload.STREAM_CODEC,
                InventoryScanPayloads::handleScanSelf);
        registrar.playToClient(
                ClientboundScanAvailablePayload.TYPE,
                ClientboundScanAvailablePayload.STREAM_CODEC,
                InventoryScanPayloads::handleScanAvailable);
    }
}
