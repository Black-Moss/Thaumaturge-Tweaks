package com.blackmoss.thaumaturgetweaks;

import com.blackmoss.thaumaturgetweaks.containerscan.ContainerScanPayloads;
import com.blackmoss.thaumaturgetweaks.containerscan.ServerboundScanContainerPayload;
import com.blackmoss.thaumaturgetweaks.compat.curios.GogglesCurioHandler;
import com.blackmoss.thaumaturgetweaks.data.lang.EnUsProvider;
import com.blackmoss.thaumaturgetweaks.data.lang.ZhCnProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
@Mod(ThaumaturgeTweaks.MODID)
public class ThaumaturgeTweaks {
    public static final String MODID = "thaumaturgetweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumaturgeTweaks(IEventBus modEventBus) {
        if (ModList.get().isLoaded("curios")) {
            GogglesCurioHandler.register(modEventBus);
        }
    }

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ServerboundScanContainerPayload.TYPE,
                ServerboundScanContainerPayload.STREAM_CODEC,
                ContainerScanPayloads::handleScanContainer);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(EnUsProvider::new);
        event.createProvider(ZhCnProvider::new);
    }
}
