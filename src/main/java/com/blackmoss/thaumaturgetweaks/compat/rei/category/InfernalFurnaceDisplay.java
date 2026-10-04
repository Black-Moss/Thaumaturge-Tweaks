package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.leclowndu93150.thaumaturge.content.infernalfurnace.InfernalBonus;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class InfernalFurnaceDisplay implements Display {
    private final ItemStack input;
    private final ItemStack defaultOutput;
    private final List<InfernalBonus> bonuses;

    InfernalFurnaceDisplay(ItemStack input, ItemStack defaultOutput, List<InfernalBonus> bonuses) {
        this.input = input;
        this.defaultOutput = defaultOutput;
        this.bonuses = List.copyOf(bonuses);
    }

    public ItemStack input() {
        return input;
    }

    public ItemStack defaultOutput() {
        return defaultOutput;
    }

    public List<InfernalBonus> bonuses() {
        return bonuses;
    }

    static List<ItemStack> stacksOf(InfernalBonus bonus) {
        List<ItemStack> out = new ArrayList<>();
        for (var holder : bonus.items()) {
            out.add(new ItemStack(holder.value()));
        }
        return out;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        if (!input.isEmpty()) {
            list.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, input)));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        for (InfernalBonus bonus : bonuses) {
            for (ItemStack stack : stacksOf(bonus)) {
                list.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack)));
            }
        }
        return list;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return InfernalFurnaceCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(BuiltInRegistries.ITEM.getKey(input.getItem()));
    }
}
