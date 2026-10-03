package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.recipe.Blueprint;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintPart;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintSource;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import net.minecraft.client.gui.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MultiblockCategory implements DisplayCategory<MultiblockDisplay> {
    public static final CategoryIdentifier<MultiblockDisplay> ID = CategoryIdentifier.of("thaumaturgetweaks:multiblock_dust_trigger");
    private static final ResourceLocation TEXTURE = TCIds.rl("textures/gui/gui_researchbook_overlay.png");
    private static final int WIDTH = 144;
    private static final int HEIGHT = 108;
    private static final int DUST_SLOT_X = 22;
    private static final int DUST_SLOT_Y = -2;
    private static final int RESULT_SLOT_X = 119;
    private static final int RESULT_SLOT_Y = 45;
    private static final int BARRIER_X = 45;
    private static final int BARRIER_Y = 4;
    private static final int SLOT_ROW_Y = HEIGHT - 20;
    private static final int SLOT_ROW_START_X = 5;
    private static final int SLOT_ROW_SPACING = 20;
    // 等距投影里单个方块的基础投影宽度。
    private static final int TILE = 10;
    private final ReiDrawable resultIcon = new ReiDrawable(TEXTURE, 41, 7, 30, 30, 512, 512, 0, 0, 0, 0, 112, 39);
    private final ReiDrawable arrow = new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512, 0, 0, 0, 0, 39, 0);
    private final Renderer icon;

    public MultiblockCategory() {
        this.icon = EntryStacks.of(TCItems.SALIS_MUNDUS.get());
    }

    @Nullable
    private static Blueprint lookupBlueprint(ResourceLocation blueprintId) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        return ResearchUtils.find(minecraft.level.registryAccess(), Blueprint.REGISTRY_KEY, blueprintId).orElse(null);
    }

    static List<Map.Entry<BlueprintSource, Integer>> sortedBlueprintSources(DustTriggerMultiblockRecipe recipe) {
        Map<BlueprintSource, Integer> counts = new HashMap<>();
        Blueprint blueprint = lookupBlueprint(recipe.blueprintId());
        if (blueprint != null) {
            for (int y = 0; y < blueprint.ySize(); y++) {
                for (int x = 0; x < blueprint.xSize(); x++) {
                    for (int z = 0; z < blueprint.zSize(); z++) {
                        BlueprintPart part = blueprint.cell(y, x, z);
                        if (part != null && !part.source().getRepresentations().isEmpty()) {
                            counts.merge(part.source(), 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return counts.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<BlueprintSource, Integer> entry) -> entry.getValue())
                        .reversed())
                .toList();
    }

    // 1.21.1 的父模组没有 26.x 的 PiP（BlockPreviewRenderState）渲染通道，而 REI 的 widget 系统是纯 2D 的，
    // 没有可用的 3D 方块渲染 API，所以这里自行做等距投影：按 MapColor 给每个方块画三个明暗不同的面。
    private void drawBlueprintPreview(GuiGraphics graphics, Rectangle bounds, DustTriggerMultiblockRecipe recipe) {
        Blueprint blueprint = lookupBlueprint(recipe.blueprintId());
        if (blueprint == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        int ySize = blueprint.ySize();
        int xSize = blueprint.xSize();
        int zSize = blueprint.zSize();

        List<Map.Entry<BlockPos, BlockState>> blocks = new ArrayList<>();
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                for (int z = 0; z < zSize; z++) {
                    BlueprintPart part = blueprint.cell(y, x, z);
                    if (part != null) {
                        blocks.add(Map.entry(new BlockPos(x, ySize - 1 - y, z), part.source().getState()));
                    }
                }
            }
        }
        if (blocks.isEmpty()) {
            return;
        }

        // 按图纸尺寸缩放，保证整体落在显示区域内。
        int spanX = xSize + zSize;
        int spanY = (xSize + zSize) / 2 + ySize;
        float scale = Math.min(
                (float) (WIDTH - 16) / Math.max(1, spanX * TILE),
                (float) (HEIGHT - 16) / Math.max(1, spanY * TILE));
        int tile = Math.max(4, Math.round(TILE * scale));
        int halfW = Math.max(2, tile / 2);
        int quarterH = Math.max(1, tile / 4);
        int sideH = Math.max(2, tile * 3 / 4);

        int centerX = bounds.x + WIDTH / 2;
        int centerY = bounds.y + HEIGHT / 2 + (ySize * sideH) / 2;

        // 从远到近绘制，保证近处方块覆盖远处。
        blocks.sort(Comparator.comparingInt(entry -> entry.getKey().getX() + entry.getKey().getZ()));
        for (Map.Entry<BlockPos, BlockState> entry : blocks) {
            BlockPos pos = entry.getKey();
            int sx = centerX + (pos.getX() - pos.getZ()) * halfW;
            int sy = centerY + (pos.getX() + pos.getZ()) * quarterH - pos.getY() * sideH;
            MapColor mapColor = entry.getValue().getMapColor(minecraft.level, BlockPos.ZERO);
            if (mapColor == null) {
                continue;
            }
            graphics.fill(sx - halfW, sy - quarterH, sx + halfW, sy, argb(mapColor, MapColor.Brightness.HIGH));
            graphics.fill(sx - halfW, sy, sx, sy + sideH, argb(mapColor, MapColor.Brightness.NORMAL));
            graphics.fill(sx, sy, sx + halfW, sy + sideH, argb(mapColor, MapColor.Brightness.LOW));
        }
    }

    private static int argb(MapColor mapColor, MapColor.Brightness brightness) {
        return 0xFF000000 | mapColor.calculateRGBColor(brightness);
    }

    @Override
    public CategoryIdentifier<MultiblockDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.thaumaturge.category.multiblock_dust_trigger");
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(MultiblockDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(MultiblockDisplay display, Rectangle bounds) {
        Point start = new Point(bounds.x, bounds.y);
        List<Widget> widgets = new ArrayList<>();

        // 蓝图预览铺满整个显示区，必须在所有槽位之前绘制（层级由添加顺序决定，z 在 2D 矩阵栈下无效）。
        DustTriggerMultiblockRecipe recipe = (DustTriggerMultiblockRecipe) display.holder().value();
        widgets.add(Widgets.createDrawableWidget(
                (GuiGraphics graphics, int mx, int my, float delta) -> drawBlueprintPreview(graphics, bounds, recipe)));

        widgets.add(arrow.toWidget(start.x, start.y));
        // 装饰图标与结果槽重叠，同样必须在槽位之前。
        widgets.add(resultIcon.toWidget(start.x, start.y));

        Slot dustSlot = Widgets.createSlot(new Point(start.x + DUST_SLOT_X, start.y + DUST_SLOT_Y))
                .entry(EntryStack.of(VanillaEntryTypes.ITEM, new ItemStack(TCItems.SALIS_MUNDUS.get())))
                .disableBackground().markInput();
        widgets.add(dustSlot);
        widgets.add(Widgets.withTooltip(dustSlot,
                Component.translatable("jei.thaumaturge.dust_trigger.target.multiblock")));

        ItemStack result = recipe.result();
        if (!result.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(start.x + RESULT_SLOT_X, start.y + RESULT_SLOT_Y))
                    .entry(EntryStack.of(VanillaEntryTypes.ITEM, result))
                    .disableBackground().markOutput());
        }

        List<Map.Entry<BlueprintSource, Integer>> sorted = sortedBlueprintSources(recipe);
        int index = 0;
        for (Map.Entry<BlueprintSource, Integer> entry : sorted) {
            int count = entry.getValue();
            List<EntryStack<?>> stacks = new ArrayList<>();
            for (ItemStack stack : entry.getKey().getRepresentations()) {
                stacks.add(EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(count)));
            }
            widgets.add(Widgets.createSlot(new Point(
                            start.x + SLOT_ROW_START_X + index * SLOT_ROW_SPACING,
                            start.y + SLOT_ROW_Y))
                    .entries(stacks)
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
