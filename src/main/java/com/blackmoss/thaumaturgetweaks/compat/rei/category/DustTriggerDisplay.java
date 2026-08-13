package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.leclowndu93150.thaumaturge.api.recipe.DustTrigger;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerSimpleRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerTagRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// REI Display 承载一份尘触发配方，供搜索与列表匹配。
public final class DustTriggerDisplay implements Display {

    private final RecipeHolder<DustTrigger> holder;

    DustTriggerDisplay(RecipeHolder<DustTrigger> holder) {
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
        DustTrigger recipe = holder.value();
        if (recipe instanceof DustTriggerSimpleRecipe simple) {
            list.add(EntryIngredient.of(
                    EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(simple.target()))));
        } else if (recipe instanceof DustTriggerTagRecipe tag) {
            // 方块标签：把标签内所有方块作为候选输入。
            List<EntryStack<?>> stacks = new ArrayList<>();
            for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag.targetTag())) {
                ItemStack stack = new ItemStack(blockHolder.value());
                if (!stack.isEmpty()) {
                    stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack));
                }
            }
            list.add(EntryIngredient.of(stacks));
        }
        return list;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        ItemStack result = DustTriggerCategory.resultStack(holder.value());
        if (result.isEmpty()) {
            return List.of();
        }
        return List.of(EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, result)));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return DustTriggerCategory.ID;
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
