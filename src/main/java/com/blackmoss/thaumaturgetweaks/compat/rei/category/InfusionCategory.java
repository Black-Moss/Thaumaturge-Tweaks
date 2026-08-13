// 注魔类别：对标 Thaumaturge 本体 JEI 的 InfusionCategory。
// 泛型支持三种注魔配方（普通注魔 / 注魔附魔 / 符文强化）。
// 渲染中心催化剂、环形组件槽、要素行、输出，并显示不稳定度与研究门控。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.recipe.IInfusionRecipe;
import com.leclowndu93150.thaumaturge.content.infusion.InfusionEnchantmentRecipe;
import com.leclowndu93150.thaumaturge.content.infusion.InfusionRecipe;
import com.leclowndu93150.thaumaturge.content.infusion.InfusionRunicAugmentRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.List;

public final class InfusionCategory<R extends Recipe<?> & IInfusionRecipe>
        implements DisplayCategory<InfusionDisplay<R>> {

    public static final CategoryIdentifier<InfusionDisplay<InfusionRecipe>> INFUSION_ID =
            CategoryIdentifier.of("thaumaturgetweaks:infusion");
    public static final CategoryIdentifier<InfusionDisplay<InfusionEnchantmentRecipe>> ENCHANTMENT_ID =
            CategoryIdentifier.of("thaumaturgetweaks:infusion_enchantment");
    public static final CategoryIdentifier<InfusionDisplay<InfusionRunicAugmentRecipe>> RUNIC_ID =
            CategoryIdentifier.of("thaumaturgetweaks:runic_augment");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");

    private static final int WIDTH = 146;
    private static final int HEIGHT = 170;

    private static final int ASPECT_Y = 135;
    private static final int ASPECT_X = 46;
    private static final int ASPECT_SPACING = 22;

    private static final int OUTPUT_X = 65;
    private static final int OUTPUT_Y = 7;
    private static final int CATALYST_X = 65;
    private static final int CATALYST_Y = 75;
    private static final int RING_RADIUS = 40;
    private static final int BARRIER_X = 92;
    private static final int BARRIER_Y = 9;

    private static final int PAGE_TEXT_COLOR = 0xFF504030;
    private static final int INSTABILITY_LEVEL_CAP = 5;
    private static final ChatFormatting[] INSTABILITY_COLORS = {
            ChatFormatting.DARK_BLUE,
            ChatFormatting.BLUE,
            ChatFormatting.DARK_PURPLE,
            ChatFormatting.YELLOW,
            ChatFormatting.GOLD,
            ChatFormatting.DARK_RED
    };

    private final ReiDrawable background =
            new ReiDrawable(TEXTURE, 413, 154, 86, 86, 512, 512, 40, 44, 30, 30);
    private final ReiDrawable headIcon =
            new ReiDrawable(TEXTURE, 40, 6, 32, 32, 512, 512, 0, 0, 0, 0, 57, 0);
    private final Renderer icon;
    private final CategoryIdentifier<InfusionDisplay<R>> id;
    private final Component title;

    public InfusionCategory(CategoryIdentifier<InfusionDisplay<R>> id, String titleKey) {
        this.icon = EntryStacks.of(TCItems.INFUSION_MATRIX.get());
        this.id = id;
        this.title = Component.translatable(titleKey);
    }

    @Override
    public CategoryIdentifier<InfusionDisplay<R>> getCategoryIdentifier() {
        return id;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(InfusionDisplay<R> display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(InfusionDisplay<R> display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 背景与顶部图标（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(background.toWidget(start.x, start.y));
        widgets.add(headIcon.toWidget(start.x, start.y));

        IInfusionRecipe recipe = display.holder().value();

        // 输出槽。
        widgets.add(Widgets.createSlot(new Point(start.x + OUTPUT_X, start.y + OUTPUT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, recipe.resultItem()))
                .disableBackground().markOutput());

        // 中心催化剂槽。
        widgets.add(Widgets.createSlot(new Point(start.x + CATALYST_X, start.y + CATALYST_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, ReiRecipeEntries.firstStack(recipe.catalyst())))
                .disableBackground().markInput());

        // 周围组件环（以催化剂为中心，半径 40，顺时针均匀分布）。
        List<Ingredient> components = recipe.components();
        float currentRotation = -90.0F;
        for (Ingredient component : components) {
            int slotX = start.x + CATALYST_X
                    + (int) (Mth.cos(currentRotation / 180.0F * (float) Math.PI) * RING_RADIUS);
            int slotY = start.y + CATALYST_Y
                    + (int) (Mth.sin(currentRotation / 180.0F * (float) Math.PI) * RING_RADIUS);
            widgets.add(Widgets.createSlot(new Point(slotX, slotY))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, ReiRecipeEntries.firstStack(component)))
                    .disableBackground().markInput());
            currentRotation += 360.0F / components.size();
        }

        // 要素行（按数量降序居中排列）。
        int center = (recipe.aspects().entries().size() * ASPECT_SPACING) / 2;
        int index = 0;
        for (AspectInstance aspect : recipe.aspects().sortedByAmount()) {
            widgets.add(Widgets.createSlot(new Point(
                            start.x + 30 + ASPECT_X - center + index * ASPECT_SPACING,
                            start.y + ASPECT_Y))
                    .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, aspect))
                    .disableBackground().markInput());
            index++;
        }

        // 不稳定度文本。
        int level = Math.min(INSTABILITY_LEVEL_CAP, recipe.instability() / 2);
        Component levelName = Component.translatable("gui.thaumaturge.infusion.instability." + level)
                .withStyle(INSTABILITY_COLORS[level]);
        Component label = Component.translatable("gui.thaumaturge.infusion.instability", levelName);
        widgets.add(Widgets.createLabel(new Point(start.x + WIDTH / 2, start.y + 158), label)
                .centered()
                .color(PAGE_TEXT_COLOR));

        // 研究门控屏障与缺失研究提示。
        if (!recipe.doesPassGate(Minecraft.getInstance().player)) {
            Slot barrier = Widgets.createSlot(new Point(start.x + BARRIER_X, start.y + BARRIER_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, Items.BARRIER.getDefaultInstance()))
                    .disableBackground().markInput();
            widgets.add(barrier);
            recipe.researchGate().ifPresent(gate -> widgets.add(
                    Widgets.withTooltip(barrier, ResearchUtils.generateMissingResearchList(gate))));
        }

        return widgets;
    }
}
