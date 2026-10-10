package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ReiRecipeEntries;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.content.recipe.crucible.CrucibleRecipe;
import com.leclowndu93150.thaumaturge.registry.TTItems;
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
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class CrucibleCategory implements DisplayCategory<CrucibleDisplay> {
    public static final CategoryIdentifier<CrucibleDisplay> ID = CategoryIdentifier.of("thaumaturgetweaks:crucible");
    private static final Identifier TEXTURE = TTIds.rl("textures/gui/gui_researchbook_overlay.png");
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
    private final ReiDrawable background = new ReiDrawable(TEXTURE, 2, 5, 109, 129, 512, 512, 0, 0, 9, 10);
    private final ReiDrawable arrow = new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512, 0, 0, 0, 0, 16, 6);
    private final Renderer icon;

    public CrucibleCategory() {
        this.icon = EntryStacks.of(TTItems.CRUCIBLE.get());
    }

    static ItemStack resultOf(CrucibleRecipe recipe) {
        return recipe.rawResult().create();
    }

    @Override
    public CategoryIdentifier<CrucibleDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gui.thaumaturge.recipe_type.crucible");
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

        widgets.add(background.toWidget(start.x, start.y));
        widgets.add(arrow.toWidget(start.x, start.y));

        CrucibleRecipe recipe = display.holder().value();

        widgets.add(Widgets.createSlot(new Point(start.x + OUTPUT_X, start.y + OUTPUT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, resultOf(recipe)))
                .disableBackground().markOutput());

        widgets.add(Widgets.createSlot(new Point(start.x + CATALYST_X, start.y + CATALYST_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, ReiRecipeEntries.firstStack(recipe.catalyst())))
                .disableBackground().markInput());

        int center = (recipe.aspects().entries().size() * ASPECT_SPACING) / 2;
        int index = 0;
        for (AspectInstance instance : recipe.aspects().sortedByAmount()) {
            widgets.add(Widgets.createSlot(new Point(
                            start.x + ASPECT_X - center + index * ASPECT_SPACING,
                            start.y + ASPECT_Y))
                    .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, instance))
                    .disableBackground().markInput());
            index++;
        }

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
