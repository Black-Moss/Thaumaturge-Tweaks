package com.blackmoss.thaumaturgetweaks.event;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID)
public final class CreativeTabTweaks {
    private static final ResourceKey<CreativeModeTab> THAUMATURGE_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, TTIds.rl("thaumaturge"));

    private CreativeTabTweaks() {
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (!THAUMATURGE_TAB.equals(event.getTabKey())) {
            return;
        }
        // 1.21.1 的 Output 没有 removeIf，先收集再按物品移除
        List<ItemStack> moved = new ArrayList<>();
        for (ItemStack stack : event.getParentEntries()) {
            if (stack.getItem() == TTItems.ESSENTIA_CRYSTAL.get() || stack.getItem() == TTItems.PHIAL.get()) {
                moved.add(stack);
            }
        }
        for (ItemStack stack : moved) {
            event.remove(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
