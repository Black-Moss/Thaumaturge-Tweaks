package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.content.infernalfurnace.InfernalBonus;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InfernalFurnaceCategory implements DisplayCategory<InfernalFurnaceDisplay> {
    public static final CategoryIdentifier<InfernalFurnaceDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:infernal_furnace");

    private static final int WIDTH = 144;
    private static final int HEIGHT = 108;
    private static final int FURNACE_X = 21;
    private static final int FURNACE_Y = 33;
    private static final int ARROW_X = 39;
    private static final int ARROW_Y = 9;
    private static final int RESULT_ICON_X = 89;
    private static final int RESULT_ICON_Y = 48;
    private static final int INPUT_X = 22;
    private static final int INPUT_Y = 7;
    private static final int DEFAULT_OUTPUT_X = 96;
    private static final int DEFAULT_OUTPUT_Y = 55;
    private static final int BONUS_X = 123;
    private static final int BONUS_Y = 9;

    private final Renderer icon;
    private final ReiDrawable furnace =
            new ReiDrawable(TTIds.rl("textures/gui/gui_researchbook_overlay.png"), 445, 452, 67, 60, 512, 512);
    private final ReiDrawable arrow =
            new ReiDrawable(TTIds.rl("textures/gui/gui_researchbook_overlay.png"), 199, 168, 26, 26, 512, 512);
    private final ReiDrawable resultIcon =
            new ReiDrawable(TTIds.rl("textures/gui/gui_researchbook_overlay.png"), 41, 7, 30, 30, 512, 512);

    public InfernalFurnaceCategory() {
        this.icon = EntryStacks.of(TTItems.INFERNAL_FURNACE.get());
    }

    static int countOf(InfernalBonus bonus) {
        return Math.max(1, bonus.count().maxInclusive());
    }

    private static Component bonusTooltip(List<InfernalBonus> bonuses) {
        List<Component> lines = new ArrayList<>();
        for (InfernalBonus bonus : bonuses) {
            lines.add(oneBonusTooltip(bonus));
        }
        return lines.getFirst();
    }

    private static Component oneBonusTooltip(InfernalBonus bonus) {
        int min = bonus.count().minInclusive();
        int max = bonus.count().maxInclusive();
        Component count = min == max
                ? Component.literal(String.valueOf(min))
                : Component.literal(min + "-" + max);
        return Component.translatable("category.thaumaturgetweaks.infernal_furnace.chance", bonus.chance() * 100.0F)
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" x").withStyle(ChatFormatting.DARK_GRAY))
                .append(count.copy().withStyle(ChatFormatting.GRAY));
    }

    @Override
    public CategoryIdentifier<InfernalFurnaceDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.thaumaturge.infernal_furnace");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(InfernalFurnaceDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(InfernalFurnaceDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        widgets.add(furnace.toWidget(start.x + FURNACE_X, start.y + FURNACE_Y));
        widgets.add(arrow.toWidget(start.x + ARROW_X, start.y + ARROW_Y));
        widgets.add(resultIcon.toWidget(start.x + RESULT_ICON_X, start.y + RESULT_ICON_Y));

        widgets.add(Widgets.createSlot(new Point(start.x + INPUT_X, start.y + INPUT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, display.input()))
                .disableBackground().markInput());

        ItemStack defaultOutput = display.defaultOutput();
        if (!defaultOutput.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(start.x + DEFAULT_OUTPUT_X, start.y + DEFAULT_OUTPUT_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, defaultOutput))
                    .disableBackground().markOutput());
        }

        List<EntryStack<?>> bonusStacks = new ArrayList<>();
        for (InfernalBonus bonus : display.bonuses()) {
            for (ItemStack stack : InfernalFurnaceDisplay.stacksOf(bonus)) {
                bonusStacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(countOf(bonus))));
            }
        }
        if (!bonusStacks.isEmpty()) {
            Slot bonusSlot = Widgets.createSlot(new Point(start.x + BONUS_X, start.y + BONUS_Y))
                    .entries(bonusStacks)
                    .disableBackground().markOutput();
            widgets.add(bonusSlot);
            widgets.add(Widgets.withTooltip(bonusSlot, bonusTooltip(display.bonuses())));
        }

        return widgets;
    }
}
