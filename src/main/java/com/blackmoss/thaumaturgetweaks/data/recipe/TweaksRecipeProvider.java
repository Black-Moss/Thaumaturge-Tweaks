package com.blackmoss.thaumaturgetweaks.data.recipe;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.registry.TweaksItems;
import com.blackmoss.thaumaturgetweaks.registry.TweaksTags;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.TCAspects;
import com.leclowndu93150.thaumaturge.data.recipe.builders.InfusionRecipeBuilder;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import committee.nova.mods.avaritia.init.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.NonNull;

public final class TweaksRecipeProvider extends RecipeProvider {

    private TweaksRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        HolderGetter<IAspect> aspects = registries.lookupOrThrow(IAspect.REGISTRY_KEY);
        HolderGetter<Item> items = registries.lookupOrThrow(Registries.ITEM);

        // 极元始珍珠：8 颗元始珍珠注魔。
        new InfusionRecipeBuilder(
                        aspects,
                        RecipeCategory.MISC,
                        new ItemStackTemplate(TweaksItems.EXTREMELY_PRIMORDIAL_PEARL.get()),
                        Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .component(Ingredient.of(TCItems.PRIMORDIAL_PEARL.get()))
                .aspect(TCAspects.ALIENIS, 32)
                .aspect(TCAspects.PERDITIO, 16)
                .instability(4)
                .unlockedBy("has_primordial_peral", has(TCItems.PRIMORDIAL_PEARL.get()))
                .save(output, ThaumaturgeTweaks.MODID + ":extremely_primordial_pearl");

        // 阿卡西记录：任意珍宝（#thaumaturge:curios）+ 4 无尽之锭注魔。
        new InfusionRecipeBuilder(
                        aspects,
                        RecipeCategory.MISC,
                        new ItemStackTemplate(TweaksItems.AKASHIC_RECORDS.get()),
                        Ingredient.of(items.getOrThrow(TweaksTags.CURIOS)))
                .component(Ingredient.of(ModItems.infinity_ingot.get()))
                .component(Ingredient.of(ModItems.infinity_ingot.get()))
                .component(Ingredient.of(ModItems.infinity_ingot.get()))
                .component(Ingredient.of(ModItems.infinity_ingot.get()))
                .aspect(TCAspects.COGNITIO, 64)
                .aspect(TCAspects.PRAECANTATIO, 64)
                .aspect(TCAspects.ALIENIS, 32)
                .instability(4)
                .unlockedBy("has_infinity_ingot", has(ModItems.infinity_ingot.get()))
                .save(output, ThaumaturgeTweaks.MODID + ":akashic_record");
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider provider, @NonNull RecipeOutput output) {
            return new TweaksRecipeProvider(provider, output);
        }

        @Override
        public @NonNull String getName() {
            return "Thaumaturge Tweaks Recipes";
        }
    }
}
