// 容器方块扫描 payload 注册（MOD 事件总线）。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class ContainerScanPayloadRegistration {

    private ContainerScanPayloadRegistration() {
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ServerboundScanContainerPayload.TYPE,
                ServerboundScanContainerPayload.STREAM_CODEC,
                ContainerScanPayloads::handleScanContainer);
    }
}
