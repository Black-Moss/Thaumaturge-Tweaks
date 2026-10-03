package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.client.AspectSlotAnnotations;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectVesselItemEntryRenderer;
import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.TCAspects;
import com.leclowndu93150.thaumaturge.client.screen.casters.FocalManipulatorScreen;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerSimpleRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerTagRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import com.leclowndu93150.thaumaturge.registry.TCRecipeTypes;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRendererRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@REIPluginClient
public final class ReiThaumaturgePlugin implements REIClientPlugin {

    @Override
    @SuppressWarnings("unchecked")
    public Class<REIClientPlugin> getPluginProviderClass() {
        return (Class<REIClientPlugin>) (Class<?>) ReiThaumaturgePlugin.class;
    }
    private static void registerAspectInfoPages() {
        RegistryAccess access = clientRegistryAccess();
        if (access == null) {
            return;
        }
        BuiltinClientPlugin plugin = BuiltinClientPlugin.getInstance();
        for (Holder.Reference<IAspect> holder : ResearchUtils.listAll(access, IAspect.REGISTRY_KEY)) {
            EntryStack<?> entry = EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(holder, 1));
            plugin.registerInformation(
                    entry,
                    AspectComponents.shortName(holder),
                    aspect -> List.of(AspectComponents.description(holder)));
        }
    }

    @Nullable
    private static Holder<IAspect> pickIconAspect() {
        RegistryAccess access = clientRegistryAccess();
        if (access == null) {
            return null;
        }
        List<Holder.Reference<IAspect>> all = ResearchUtils.listAll(access, IAspect.REGISTRY_KEY);
        for (Holder.Reference<IAspect> holder : all) {
            if (holder.is(TCAspects.PRAECANTATIO)) {
                return holder;
            }
        }
        return all.isEmpty() ? null : all.getFirst();
    }

    @Nullable
    private static RegistryAccess clientRegistryAccess() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        return level == null
                ? null
                : level.registryAccess();
    }

    private static <R extends Recipe<?>> void forEachTypedRecipe(
            ClientLevel level, RecipeType<R> type, Consumer<RecipeHolder<R>> consumer) {
        // 1.21.1 的签名是 <I extends RecipeInput, T extends Recipe<I>> getAllRecipesFor(RecipeType<T>)，
        // 无法直接用 R extends Recipe<?> 推导，这里按原始类型调用再强转回去。
        //noinspection rawtypes,unchecked
        List<RecipeHolder<R>> holders = (List<RecipeHolder<R>>) (List) level.getRecipeManager()
                .getAllRecipesFor((RecipeType) type);
        for (RecipeHolder<R> holder : holders) {
            consumer.accept(holder);
        }
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        RegistryAccess registryAccess = clientRegistryAccess();
        if (registryAccess == null) {
            return;
        }
        List<EntryStack<?>> stacks = new ArrayList<>();
        for (Holder.Reference<IAspect> holder : ResearchUtils.listAll(registryAccess, IAspect.REGISTRY_KEY)) {
            stacks.add(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(holder, 1)));
        }
        registry.addEntries(stacks);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void registerEntryRenderers(EntryRendererRegistry registry) {
        registry.register(VanillaEntryTypes.ITEM, (entry, currentRenderer) -> {
            ItemStack stack = entry.getValue();
            if (AspectSlotAnnotations.isAspectVessel(stack)) {
                return new AspectVesselItemEntryRenderer(currentRenderer);
            }
            return currentRenderer;
        });
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(FocalManipulatorScreen.class, screen -> screen.jeiExtraAreas().stream()
                .map(area -> new Rectangle(area.getX(), area.getY(), area.getWidth(), area.getHeight()))
                .toList());
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ArcaneWorkbenchCategory());
        registry.add(new CrucibleCategory());
        registry.add(new InfusionCategory<>(InfusionCategory.INFUSION_ID, "recipe.type.infusion"));
        registry.add(new InfusionCategory<>(InfusionCategory.ENCHANTMENT_ID, "recipe.type.infusion_enchantment"));
        registry.add(new InfusionCategory<>(InfusionCategory.RUNIC_ID, "recipe.type.runic_augment"));
        registry.add(new DustTriggerCategory());
        registry.add(new MultiblockCategory());
        registry.add(new AspectCompositionCategory(pickIconAspect()));
        registry.add(new AspectFromStacksCategory());

        // 催化剂（工作台）。
        registry.addWorkstations(ArcaneWorkbenchCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.ARCANE_WORKBENCH.get())));
        registry.addWorkstations(CrucibleCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.CRUCIBLE.get())));
        registry.addWorkstations(InfusionCategory.INFUSION_ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.INFUSION_MATRIX.get())));
        registry.addWorkstations(InfusionCategory.ENCHANTMENT_ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.INFUSION_MATRIX.get())));
        registry.addWorkstations(InfusionCategory.RUNIC_ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.INFUSION_MATRIX.get())));
        registry.addWorkstations(DustTriggerCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get())));
        registry.addWorkstations(MultiblockCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get())));
        registry.addWorkstations(AspectCompositionCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.THAUMONOMICON.get())));
        registry.addWorkstations(AspectFromStacksCategory.ID,
                EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.THAUMONOMICON.get())));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        forEachTypedRecipe(level, TCRecipeTypes.ARCANE.get(), holder -> registry.add(new ArcaneWorkbenchDisplay(holder)));
        forEachTypedRecipe(level, TCRecipeTypes.CRUCIBLE.get(), holder -> registry.add(new CrucibleDisplay(holder)));
        forEachTypedRecipe(level, TCRecipeTypes.INFUSION.get(), holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.INFUSION_ID)));
        forEachTypedRecipe(level, TCRecipeTypes.INFUSION_ENCHANTMENT.get(), holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.ENCHANTMENT_ID)));
        forEachTypedRecipe(level, TCRecipeTypes.RUNIC_AUGMENT.get(), holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.RUNIC_ID)));
        forEachTypedRecipe(level, TCRecipeTypes.DUST_TRIGGER.get(), holder -> {
            if (holder.value() instanceof DustTriggerSimpleRecipe || holder.value() instanceof DustTriggerTagRecipe) {
                registry.add(new DustTriggerDisplay(holder));
            } else if (holder.value() instanceof DustTriggerMultiblockRecipe) {
                registry.add(new MultiblockDisplay(holder));
            }
        });

        RegistryAccess access = level.registryAccess();
        for (AspectCompositionDisplay display :
                AspectCompositionCategory.collect(ResearchUtils.listAll(access, IAspect.REGISTRY_KEY))) {
            registry.add(display);
        }

        for (AspectFromStacksDisplay display : AspectFromStacksCategory.collectAll(access)) {
            registry.add(display);
        }

        registerAspectInfoPages();
    }
}
