// 要素来源物类别：对标 Thaumaturge 本体 JEI 的 AspectFromStacksCategory。
// 展示某要素可由哪些物品提供，物品按提供数量降序排列，每页最多 36 个。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class AspectFromStacksCategory implements DisplayCategory<AspectFromStacksDisplay> {

    public static final CategoryIdentifier<AspectFromStacksDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:aspect_from_stacks");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");
    private static final Identifier INNER_TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_inner.png");

    private static final int WIDTH = 176;
    private static final int HEIGHT = 109;

    private static final int PAGE_SIZE = 36;
    private static final int ROW_SIZE = 9;

    private static final int ASPECT_SLOT_X = 80;
    private static final int ASPECT_SLOT_Y = 8;
    private static final int STACK_SLOT_ORIGIN_X = 6;
    private static final int STACK_SLOT_ORIGIN_Y = 32;
    private static final int STACK_SLOT_SPACING = 18;

    private final ReiDrawable resultSlot =
            new ReiDrawable(TEXTURE, 40, 6, 32, 32, 512, 512, 0, 77, 72, 72);
    private final ReiDrawable innerBackground =
            new ReiDrawable(INNER_TEXTURE, 0, 0, 163, 74, 256, 256, 0, 0, 0, 0, 5, 30);
    private final Renderer icon;

    public AspectFromStacksCategory() {
        this.icon = EntryStacks.of(TCItems.THAUMONOMICON.get());
    }

    // 反查所有物品的要素构成，构建「要素 -> 来源物品」分页列表。
    public static List<AspectFromStacksDisplay> collectAll(RegistryAccess access) {
        List<AspectFromStacksDisplay> displays = new ArrayList<>();
        if (access == null) {
            return displays;
        }
        Registry<IAspect> aspectRegistry = access.lookup(IAspect.REGISTRY_KEY).orElse(null);
        if (aspectRegistry == null) {
            return displays;
        }

        Map<Holder<IAspect>, List<ItemStack>> inverted = new HashMap<>();
        for (Holder.Reference<Item> itemRef : BuiltInRegistries.ITEM.listElements().toList()) {
            ItemStack stack = new ItemStack(itemRef);
            if (stack.isEmpty()) {
                continue;
            }
            AspectList aspects = AspectIndexAccess.index().of(stack);
            for (AspectInstance instance : aspects.entries()) {
                inverted.computeIfAbsent(instance.aspect(), key -> new ArrayList<>()).add(stack);
            }
        }

        // 先原初要素后复合要素，组内按注册表 id 排序。
        List<Map.Entry<Holder<IAspect>, List<ItemStack>>> sorted = inverted.entrySet().stream()
                .sorted(Comparator.comparingInt(
                        (Map.Entry<Holder<IAspect>, List<ItemStack>> entry) ->
                                aspectRegistry.getId(entry.getKey().getKey())))
                .sorted(Comparator.comparing(entry -> !entry.getKey().value().isPrimal()))
                .toList();

        for (Map.Entry<Holder<IAspect>, List<ItemStack>> entry : sorted) {
            Holder<IAspect> aspect = entry.getKey();
            List<ItemStack> stacks = entry.getValue().stream()
                    .map(stack -> stack.copyWithCount(AspectIndexAccess.index().of(stack).amountOf(aspect)))
                    .filter(Predicate.not(ItemStack::isEmpty))
                    .sorted(Comparator.comparingInt(ItemStack::getCount).reversed())
                    .toList();
            int start = 0;
            while (start < stacks.size()) {
                int end = Math.min(start + PAGE_SIZE, stacks.size());
                displays.add(new AspectFromStacksDisplay(new Wrapper(aspect, stacks.subList(start, end))));
                start = end;
            }
        }
        return displays;
    }

    @Override
    public CategoryIdentifier<AspectFromStacksDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.aspect_from_stacks");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(AspectFromStacksDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(AspectFromStacksDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 装饰层（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(innerBackground.toWidget(start.x, start.y));
        widgets.add(resultSlot.toWidget(start.x, start.y));

        // 输出：要素图标。
        widgets.add(Widgets.createSlot(new Point(start.x + ASPECT_SLOT_X, start.y + ASPECT_SLOT_Y))
                .entry(EntryStack.of(
                        AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(display.wrapper().aspect(), 1)))
                .markOutput());

        // 输入：来源物品网格（9 列）。
        int slot = 0;
        for (ItemStack stack : display.wrapper().stacks()) {
            int slotX = (slot % ROW_SIZE) * STACK_SLOT_SPACING + STACK_SLOT_ORIGIN_X;
            int slotY = (slot / ROW_SIZE) * STACK_SLOT_SPACING + STACK_SLOT_ORIGIN_Y;
            widgets.add(Widgets.createSlot(new Point(start.x + slotX, start.y + slotY))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, stack))
                    .markInput());
            slot++;
        }

        return widgets;
    }

    public record Wrapper(Holder<IAspect> aspect, List<ItemStack> stacks) {}
}
