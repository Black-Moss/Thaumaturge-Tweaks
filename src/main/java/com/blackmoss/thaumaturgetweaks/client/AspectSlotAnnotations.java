package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.TTIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class AspectSlotAnnotations {
    private static final Identifier PHIAL_ID = TTIds.rl("phial");
    private static final Identifier ESSENTIA_CRYSTAL_ID = TTIds.rl("essentia_crystal");
    private static final Identifier ASPECT_BACK_TEXTURE = TTIds.rl("textures/aspects/_back.png");

    private AspectSlotAnnotations() {
    }

    public static boolean isAspectVessel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.equals(PHIAL_ID) || id.equals(ESSENTIA_CRYSTAL_ID);
    }

    public static Identifier aspectBackTexture() {
        return ASPECT_BACK_TEXTURE;
    }

    public static boolean renderAspectIcon(GuiGraphicsExtractor graphics, int x, int y, ItemStack stack) {
        if (graphics == null || stack == null || stack.isEmpty()) {
            return false;
        }
        Holder<IAspect> aspect = aspectOf(stack);
        if (aspect == null) {
            return false;
        }
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ASPECT_BACK_TEXTURE,
                x, y,
                0.0F, 0.0F,
                16, 16,
                32, 32,
                32, 32,
                0xFFFFFFFF);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                aspect.value().texture(),
                x, y,
                0.0F, 0.0F,
                16, 16,
                32, 32,
                32, 32,
                0xFF000000 | aspect.value().color());
        return true;
    }

    @SubscribeEvent
    public static void onContainerRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!Minecraft.getInstance().hasShiftDown()) {
            return;
        }
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        for (Slot slot : screen.getMenu().slots) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            renderAspectIcon(graphics, slot.x, slot.y, stack);
        }
    }

    public static Holder<IAspect> aspectOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!id.equals(PHIAL_ID) && !id.equals(ESSENTIA_CRYSTAL_ID)) {
            return null;
        }
        AspectList aspects = AspectIndexAccess.of(stack);
        if (aspects == null || aspects.isEmpty()) {
            return null;
        }
        AspectInstance primary = aspects.entries().getFirst();
        if (primary == null || primary.aspect() == null) {
            return null;
        }
        return primary.aspect();
    }
}
