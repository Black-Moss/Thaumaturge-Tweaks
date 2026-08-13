// 要素合成类别：对标 Thaumaturge 本体 JEI 的 AspectCompositionCategory。
// 展示两个要素合成出一个复合要素（A + B = C），并标注各要素短名。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public final class AspectCompositionCategory implements DisplayCategory<AspectCompositionDisplay> {

    public static final CategoryIdentifier<AspectCompositionDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:aspect_composition");

    private static final int WIDTH = 100;
    private static final int HEIGHT = 34;

    private static final int LEFT_X = 6;
    private static final int LEFT_Y = 5;
    private static final int RIGHT_X = 38;
    private static final int RIGHT_Y = 5;
    private static final int RESULT_X = 78;
    private static final int RESULT_Y = 5;

    private static final int NAME_Y = 24;
    private static final int OP_COLOR = 0xFF000000 | ChatFormatting.DARK_GRAY.getColor();

    private final Renderer icon;

    public AspectCompositionCategory(@Nullable Holder<IAspect> iconAspect) {
        if (iconAspect != null) {
            this.icon = EntryStack.of(
                    AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(iconAspect, 1));
        } else {
            this.icon = EntryStacks.of(TCItems.SALIS_MUNDUS.get());
        }
    }

    // 收集所有非原初、恰好由两个要素组成的合成关系。
    public static List<AspectCompositionDisplay> collect(Iterable<Holder.Reference<IAspect>> all) {
        List<AspectCompositionDisplay> out = new ArrayList<>();
        for (Holder.Reference<IAspect> result : all) {
            IAspect value = result.value();
            if (value.isPrimal()) {
                continue;
            }
            List<Holder<IAspect>> components = value.components();
            if (components.size() != 2) {
                continue;
            }
            out.add(new AspectCompositionDisplay(new Composition(components.get(0), components.get(1), result)));
        }
        return out;
    }

    @Override
    public CategoryIdentifier<AspectCompositionDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.aspect_composition");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(AspectCompositionDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(AspectCompositionDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        Composition recipe = display.composition();

        // 输入槽（左 + 右）。
        widgets.add(Widgets.createSlot(new Point(start.x + LEFT_X, start.y + LEFT_Y))
                .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(recipe.left(), 1)))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + RIGHT_X, start.y + RIGHT_Y))
                .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(recipe.right(), 1)))
                .markInput());

        // 输出槽。
        widgets.add(Widgets.createSlot(new Point(start.x + RESULT_X, start.y + RESULT_Y))
                .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(recipe.result(), 1)))
                .markOutput());

        // 运算符标注。
        widgets.add(Widgets.createLabel(new Point(start.x + (LEFT_X + 16 + RIGHT_X) / 2 - 3, start.y + 10),
                        Component.literal("+"))
                .centered()
                .color(OP_COLOR));
        widgets.add(Widgets.createLabel(new Point(start.x + (RIGHT_X + 16 + RESULT_X) / 2 - 3, start.y + 10),
                        Component.literal("="))
                .centered()
                .color(OP_COLOR));

        // 各要素短名。
        widgets.add(Widgets.createLabel(new Point(start.x + LEFT_X + 8, start.y + NAME_Y),
                        AspectComponents.shortName(recipe.left()))
                .centered()
                .color(OP_COLOR));
        widgets.add(Widgets.createLabel(new Point(start.x + RIGHT_X + 8, start.y + NAME_Y),
                        AspectComponents.shortName(recipe.right()))
                .centered()
                .color(OP_COLOR));
        widgets.add(Widgets.createLabel(new Point(start.x + RESULT_X + 8, start.y + NAME_Y),
                        AspectComponents.shortName(recipe.result()))
                .centered()
                .color(OP_COLOR));

        return widgets;
    }

    public record Composition(Holder<IAspect> left, Holder<IAspect> right, Holder<IAspect> result) {}
}
