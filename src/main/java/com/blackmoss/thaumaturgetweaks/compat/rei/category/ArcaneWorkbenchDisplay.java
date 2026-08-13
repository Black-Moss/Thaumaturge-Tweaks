package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneShapedCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneShapelessCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REI Display 承载一份奥术配方，供搜索与列表匹配。
public final class ArcaneWorkbenchDisplay implements Display {

    private final RecipeHolder<ArcaneCraftingRecipe> holder;

    ArcaneWorkbenchDisplay(RecipeHolder<ArcaneCraftingRecipe> holder) {
        this.holder = holder;
    }

    // 单个 Ingredient 的首个 ItemStack 作为 REI 输入条目。
    private static EntryIngredient itemEntry(Ingredient ingredient) {
        return ReiRecipeEntries.ingredientEntry(ingredient);
    }

    RecipeHolder<ArcaneCraftingRecipe> holder() {
        return holder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        ArcaneCraftingRecipe recipe = holder.value();
        if (recipe instanceof ArcaneShapedCraftingRecipe shaped) {
            for (Optional<Ingredient> opt : shaped.getIngredients()) {
                opt.ifPresent(ingredient -> list.add(itemEntry(ingredient)));
            }
        } else if (recipe instanceof ArcaneShapelessCraftingRecipe shapeless) {
            for (Ingredient ingredient : shapeless.ingredients()) {
                list.add(itemEntry(ingredient));
            }
        }
        for (AspectInstance instance : recipe.getCrystals().entries()) {
            ItemStack crystal = EssentiaCrystalFactory.of(instance.aspect(), instance.amount());
            list.add(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, crystal)));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(
                EntryStack.of(VanillaEntryTypes.ITEM, ArcaneWorkbenchCategory.resultOf(holder.value()))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ArcaneWorkbenchCategory.ID;
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
