// 要素合成类别：对标 Thaumaturge 本体 JEI 的 AspectCompositionCategory。
// 展示两个要素合成出一个复合要素（A + B = C），并标注各要素短名。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiTextDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    private static final float NAME_Y = 22.5F;
    private static final int OP_COLOR = 0xFF000000 | darkGrayColor();

    // ChatFormatting.getColor() 返回可空 Integer，此处提供默认值避免拆箱 NPE。
    private static int darkGrayColor() {
        Integer color = ChatFormatting.DARK_GRAY.getColor();
        return color == null ? 0x555555 : color;
    }

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
                .disableBackground().markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + RIGHT_X, start.y + RIGHT_Y))
                .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(recipe.right(), 1)))
                .disableBackground().markInput());

        // 输出槽。
        widgets.add(Widgets.createSlot(new Point(start.x + RESULT_X, start.y + RESULT_Y))
                .entry(EntryStack.of(AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(recipe.result(), 1)))
                .disableBackground().markOutput());

        // 运算符标注（本体左对齐 text 于 (27,10)/(63,10)，无阴影）。
        widgets.add(Widgets.createLabel(new Point(start.x + (LEFT_X + 16 + RIGHT_X) / 2 - 3, start.y + 10),
                        Component.literal("+"))
                .noShadow()
                .color(OP_COLOR));
        widgets.add(Widgets.createLabel(new Point(start.x + (RIGHT_X + 16 + RESULT_X) / 2 - 3, start.y + 10),
                        Component.literal("="))
                .noShadow()
                .color(OP_COLOR));

        // 各要素短名（本体 scale(0.5) 居中于槽位下方，坐标为全局屏幕坐标）。
        widgets.add(Widgets.createDrawableWidget(new ReiTextDrawable(AspectComponents.shortName(recipe.left()),
                0.5F, OP_COLOR, start.x + LEFT_X + 8, start.y + NAME_Y, true)));
        widgets.add(Widgets.createDrawableWidget(new ReiTextDrawable(AspectComponents.shortName(recipe.right()),
                0.5F, OP_COLOR, start.x + RIGHT_X + 8, start.y + NAME_Y, true)));
        widgets.add(Widgets.createDrawableWidget(new ReiTextDrawable(AspectComponents.shortName(recipe.result()),
                0.5F, OP_COLOR, start.x + RESULT_X + 8, start.y + NAME_Y, true)));

        return widgets;
    }

    public record Composition(Holder<IAspect> left, Holder<IAspect> right, Holder<IAspect> result) {
    }
}
