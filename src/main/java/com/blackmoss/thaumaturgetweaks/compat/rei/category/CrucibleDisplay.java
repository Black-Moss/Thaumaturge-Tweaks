package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.leclowndu93150.thaumaturge.content.recipe.crucible.CrucibleRecipe;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
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
        ItemStack catalyst = CrucibleCategory.firstStack(recipe.catalyst());
        list.add(catalyst.isEmpty()
                ? EntryIngredient.empty()
                : EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, catalyst)));
        for (AspectInstance instance : recipe.aspects().sortedByAmount()) {
            // 一个输入可对应要素图标、瓶装要素、要素水晶三种形式，全部作为候选。
            List<EntryStack<?>> variants = List.of(
                    EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, instance),
                    EntryStack.of(VanillaEntryTypes.ITEM, PhialItem.makeFilled(instance.aspect())),
                    EntryStack.of(VanillaEntryTypes.ITEM, EssentiaCrystalFactory.of(instance.aspect(), 1)));
            list.add(EntryIngredient.of(variants));
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
