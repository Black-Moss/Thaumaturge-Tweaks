package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.blackmoss.thaumaturgetweaks.compat.trinkets.client.CuriosityBandTrinketRenderer;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import eu.pb4.trinkets.api.client.TrinketRendererRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public final class CuriosityBandTrinketHandler {
    private CuriosityBandTrinketHandler() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(CuriosityBandTrinketHandler::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        TrinketRendererRegistry.registerRenderer(TTItems.CURIOSITY_BAND.get(), new CuriosityBandTrinketRenderer());
    }
}
