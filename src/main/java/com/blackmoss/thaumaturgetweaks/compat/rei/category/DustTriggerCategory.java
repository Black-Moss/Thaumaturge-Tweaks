// 尘触发类别：对标 Thaumaturge 本体 JEI 的 DustTriggerCategory。
// 渲染盐晶（Salis Mundus）触发、目标方块（单个方块或方块标签）、输出。
// 仅包含 Simple 与 Tag 两类尘触发配方，Multiblock 配方归入多方块类别。
package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.recipe.DustTrigger;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerSimpleRecipe;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerTagRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class DustTriggerCategory implements DisplayCategory<DustTriggerDisplay> {

    public static final CategoryIdentifier<DustTriggerDisplay> ID =
            CategoryIdentifier.of("thaumaturgetweaks:dust_trigger");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(TCIds.MODID, "textures/gui/gui_researchbook_overlay.png");

    private static final int WIDTH = 144;
    private static final int HEIGHT = 54;

    private static final int DUST_SLOT_X = 7;
    private static final int DUST_SLOT_Y = 19;
    private static final int TARGET_SLOT_X = 57;
    private static final int TARGET_SLOT_Y = 19;
    private static final int RESULT_SLOT_X = 119;
    private static final int RESULT_SLOT_Y = 19;

    private static final int BARRIER_X = 87;
    private static final int BARRIER_Y = 20;

    private static final int PLUS_LABEL_X = 39;
    private static final int EQUALS_LABEL_X = 95;
    private static final int OP_LABEL_Y = 27;

    private final ReiDrawable resultIcon =
            new ReiDrawable(TEXTURE, 41, 7, 30, 30, 512, 512, 0, 0, 0, 0, 112, 12);
    private final Renderer icon;

    public DustTriggerCategory() {
        this.icon = EntryStacks.of(TCItems.SALIS_MUNDUS.get());
    }

    // 取三种尘触发配方的输出物品。
    static ItemStack resultStack(DustTrigger recipe) {
        if (recipe instanceof DustTriggerSimpleRecipe simple) {
            return simple.result();
        }
        if (recipe instanceof DustTriggerTagRecipe tag) {
            return tag.result();
        }
        if (recipe.isMultiblock()) {
            return ((DustTriggerMultiblockRecipe) recipe).result();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public CategoryIdentifier<DustTriggerDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.dust_trigger");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(DustTriggerDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(DustTriggerDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 输出装饰图标（固定坐标纹理 widget，避免跟随鼠标）。
        widgets.add(resultIcon.toWidget(start.x, start.y));

        // 盐晶输入槽 + 用法提示。
        Slot dustSlot = Widgets.createSlot(new Point(start.x + DUST_SLOT_X, start.y + DUST_SLOT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get())))
                .markInput();
        widgets.add(dustSlot);
        widgets.add(Widgets.withTooltip(dustSlot,
                Component.translatable("jei.thaumaturge.dust_trigger.usage")));

        // 目标槽：单方块或方块标签。
        DustTrigger recipe = display.holder().value();
        Slot targetSlot = Widgets.createSlot(new Point(start.x + TARGET_SLOT_X, start.y + TARGET_SLOT_Y))
                .markInput();
        if (recipe instanceof DustTriggerSimpleRecipe simple) {
            targetSlot.entry(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(simple.target())));
        } else if (recipe instanceof DustTriggerTagRecipe tag) {
            List<EntryStack<?>> stacks = new ArrayList<>();
            for (Holder<Block> blockHolder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag.targetTag())) {
                ItemStack stack = new ItemStack(blockHolder.value());
                if (!stack.isEmpty()) {
                    stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack));
                }
            }
            targetSlot.entries(stacks);
            TagKey<Block> tagKey = tag.targetTag();
            widgets.add(Widgets.withTooltip(targetSlot,
                    Component.translatable(
                            "jei.thaumaturge.dust_trigger.target.tag",
                            Component.literal("#" + tagKey.location()))));
        }
        widgets.add(targetSlot);

        // 输出槽。
        ItemStack result = resultStack(recipe);
        if (!result.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(start.x + RESULT_SLOT_X, start.y + RESULT_SLOT_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, result))
                    .markOutput());
        }

        // 运算符标注。
        int opColor = 0xFF000000 | ChatFormatting.DARK_GRAY.getColor();
        widgets.add(Widgets.createLabel(new Point(start.x + PLUS_LABEL_X, start.y + OP_LABEL_Y),
                        Component.literal("+"))
                .centered()
                .color(opColor));
        widgets.add(Widgets.createLabel(new Point(start.x + EQUALS_LABEL_X, start.y + OP_LABEL_Y),
                        Component.literal("="))
                .centered()
                .color(opColor));

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
