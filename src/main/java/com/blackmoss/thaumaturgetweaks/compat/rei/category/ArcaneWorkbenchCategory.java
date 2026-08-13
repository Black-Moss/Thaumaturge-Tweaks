// 奥术工作台类别：对标 Thaumaturge 本体 JEI 的 ArcaneWorkbenchCategory。
// 渲染 3x3 合成网格、要素水晶列、VIS 消耗、研究门控屏障，并注册为 REI Display。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneShapedCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.workbench.ArcaneShapelessCraftingRecipe;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import com.leclowndu93150.thaumaturge.content.workbench.MenuArcaneWorkbench;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class ArcaneWorkbenchCategory implements DisplayCategory<ArcaneWorkbenchDisplay> {

    public static final CategoryIdentifier<ArcaneWorkbenchDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:arcane_workbench");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");

    private static final int WIDTH = 162;
    private static final int HEIGHT = 138;

    private static final int GRID_ORIGIN_X = 42;
    private static final int GRID_ORIGIN_Y = 48;
    private static final int GRID_SPACING = 31;

    private static final int OUTPUT_X = 73;
    private static final int OUTPUT_Y = 7;

    private static final int CRYSTAL_X = 141;
    private static final int CRYSTAL_Y = 6;
    private static final int CRYSTAL_SPACING = 22;

    private static final int PLATE_X = 65;
    private static final int PLATE_Y = 0;
    private static final int ARROW_X = 12;
    private static final int ARROW_Y = 4;
    private static final int BARRIER_X = 15;
    private static final int BARRIER_Y = 8;

    private static final int VIS_CENTER_X = 50;
    private static final int VIS_Y = 12;

    private final ReiDrawable background = new ReiDrawable(TEXTURE, 225, 31, 102, 102, 512, 512, 36, 0, 30, 30);
    private final ReiDrawable plate = new ReiDrawable(TEXTURE, 40, 6, 32, 32, 512, 512, 0, 0, 0, 0, PLATE_X, PLATE_Y);
    private final ReiDrawable arrow = new ReiDrawable(TEXTURE, 135, 152, 23, 23, 512, 512, 0, 0, 0, 0, ARROW_X, ARROW_Y);
    private final Renderer icon;

    public ArcaneWorkbenchCategory() {
        this.icon = EntryStacks.of(TCItems.ARCANE_WORKBENCH.get());
    }

    // 取配方的输出物品。ItemStackTemplate 转 ItemStack 使用 create()；
    // 无序配方的 result() 可能为 null（@Nullable），此时返回空物品。
    static ItemStack resultOf(ArcaneCraftingRecipe recipe) {
        ItemStackTemplate result;
        if (recipe instanceof ArcaneShapedCraftingRecipe shaped) {
            result = shaped.result();
        } else {
            result = ((ArcaneShapelessCraftingRecipe) recipe).result();
        }
        return result == null ? ItemStack.EMPTY : result.create();
    }

    // 在指定格子坐标添加一个输入槽。
    private static void addGridSlot(List<Widget> widgets, Point start, int x, int y, Ingredient ingredient) {
        widgets.add(Widgets.createSlot(new Point(
                        start.x + GRID_ORIGIN_X + x * GRID_SPACING,
                        start.y + GRID_ORIGIN_Y + y * GRID_SPACING))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, ReiRecipeEntries.firstStack(ingredient)))
                .disableBackground().markInput());
    }

    @Override
    public CategoryIdentifier<ArcaneWorkbenchDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.arcane_workbench");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(ArcaneWorkbenchDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(ArcaneWorkbenchDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 背景与装饰层（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(background.toWidget(start.x, start.y));
        widgets.add(plate.toWidget(start.x, start.y));
        widgets.add(arrow.toWidget(start.x, start.y));

        ArcaneCraftingRecipe recipe = display.holder().value();

        // 输入格子（3x3，有序网格或无序列表统一按格子坐标填充）。
        if (recipe instanceof ArcaneShapedCraftingRecipe shaped) {
            int width = shaped.getWidth();
            int height = shaped.getHeight();
            List<Optional<Ingredient>> ingredients = shaped.getIngredients();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Optional<Ingredient> opt = ingredients.get(x + y * width);
                    if (opt.isPresent()) {
                        addGridSlot(widgets, start, x, y, opt.get());
                    }
                }
            }
        } else {
            ArcaneShapelessCraftingRecipe shapeless = (ArcaneShapelessCraftingRecipe) recipe;
            List<Ingredient> ingredients = shapeless.ingredients();
            for (int i = 0; i < 9 && i < ingredients.size(); i++) {
                addGridSlot(widgets, start, i % 3, i / 3, ingredients.get(i));
            }
        }

        // 要素水晶列。
        AspectList crystals = recipe.getCrystals();
        if (!crystals.isEmpty()) {
            List<AspectInstance> aspects = crystals.entries().stream()
                    .sorted(Comparator.comparingInt(
                            k -> MenuArcaneWorkbench.PRIMAL_ORDER.indexOf(k.aspect().getKey())))
                    .toList();
            int index = 0;
            for (int i = 0; i < 6 && index < aspects.size(); i++) {
                AspectInstance instance = aspects.get(index);
                if (java.util.Objects.equals(instance.aspect().getKey(), MenuArcaneWorkbench.PRIMAL_ORDER.get(i))) {
                    ItemStack crystal = EssentiaCrystalFactory.of(instance.aspect(), instance.amount());
                    widgets.add(Widgets.createSlot(new Point(
                                    start.x + CRYSTAL_X, start.y + CRYSTAL_Y + index * CRYSTAL_SPACING))
                            .entry(EntryStack.of(VanillaEntryTypes.ITEM, crystal))
                            .disableBackground().markInput());
                    index++;
                } else {
                    widgets.add(Widgets.createSlot(new Point(
                                    start.x + BARRIER_X, start.y + BARRIER_Y))
                            .entry(EntryStack.of(VanillaEntryTypes.ITEM, Items.BARRIER.getDefaultInstance()))
                            .disableBackground().markInput());
                }
            }
        }

        // 输出。
        widgets.add(Widgets.createSlot(new Point(start.x + OUTPUT_X, start.y + OUTPUT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, resultOf(recipe)))
                .disableBackground().markOutput());

        // VIS 消耗文本（本体颜色 0xFF000000 | DARK_GRAY = 0xFF404040，无阴影）。
        String vis = Integer.toString(recipe.getBaseVis());
        widgets.add(Widgets.createLabel(new Point(start.x + VIS_CENTER_X, start.y + VIS_Y),
                        Component.literal(vis))
                .centered()
                .noShadow()
                .color(0xFF404040));

        // 研究门控屏障。
        if (!recipe.doesPassGate(Minecraft.getInstance().player)) {
            widgets.add(Widgets.createSlot(new Point(start.x + BARRIER_X, start.y + BARRIER_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, Items.BARRIER.getDefaultInstance()))
                    .disableBackground().markInput());
        }

        return widgets;
    }
}
