// 多方块尘触发类别：对标 Thaumaturge 本体 JEI 的 MultiblockCategory。
// 渲染盐晶触发、输出，以及蓝图所需方块（按需求量降序排列）。
// 注：本体通过 Mixin 注入的 3D 方块预览不在此实现，仅展示方块需求清单。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.recipe.Blueprint;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintPart;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintSource;
import com.leclowndu93150.thaumaturge.api.recipe.DustTrigger;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MultiblockCategory implements DisplayCategory<MultiblockDisplay> {

    public static final CategoryIdentifier<MultiblockDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:multiblock_dust_trigger");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");

    private static final int WIDTH = 144;
    private static final int HEIGHT = 108;

    private static final int DUST_SLOT_X = 22;
    private static final int DUST_SLOT_Y = -2;
    private static final int RESULT_SLOT_X = 119;
    private static final int RESULT_SLOT_Y = 45;
    private static final int BARRIER_X = 45;
    private static final int BARRIER_Y = 4;

    private static final int SLOT_ROW_Y = HEIGHT - 20;
    private static final int SLOT_ROW_START_X = 5;
    private static final int SLOT_ROW_SPACING = 20;

    private final ReiDrawable resultIcon =
            new ReiDrawable(TEXTURE, 41, 7, 30, 30, 512, 512, 0, 0, 0, 0, 112, 39);
    private final ReiDrawable arrow =
            new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512, 0, 0, 0, 0, 39, 0);
    private final Renderer icon;

    public MultiblockCategory() {
        this.icon = EntryStacks.of(TCItems.SALIS_MUNDUS.get());
    }

    // 从注册表查找蓝图，null 表示不可用。
    @Nullable
    private static Blueprint lookupBlueprint(Identifier blueprintId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        Registry<Blueprint> registry =
                minecraft.level.registryAccess().lookup(Blueprint.REGISTRY_KEY).orElse(null);
        if (registry == null) {
            return null;
        }
        ResourceKey<Blueprint> key = ResourceKey.create(Blueprint.REGISTRY_KEY, blueprintId);
        return registry.get(key).map(Holder::value).orElse(null);
    }

    // 统计蓝图各来源方块的需求数量，按数量降序返回。
    static List<Map.Entry<BlueprintSource, Integer>> sortedBlueprintSources(DustTriggerMultiblockRecipe recipe) {
        Map<BlueprintSource, Integer> counts = new HashMap<>();
        Blueprint blueprint = lookupBlueprint(recipe.blueprintId());
        if (blueprint != null) {
            for (int y = 0; y < blueprint.ySize(); y++) {
                for (int x = 0; x < blueprint.xSize(); x++) {
                    for (int z = 0; z < blueprint.zSize(); z++) {
                        BlueprintPart part = blueprint.cell(y, x, z);
                        if (part != null && !part.source().getRepresentations().isEmpty()) {
                            counts.merge(part.source(), 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return counts.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<BlueprintSource, Integer> entry) -> entry.getValue())
                        .reversed())
                .toList();
    }

    @Override
    public CategoryIdentifier<MultiblockDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.multiblock_dust_trigger");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(MultiblockDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(MultiblockDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 输出装饰图标与箭头（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(resultIcon.toWidget(start.x, start.y));
        widgets.add(arrow.toWidget(start.x, start.y));

        // 盐晶输入槽 + 用法提示。
        Slot dustSlot = Widgets.createSlot(new Point(start.x + DUST_SLOT_X, start.y + DUST_SLOT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get())))
                .markInput();
        widgets.add(dustSlot);
        widgets.add(Widgets.withTooltip(dustSlot,
                Component.translatable("jei.thaumaturge.dust_trigger.target.multiblock")));

        DustTriggerMultiblockRecipe recipe = (DustTriggerMultiblockRecipe) display.holder().value();

        // 输出槽。
        ItemStack result = recipe.result();
        if (!result.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(start.x + RESULT_SLOT_X, start.y + RESULT_SLOT_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, result))
                    .markOutput());
        }

        // 蓝图所需方块行（按需求量降序）。
        List<Map.Entry<BlueprintSource, Integer>> sorted = sortedBlueprintSources(recipe);
        int index = 0;
        for (Map.Entry<BlueprintSource, Integer> entry : sorted) {
            int count = entry.getValue();
            List<EntryStack<?>> stacks = new ArrayList<>();
            for (ItemStack stack : entry.getKey().getRepresentations()) {
                stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(count)));
            }
            widgets.add(Widgets.createSlot(new Point(
                            start.x + SLOT_ROW_START_X + index * SLOT_ROW_SPACING,
                            start.y + SLOT_ROW_Y))
                    .entries(stacks)
                    .markInput());
            index++;
        }

        // 研究门控屏障与缺失研究提示。
        if (!recipe.doesPassGate(Minecraft.getInstance().player)) {
            Slot barrier = Widgets.createSlot(new Point(start.x + BARRIER_X, start.y + BARRIER_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, Items.BARRIER.getDefaultInstance()))
                    .markInput();
            widgets.add(barrier);
            recipe.researchGate().ifPresent(gate -> widgets.add(
                    Widgets.withTooltip(barrier, ResearchUtils.generateMissingResearchList(gate))));
        }

        return widgets;
    }
}
