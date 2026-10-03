package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AspectFromStacksDisplay implements Display {
    private final AspectFromStacksCategory.Wrapper wrapper;

    AspectFromStacksDisplay(AspectFromStacksCategory.Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    AspectFromStacksCategory.Wrapper wrapper() {
        return wrapper;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        for (ItemStack stack : wrapper.stacks()) {
            list.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack)));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(EntryStack.of(
                AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(wrapper.aspect(), 1))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AspectFromStacksCategory.ID;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.empty();
    }
}
