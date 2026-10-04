package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.content.infernalfurnace.InfernalBonus;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class InfernalFurnaceCategory implements DisplayCategory<InfernalFurnaceDisplay> {
    public static final CategoryIdentifier<InfernalFurnaceDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:infernal_furnace");

    private static final int WIDTH = 144;
    private static final int HEIGHT = 108;
    private static final int INPUT_X = 22;
    private static final int INPUT_Y = 7;
    private static final int DEFAULT_OUTPUT_X = 96;
    private static final int DEFAULT_OUTPUT_Y = 55;
    private static final int BONUS_X = 123;
    private static final int BONUS_Y = 20;
    private static final int ARROW_X = 35;
    private static final int ARROW_Y = 10;
    private static final int RESULT_ICON_X = 89;
    private static final int RESULT_ICON_Y = 48;
    private static final ResourceLocation TEXTURE = TCIds.rl("textures/gui/gui_researchbook_overlay.png");
    private static final int FURNACE_X = 21;
    private static final int FURNACE_Y = 39;
    private final Renderer icon;
    private final ReiDrawable furnace = new ReiDrawable(TEXTURE, 445, 452, 67, 60, 512, 512);
    private final ReiDrawable arrow = new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512);
    private final ReiDrawable resultIcon = new ReiDrawable(TEXTURE, 41, 7, 30, 30, 512, 512);

    public InfernalFurnaceCategory() {
        this.icon = EntryStacks.of(TCItems.INFERNAL_FURNACE.get());
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

        widgets.add(furnace.toBackgroundWidget(start.x + FURNACE_X, start.y + FURNACE_Y));
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

        List<InfernalBonus> bonuses = display.bonuses();
        List<EntryStack<?>> bonusStacks = new ArrayList<>();
        for (InfernalBonus bonus : bonuses) {
            for (ItemStack stack : stacksOf(bonus)) {
                bonusStacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(countOf(bonus))));
            }
        }
        if (!bonusStacks.isEmpty()) {
            Slot bonusSlot = Widgets.createSlot(new Point(start.x + BONUS_X, start.y + BONUS_Y))
                    .entries(bonusStacks)
                    .disableBackground().markOutput();
            widgets.add(bonusSlot);
            widgets.add(Widgets.withTooltip(bonusSlot, bonusTooltip(bonuses)));
        }

        return widgets;
    }

    private static List<ItemStack> stacksOf(InfernalBonus bonus) {
        List<ItemStack> out = new ArrayList<>();
        for (var holder : bonus.items()) {
            if (holder.isBound()) {
                holder.value();
                out.add(new ItemStack(holder.value()));
            }
        }
        return out;
    }

    private static int countOf(InfernalBonus bonus) {
        return Math.max(1, bonus.count().getMaxValue());
    }

    private static Component bonusTooltip(List<InfernalBonus> bonuses) {
        List<Component> lines = new ArrayList<>();
        for (InfernalBonus bonus : bonuses) {
            lines.add(oneBonusTooltip(bonus));
        }
        return lines.getFirst();
    }

    private static Component oneBonusTooltip(InfernalBonus bonus) {
        int min = bonus.count().getMinValue();
        int max = bonus.count().getMaxValue();
        Component count = min == max
                ? Component.literal(String.valueOf(min))
                : Component.literal(min + "-" + max);
        return Component.translatable("category.thaumaturge.infernal_furnace.chance", bonus.chance() * 100.0F)
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(" x").withStyle(ChatFormatting.DARK_GRAY))
                .append(count.copy().withStyle(ChatFormatting.GRAY));
    }
}
 
