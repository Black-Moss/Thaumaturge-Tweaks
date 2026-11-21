package com.blackmoss.thaumaturgetweaks.registry;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.wands.WandCap;
import com.leclowndu93150.thaumaturge.api.wands.WandRod;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import com.leclowndu93150.thaumaturge.content.wands.ItemWand;
import com.leclowndu93150.thaumaturge.content.wands.WandVisHelper;
import com.leclowndu93150.thaumaturge.registry.TTItems;
import com.leclowndu93150.thaumaturge.registry.TTWandParts;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TTCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThaumaturgeTweaks.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ASPECT_ITEMS;
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WANDS;

    private static final long ICON_CYCLE_MILLIS = 1000L;
    private static volatile List<ItemStack> essentiaIconCycle = List.of();
    private static volatile List<ItemStack> wandIconCycle = List.of();

    static {
        ASPECT_ITEMS = CREATIVE_MODE_TABS.register("aspect_items",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.thaumaturgetweaks.aspect_items"))
                        .icon(TTCreativeModeTabs::essentiaIcon)
                        .withTabsBefore(TTIds.rl("thaumaturge"))
                        .displayItems((parameters, output) -> {
                            HolderLookup.RegistryLookup<IAspect> aspectRegistry = parameters.holders().lookupOrThrow(IAspect.REGISTRY_KEY);
                            List<Holder.Reference<IAspect>> aspects = aspectRegistry
                                    .listElements()
                                    .sorted(Comparator.comparing((h) -> !h.value().isPrimal()))
                                    .toList();

                            List<ItemStack> crystals = new ArrayList<>(aspects.size());
                            for (Holder<IAspect> aspect : aspects) {
                                ItemStack crystal = EssentiaCrystalFactory.of(aspect);
                                crystals.add(crystal);
                                output.accept(crystal);
                            }
                            essentiaIconCycle = List.copyOf(crystals);

                            output.accept(TTItems.PHIAL.get());

                            for (Holder<IAspect> aspect : aspects) {
                                output.accept(PhialItem.makeFilled(aspect));
                            }
                        }).build());
        WANDS = CREATIVE_MODE_TABS.register("wands",
                () -> CreativeModeTab.builder()
                        .title(Component.translatable("itemGroup.thaumaturgetweaks.wands"))
                        .icon(TTCreativeModeTabs::wandIcon)
                        .withTabsBefore(ThaumaturgeTweaks.identifier("aspect_items"))
                        .displayItems((parameters, output) -> {
                            List<ItemStack> wands = new ArrayList<>();
                            for (WandRod rod : TTWandParts.rods()) {
                                for (WandCap cap : TTWandParts.caps()) {
                                    ItemStack wand = chargedWand(cap, rod, false);
                                    wands.add(wand);
                                    output.accept(wand);
                                }
                                if (!rod.staff()) {
                                    for (WandCap cap : TTWandParts.caps()) {
                                        ItemStack sceptre = chargedWand(cap, rod, true);
                                        wands.add(sceptre);
                                        output.accept(sceptre);
                                    }
                                }
                            }
                            wandIconCycle = List.copyOf(wands);
                        }).build());
    }

    private static ItemStack essentiaIcon() {
        List<ItemStack> icons = essentiaIconCycle;
        if (icons.isEmpty()) {
            return new ItemStack(TTItems.ESSENTIA_CRYSTAL.get());
        }
        return pickIcon(icons);
    }

    private static ItemStack wandIcon() {
        List<ItemStack> icons = wandIconCycle;
        if (icons.isEmpty()) {
            return chargedWand(TTWandParts.CAP_IRON.get(), TTWandParts.ROD_WOOD.get(), false);
        }
        return pickIcon(icons);
    }

    private static ItemStack pickIcon(List<ItemStack> icons) {
        int index = (int) (System.currentTimeMillis() / ICON_CYCLE_MILLIS % icons.size());
        return icons.get(index).copy();
    }

    private static ItemStack chargedWand(WandCap cap, WandRod rod, boolean sceptre) {
        ItemStack stack = ItemWand.create(TTItems.WAND.get(), cap, rod, sceptre);
        WandVisHelper.fill(stack);
        return stack;
    }

    public static boolean usesDynamicIcon(CreativeModeTab tab) {
        return tab == ASPECT_ITEMS.get() || tab == WANDS.get();
    }
}
