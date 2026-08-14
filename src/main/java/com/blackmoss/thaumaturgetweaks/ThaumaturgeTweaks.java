package com.blackmoss.thaumaturgetweaks;

import com.blackmoss.thaumaturgetweaks.curios.GogglesCurioHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(ThaumaturgeTweaks.MODID)
public class ThaumaturgeTweaks {
    public static final String MODID = "thaumaturgetweaks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumaturgeTweaks(IEventBus modEventBus) {
        // 揭示之护目镜优先放入饰品栏：仅在 Curios 已加载时注册，
        // 否则 GogglesCurioHandler 引用 Curios API 会导致类加载失败。
        if (ModList.get().isLoaded("curios")) {
            GogglesCurioHandler.register(modEventBus);
        }
    }
}
