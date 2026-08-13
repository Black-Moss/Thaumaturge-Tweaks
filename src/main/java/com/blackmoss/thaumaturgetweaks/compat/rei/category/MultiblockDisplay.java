package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.leclowndu93150.thaumaturge.api.recipe.BlueprintSource;
import com.leclowndu93150.thaumaturge.api.recipe.DustTrigger;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
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
import java.util.Map;
import java.util.Optional;

// REI Display 承载一份多方块尘触发配方，供搜索与列表匹配。
public final class MultiblockDisplay implements Display {

    private final RecipeHolder<DustTrigger> holder;

    MultiblockDisplay(RecipeHolder<DustTrigger> holder) {
        this.holder = holder;
    }

    RecipeHolder<DustTrigger> holder() {
        return holder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> list = new ArrayList<>();
        list.add(EntryIngredient.of(
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get()))));
        for (Map.Entry<BlueprintSource, Integer> entry : MultiblockCategory.sortedBlueprintSources(
                (DustTriggerMultiblockRecipe) holder.value())) {
            List<EntryStack<?>> stacks = new ArrayList<>();
            for (ItemStack stack : entry.getKey().getRepresentations()) {
                stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(entry.getValue())));
            }
            list.add(EntryIngredient.of(stacks));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        ItemStack result = ((DustTriggerMultiblockRecipe) holder.value()).result();
        if (result.isEmpty()) {
            return List.of();
        }
        return List.of(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, result)));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MultiblockCategory.ID;
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
