// REI 兼容插件主入口：注册 Thaumaturge 的 7 类配方/信息类别、催化剂与条目。
// 位于 category 包，以便访问同包内包级可见的 Display 类。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.api.aspect.TCAspects;
import com.leclowndu93150.thaumaturge.client.recipes.TCClientRecipes;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerSimpleRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerTagRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import com.leclowndu93150.thaumaturge.registry.TCRecipeTypes;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
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
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@REIPluginClient
public final class ReiThaumaturgePlugin implements REIClientPlugin {

    // 每个要素注册一个信息页，展示其描述文本。
    private static void registerAspectInfoPages() {
        RegistryAccess access = clientRegistryAccess();
        if (access == null) {
            return;
        }
        Optional<Registry<IAspect>> registryOpt = access.lookup(IAspect.REGISTRY_KEY);
        if (registryOpt.isEmpty()) {
            return;
        }
        Registry<IAspect> aspectRegistry = registryOpt.get();
        BuiltinClientPlugin plugin = BuiltinClientPlugin.getInstance();
        for (Holder.Reference<IAspect> holder : aspectRegistry.listElements().toList()) {
            EntryStack<?> entry = EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(holder, 1));
            plugin.registerInformation(
                    entry,
                    AspectComponents.shortName(holder),
                    _ -> List.of(AspectComponents.description(holder)));
        }
    }

    // 选取一个稳定的要素作为类别图标，缺省回退盐晶。
    @Nullable
    private static Holder<IAspect> pickIconAspect() {
        RegistryAccess access = clientRegistryAccess();
        if (access != null) {
            Optional<Registry<IAspect>> registryOpt = access.lookup(IAspect.REGISTRY_KEY);
            if (registryOpt.isPresent()) {
                Registry<IAspect> registry = registryOpt.get();
                Optional<Holder.Reference<IAspect>> stable = registry.get(TCAspects.PRAECANTATIO);
                if (stable.isPresent()) {
                    return stable.get();
                }
                Optional<Holder.Reference<IAspect>> first = registry.listElements().findFirst();
                if (first.isPresent()) {
                    return first.get();
                }
            }
        }
        return null;
    }

    @Nullable
    private static RegistryAccess clientRegistryAccess() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        return level == null
                ? null
                : level.registryAccess();
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        RegistryAccess registryAccess = clientRegistryAccess();
        if (registryAccess == null) {
            return;
        }
        Optional<Registry<IAspect>> registryOpt = registryAccess.lookup(IAspect.REGISTRY_KEY);
        if (registryOpt.isEmpty()) {
            return;
        }
        Registry<IAspect> aspectRegistry = registryOpt.get();
        List<EntryStack<?>> stacks = new ArrayList<>();
        for (Holder<IAspect> holder : aspectRegistry.listElements().toList()) {
            stacks.add(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(holder, 1)));
        }
        registry.addEntries(stacks);
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        // 配方类别。
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

        // 奥术工作台。
        forEachTypedRecipe(level, TCRecipeTypes.ARCANE.get(), holder -> registry.add(new ArcaneWorkbenchDisplay(holder)));

        // 熔锅。
        forEachTypedRecipe(level, TCRecipeTypes.CRUCIBLE.get(), holder -> registry.add(new CrucibleDisplay(holder)));

        // 注魔（三种配方）。
        forEachTypedRecipe(level, TCRecipeTypes.INFUSION.get(),
                holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.INFUSION_ID)));
        forEachTypedRecipe(level, TCRecipeTypes.INFUSION_ENCHANTMENT.get(),
                holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.ENCHANTMENT_ID)));
        forEachTypedRecipe(level, TCRecipeTypes.RUNIC_AUGMENT.get(),
                holder -> registry.add(new InfusionDisplay<>(holder, InfusionCategory.RUNIC_ID)));

        // 尘触发：Simple/Tag 归尘触发类别，Multiblock 归多方块类别。
        forEachTypedRecipe(level, TCRecipeTypes.DUST_TRIGGER.get(), holder -> {
            if (holder.value() instanceof DustTriggerSimpleRecipe || holder.value() instanceof DustTriggerTagRecipe) {
                registry.add(new DustTriggerDisplay(holder));
            } else if (holder.value() instanceof DustTriggerMultiblockRecipe) {
                registry.add(new MultiblockDisplay(holder));
            }
        });

        // 要素合成关系。
        RegistryAccess access = level.registryAccess();
        Optional<Registry<IAspect>> registryOpt = access.lookup(IAspect.REGISTRY_KEY);
        if (registryOpt.isPresent()) {
            for (AspectCompositionDisplay display :
                    AspectCompositionCategory.collect(registryOpt.get().listElements().toList())) {
                registry.add(display);
            }
        }

        // 要素来源物。
        for (AspectFromStacksDisplay display : AspectFromStacksCategory.collectAll(access)) {
            registry.add(display);
        }

        registerAspectInfoPages();
    }

    // 遍历某配方类型的全部持有者并交给回调处理。
    // RecipeMap.byType 的 I（RecipeInput）无法从调用侧推断，用原始类型规避并做安全转换。
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <R extends Recipe<?>> void forEachTypedRecipe(
            ClientLevel level, RecipeType<R> type, Consumer<RecipeHolder<R>> consumer) {
        RecipeMap map = TCClientRecipes.getRecipeMapForType(level, type);
        List<RecipeHolder<R>> holders = (List<RecipeHolder<R>>) map.byType((RecipeType) type);
        for (RecipeHolder<R> holder : holders) {
            consumer.accept(holder);
        }
    }
}
