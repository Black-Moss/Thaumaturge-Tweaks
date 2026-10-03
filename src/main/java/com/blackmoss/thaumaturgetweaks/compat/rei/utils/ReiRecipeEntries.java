package com.blackmoss.thaumaturgetweaks.compat.rei.utils;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public final class ReiRecipeEntries {
    private ReiRecipeEntries() {
    }

    public static ItemStack firstStack(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        return items.length == 0 ? ItemStack.EMPTY : items[0];
    }

    public static EntryIngredient itemEntry(ItemStack stack) {
        if (stack.isEmpty()) {
            return EntryIngredient.empty();
        }
        return EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack));
    }

    public static EntryIngredient ingredientEntry(Ingredient ingredient) {
        return itemEntry(firstStack(ingredient));
    }

    public static EntryIngredient aspectVariants(AspectInstance instance) {
        List<EntryStack<?>> variants = List.of(
                EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, instance),
                EntryStack.of(VanillaEntryTypes.ITEM, PhialItem.makeFilled(instance.aspect())),
                EntryStack.of(VanillaEntryTypes.ITEM, EssentiaCrystalFactory.of(instance.aspect(), 1)));
        return EntryIngredient.of(variants);
    }
}
