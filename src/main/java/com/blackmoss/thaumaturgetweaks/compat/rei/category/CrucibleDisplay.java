package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.recipe.crucible.CrucibleRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REI Display 承载一份熔锅配方，供搜索与列表匹配。
public final class CrucibleDisplay implements Display {

    private final RecipeHolder<CrucibleRecipe> holder;

    CrucibleDisplay(RecipeHolder<CrucibleRecipe> holder) {
        this.holder = holder;
    }

    RecipeHolder<CrucibleRecipe> holder() {
        return holder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        CrucibleRecipe recipe = holder.value();
        list.add(ReiRecipeEntries.ingredientEntry(recipe.catalyst()));
        for (AspectInstance instance : recipe.aspects().sortedByAmount()) {
            list.add(ReiRecipeEntries.aspectVariants(instance));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(
                EntryStack.of(VanillaEntryTypes.ITEM, CrucibleCategory.resultOf(holder.value()))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CrucibleCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(holder.id().identifier());
    }
}
