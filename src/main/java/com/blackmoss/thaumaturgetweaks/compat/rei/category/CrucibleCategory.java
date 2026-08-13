// 熔锅类别：对标 Thaumaturge 本体 JEI 的 CrucibleCategory。
// 渲染催化剂槽、输出槽、要素行，支持研究门控屏障与缺失研究提示。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.leclowndu93150.thaumaturge.content.recipe.crucible.CrucibleRecipe;
import com.leclowndu93150.thaumaturge.content.taint.item.EssentiaCrystalFactory;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class CrucibleCategory implements DisplayCategory<CrucibleDisplay> {

    public static final CategoryIdentifier<CrucibleDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:crucible");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");

    private static final int WIDTH = 129;
    private static final int HEIGHT = 129;

    private static final int OUTPUT_X = 55;
    private static final int OUTPUT_Y = 8;
    private static final int CATALYST_X = 2;
    private static final int CATALYST_Y = 2;
    private static final int BARRIER_X = 22;
    private static final int BARRIER_Y = 14;

    private static final int ASPECT_X = 66;
    private static final int ASPECT_Y = 66;
    private static final int ASPECT_SPACING = 22;

    private final ReiDrawable background =
            new ReiDrawable(TEXTURE, 2, 5, 109, 129, 512, 512, 0, 0, 9, 10);
    private final ReiDrawable arrow =
            new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512, 0, 0, 0, 0, 16, 6);
    private final Renderer icon;

    public CrucibleCategory() {
        this.icon = EntryStacks.of(TCItems.CRUCIBLE.get());
    }

    // 取配方的输出物品。ItemStackTemplate 转 ItemStack 使用 create()。
    static ItemStack resultOf(CrucibleRecipe recipe) {
        return recipe.rawResult().create();
    }

    // 取 Ingredient 的首个 ItemStack（REI 不直接支持 Ingredient）。
    static ItemStack firstStack(Ingredient ingredient) {
        return ingredient.items()
                .findFirst()
                .map(holder -> new ItemStack(holder.value()))
                .orElse(ItemStack.EMPTY);
    }

    @Override
    public CategoryIdentifier<CrucibleDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.type.crucible");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(CrucibleDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(CrucibleDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 背景与箭头装饰（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(background.toWidget(start.x, start.y));
        widgets.add(arrow.toWidget(start.x, start.y));

        CrucibleRecipe recipe = display.holder().value();

        // 输出槽。
        widgets.add(Widgets.createSlot(new Point(start.x + OUTPUT_X, start.y + OUTPUT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, resultOf(recipe)))
                .markOutput());

        // 催化剂输入槽。
        widgets.add(Widgets.createSlot(new Point(start.x + CATALYST_X, start.y + CATALYST_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, firstStack(recipe.catalyst())))
                .markInput());

        // 要素行（按数量降序居中排列）。
        int center = (recipe.aspects().entries().size() * ASPECT_SPACING) / 2;
        int index = 0;
        for (AspectInstance instance : recipe.aspects().sortedByAmount()) {
            widgets.add(Widgets.createSlot(new Point(
                            start.x + ASPECT_X - center + index * ASPECT_SPACING,
                            start.y + ASPECT_Y))
                    .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, instance))
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
