package com.blackmoss.thaumaturgetweaks.client;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;

@EventBusSubscriber(modid = ThaumaturgeTweaks.MODID, value = Dist.CLIENT)
public final class AspectSlotAnnotations {
    private static final ResourceLocation PHIAL_ID = TCIds.rl("phial");
    private static final ResourceLocation ESSENTIA_CRYSTAL_ID = TCIds.rl("essentia_crystal");
    private static final ResourceLocation ASPECT_BACK_TEXTURE = TCIds.rl("textures/aspects/_back.png");

    private AspectSlotAnnotations() {
    }

    public static boolean isAspectVessel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.equals(PHIAL_ID) || id.equals(ESSENTIA_CRYSTAL_ID);
    }

    public static ResourceLocation aspectBackTexture() {
        return ASPECT_BACK_TEXTURE;
    }

    public static boolean renderAspectIcon(GuiGraphics graphics, int x, int y, ItemStack stack) {
        if (graphics == null || stack == null || stack.isEmpty()) {
            return false;
        }
        Holder<IAspect> aspect = aspectOf(stack);
        if (aspect == null) {
            return false;
        }
        graphics.blit(
                ASPECT_BACK_TEXTURE,
                x, y,
                0, 0,
                16, 16,
                32, 32,
                32, 32);
        int color = aspect.value().color();
        RenderSystem.setShaderColor(
                ((color >> 16) & 0xFF) / 255.0F,
                ((color >> 8) & 0xFF) / 255.0F,
                (color & 0xFF) / 255.0F,
                1.0F);
        graphics.blit(
                aspect.value().texture(),
                x, y,
                0, 0,
                16, 16,
                32, 32,
                32, 32);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }

    @SubscribeEvent
    public static void onContainerRenderForeground(ContainerScreenEvent.Render.Foreground event) {
        if (!Minecraft.getInstance().options.keyShift.isDown()) {
            return;
        }
        AbstractContainerScreen<?> screen = event.getContainerScreen();
        GuiGraphics graphics = event.getGuiGraphics();
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
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
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
