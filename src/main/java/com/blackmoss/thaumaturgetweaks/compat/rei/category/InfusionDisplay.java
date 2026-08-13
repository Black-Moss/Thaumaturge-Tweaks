package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.recipe.IInfusionRecipe;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REI Display 承载一份注魔配方，供搜索与列表匹配。
public final class InfusionDisplay<R extends Recipe<?> & IInfusionRecipe> implements Display {

    private final RecipeHolder<R> holder;
    private final CategoryIdentifier<?> categoryId;

    InfusionDisplay(RecipeHolder<R> holder, CategoryIdentifier<?> categoryId) {
        this.holder = holder;
        this.categoryId = categoryId;
    }

    RecipeHolder<R> holder() {
        return holder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        IInfusionRecipe recipe = holder.value();
        ItemStack catalyst = InfusionCategory.firstStack(recipe.catalyst());
        list.add(catalyst.isEmpty()
                ? EntryIngredient.empty()
                : EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, catalyst)));
        for (Ingredient component : recipe.components()) {
            ItemStack stack = InfusionCategory.firstStack(component);
            list.add(stack.isEmpty()
                    ? EntryIngredient.empty()
                    : EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack)));
        }
        for (AspectInstance aspect : recipe.aspects().sortedByAmount()) {
            // 一个输入可对应要素图标、瓶装要素、要素水晶三种形式，全部作为候选。
            List<EntryStack<?>> variants = List.of(
                    EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, aspect),
                    EntryStack.of(VanillaEntryTypes.ITEM, PhialItem.makeFilled(aspect.aspect())),
                    EntryStack.of(VanillaEntryTypes.ITEM, EssentiaCrystalFactory.of(aspect.aspect(), 1)));
            list.add(EntryIngredient.of(variants));
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
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.of(holder.id().identifier());
    }
}
