package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.recipe.IInfusionRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
public final class InfusionDisplay<R extends Recipe<?> & IInfusionRecipe> implements Display {
    private final RecipeHolder<R> holder;
    private final CategoryIdentifier<?> categoryId;

    InfusionDisplay(RecipeHolder<R> holder, CategoryIdentifier<?> categoryId) {
        this.holder = Objects.requireNonNull(holder, "holder");
        this.categoryId = Objects.requireNonNull(categoryId, "categoryId");
    }

    RecipeHolder<R> holder() {
        return holder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        IInfusionRecipe recipe = holder.value();
        list.add(ReiRecipeEntries.ingredientEntry(recipe.catalyst()));
        for (Ingredient component : recipe.components()) {
            list.add(ReiRecipeEntries.ingredientEntry(component));
        }
        for (AspectInstance aspect : recipe.aspects().sortedByAmount()) {
            list.add(ReiRecipeEntries.aspectVariants(aspect));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(
                EntryStack.of(VanillaEntryTypes.ITEM, holder.value().resultItem())));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return categoryId;
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(holder.id());
    }
}
