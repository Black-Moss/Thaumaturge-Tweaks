package com.blackmoss.thaumaturgetweaks;

import com.blackmoss.thaumaturgetweaks.registry.TweaksItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ThaumaturgeTweaks.MODID)
public class ThaumaturgeTweaks {
    public static final String MODID = "thaumaturgetweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumaturgeTweaks(IEventBus modEventBus) {
        TweaksItems.register(modEventBus);
    }
}
