// DataGen 入口：注册 lang 生成器。
package com.blackmoss.thaumaturgetweaks.data;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.data.lang.TweaksZhCnProvider;
import com.blackmoss.thaumaturgetweaks.data.lang.TweaksEnUsProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class TweaksDataGenerators {

    private TweaksDataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(TweaksEnUsProvider::new);
        event.createProvider(TweaksZhCnProvider::new);
    }
}
