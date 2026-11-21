package com.blackmoss.thaumaturgetweaks.event;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class CreativeTabTweaks {
    private static final ResourceKey<CreativeModeTab> THAUMATURGE_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, TTIds.rl("thaumaturge"));

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (!THAUMATURGE_TAB.equals(event.getTabKey())) {
            return;
        }
        event.removeIf(
                stack -> stack.is(TTItems.ESSENTIA_CRYSTAL) || stack.is(TTItems.PHIAL),
                CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
