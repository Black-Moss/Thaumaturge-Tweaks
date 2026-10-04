package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.drawable.ReiDrawable;
import com.blackmoss.thaumaturgetweaks.compat.rei.utils.ResearchUtils;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.recipe.Blueprint;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintPart;
import com.leclowndu93150.thaumaturge.api.recipe.BlueprintSource;
import com.leclowndu93150.thaumaturge.content.infusion.BlockEntityInfusionMatrix;
import com.leclowndu93150.thaumaturge.content.recipe.dust.DustTriggerMultiblockRecipe;
import com.leclowndu93150.thaumaturge.registry.TCBlocks;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MultiblockCategory implements DisplayCategory<MultiblockDisplay> {
    public static final CategoryIdentifier<MultiblockDisplay> ID = CategoryIdentifier.of("thaumaturgetweaks:multiblock_dust_trigger");
    private static final int WIDTH = 144;
    private static final int HEIGHT = 108;
    private static final int DUST_SLOT_X = 22;
    private static final int DUST_SLOT_Y = -2;
    private static final int RESULT_SLOT_X = 119;
    private static final int RESULT_SLOT_Y = 45;
    private static final int RESULT_ICON_X = 112;
    private static final int RESULT_ICON_Y = 38;
    private static final int BARRIER_X = 45;
    private static final int BARRIER_Y = 4;
    private static final int SLOT_ROW_Y = HEIGHT - 20;
    private static final int SLOT_ROW_START_X = 5;
    private static final int SLOT_ROW_SPACING = 20;
    private static final int ARROW_X = 39;
    private static final int ARROW_Y = 0;
    private static final ResourceLocation TEXTURE = TCIds.rl("textures/gui/gui_researchbook_overlay.png");
    private final ReiDrawable arrow = new ReiDrawable(TEXTURE, 199, 168, 26, 26, 512, 512);
    private final ReiDrawable resultIcon = new ReiDrawable(TEXTURE, 41, 7, 30, 30, 512, 512);
    private static final float PREVIEW_CENTER_X = 54.5F;
    private static final float PREVIEW_CENTER_Y = 56.5F;
    private static final float PREVIEW_SCALE = 15.0F;
    private static final float PREVIEW_ROT_X = 25.0F;
    private static final float PREVIEW_DEPTH = 200.0F;
    private final Renderer icon;
    private final BlockEntityInfusionMatrix matrixPreview;
    private int rotation = 0;

    public MultiblockCategory() {
        this.icon = EntryStacks.of(TCItems.SALIS_MUNDUS.get());
        this.matrixPreview = new BlockEntityInfusionMatrix(BlockPos.ZERO,
                TCBlocks.INFUSION_MATRIX.get().defaultBlockState());
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

    private void drawBlueprintPreview(
            GuiGraphics graphics,
            Rectangle bounds,
            DustTriggerMultiblockRecipe recipe,
            int mouseX,
            int mouseY,
            float delta) {
        Blueprint blueprint = lookupBlueprint(recipe.blueprintId());
        if (blueprint == null) {
            return;
        }
        Map<BlockPos, BlockState> blocks = new HashMap<>();
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (int y = 0; y < blueprint.ySize(); y++) {
            for (int x = 0; x < blueprint.xSize(); x++) {
                for (int z = 0; z < blueprint.zSize(); z++) {
                    BlueprintPart part = blueprint.cell(y, x, z);
                    if (part != null) {
                        int py = -y + (blueprint.ySize() - 1);
                        blocks.put(new BlockPos(x, py, z), part.source().getState());
                        minX = Math.min(minX, x);
                        maxX = Math.max(maxX, x);
                        minY = Math.min(minY, py);
                        maxY = Math.max(maxY, py);
                        minZ = Math.min(minZ, z);
                        maxZ = Math.max(maxZ, z);
                    }
                }
            }
        }
        if (blocks.isEmpty()) {
            return;
        }
        float centerX = (float) (minX + maxX + 1) / 2.0F;
        float centerY = (float) (minY + maxY + 1) / 2.0F;
        float centerZ = (float) (minZ + maxZ + 1) / 2.0F;
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource buffers = graphics.bufferSource();
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(bounds.x + PREVIEW_CENTER_X, bounds.y + PREVIEW_CENTER_Y, PREVIEW_DEPTH);
        pose.scale(PREVIEW_SCALE, -PREVIEW_SCALE, PREVIEW_SCALE);
        pose.mulPose(Axis.XP.rotationDegrees(PREVIEW_ROT_X));
        pose.mulPose(Axis.YP.rotationDegrees((float) rotation / 8.0F + 90.0F));
        pose.translate(-centerX, -centerY, -centerZ);
        Lighting.setupFor3DItems();
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            BlockPos blockPos = entry.getKey();
            pose.pushPose();
            pose.translate((float) blockPos.getX(), (float) blockPos.getY(), (float) blockPos.getZ());
            BlockState state = entry.getValue();
            if (state.is(TCBlocks.INFUSION_MATRIX.get())) {
                Minecraft.getInstance().getBlockEntityRenderDispatcher()
                        .renderItem(matrixPreview, pose, buffers, 15728880, OverlayTexture.NO_OVERLAY);
            } else {
                //noinspection deprecation
                dispatcher.renderSingleBlock(state, pose, buffers, 15728880, OverlayTexture.NO_OVERLAY);
            }
            pose.popPose();
        }
        buffers.endBatch();
        Lighting.setupForFlatItems();
        pose.popPose();
        rotation++;
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

        DustTriggerMultiblockRecipe recipe = (DustTriggerMultiblockRecipe) display.holder().value();
        widgets.add(Widgets.createDrawableWidget(
                (GuiGraphics graphics, int mx, int my, float delta) ->
                        drawBlueprintPreview(graphics, bounds, recipe, mx, my, delta)));

        widgets.add(arrow.toWidget(start.x + ARROW_X, start.y + ARROW_Y));
        widgets.add(resultIcon.toWidget(start.x + RESULT_ICON_X, start.y + RESULT_ICON_Y));

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
