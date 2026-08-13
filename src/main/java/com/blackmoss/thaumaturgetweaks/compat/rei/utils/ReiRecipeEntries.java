// REI 类别共用的条目构造工具：消除各 category/display 中重复的 Ingredient 转换与要素变体逻辑。
package com.blackmoss.thaumaturgetweaks.compat.rei.utils;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import java.util.List;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public final class ReiRecipeEntries {

    private ReiRecipeEntries() {
    }

    // 取 Ingredient 的首个 ItemStack（REI 不直接支持 Ingredient）。
    public static ItemStack firstStack(Ingredient ingredient) {
        return ingredient.items()
                .findFirst()
                .map(holder -> new ItemStack(holder.value()))
                .orElse(ItemStack.EMPTY);
    }

    // 单个 ItemStack 作为 REI 输入条目，空则返回空条目。
    public static EntryIngredient itemEntry(ItemStack stack) {
        if (stack.isEmpty()) {
            return EntryIngredient.empty();
        }
        return EntryIngredient.of(EntryStack.of(VanillaEntryTypes.ITEM, stack));
    }

    // 单个 Ingredient 的首个物品作为 REI 输入条目。
    public static EntryIngredient ingredientEntry(Ingredient ingredient) {
        return itemEntry(firstStack(ingredient));
    }

    // 一个输入可对应要素图标、瓶装要素、要素水晶三种形式，全部作为候选。
    public static EntryIngredient aspectVariants(AspectInstance instance) {
        List<EntryStack<?>> variants = List.of(
                EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, instance),
                EntryStack.of(VanillaEntryTypes.ITEM, PhialItem.makeFilled(instance.aspect())),
                EntryStack.of(VanillaEntryTypes.ITEM, EssentiaCrystalFactory.of(instance.aspect(), 1)));
        return EntryIngredient.of(variants);
    }
}
