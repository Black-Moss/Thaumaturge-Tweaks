package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class ThaumaturgeTweaksClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModList.get()
                .getModContainerById(ThaumaturgeTweaks.MODID)
                .ifPresent(container ->
                        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new));
    }
}
