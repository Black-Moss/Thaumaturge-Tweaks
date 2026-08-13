// DataGen 入口：注册 lang、配方与 datamap 生成器。
// 注入 Thaumaturge 的 aspect datapack 注册表，使配方/datamap 的 lookupOrThrow 可解析。
package com.blackmoss.thaumaturgetweaks.data;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.data.lang.TweaksEnUsProvider;
import com.blackmoss.thaumaturgetweaks.data.lang.TweaksZhCnProvider;
import com.blackmoss.thaumaturgetweaks.data.model.TweaksModelProvider;
import com.blackmoss.thaumaturgetweaks.data.recipe.TweaksRecipeProvider;
import com.blackmoss.thaumaturgetweaks.data.tag.TweaksTagsProvider;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.data.worldgen.aspect.AspectBootstrap;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class TweaksDataGenerators {

    private TweaksDataGenerators() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        // 注入 Thaumaturge 的 aspect datapack 注册表，供配方/datamap provider 解析要素。
        RegistrySetBuilder registries = new RegistrySetBuilder()
                .add(IAspect.REGISTRY_KEY, AspectBootstrap::bootstrap);
        event.createDatapackRegistryObjects(registries);

        event.createProvider(TweaksEnUsProvider::new);
        event.createProvider(TweaksZhCnProvider::new);
        event.createProvider(TweaksRecipeProvider.Runner::new);
        event.createProvider(TweaksModelProvider::new);
        event.createProvider(TweaksTagsProvider::new);
//        event.createProvider(ItemAspectsProvider::new);
    }
}
