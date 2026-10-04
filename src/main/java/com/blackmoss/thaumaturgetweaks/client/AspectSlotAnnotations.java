package com.blackmoss.thaumaturgetweaks.client;

import com.leclowndu93150.thaumaturge.TCIds;
import com.leclowndu93150.thaumaturge.api.aspect.AspectIndexAccess;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.AspectList;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.client.render.GuiBlend;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class AspectSlotAnnotations {
    private static final String PHIAL_PATH = "phial";
    private static final String ESSENTIA_CRYSTAL_PATH = "essentia_crystal";
    private static final ResourceLocation ASPECT_BACK_TEXTURE = TCIds.rl("textures/aspects/_back.png");
    private static final int BACK_TEXTURE_SIZE = 64;
    private static final int ASPECT_TEXTURE_SIZE = 32;
    private static final int ICON_SIZE = 16;

    private AspectSlotAnnotations() {
    }

    public static boolean isAspectVessel(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return isAspectVesselPath(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
    }

    public static Holder<IAspect> aspectOf(ItemStack stack) {
        if (!isAspectVessel(stack)) {
            return null;
        }
        AspectList aspects = AspectIndexAccess.of(stack);
        if (aspects == null || aspects.isEmpty()) {
            return null;
        }
        AspectInstance primary = aspects.entries().getFirst();
        return primary == null ? null : primary.aspect();
    }

    public static boolean renderAspectIcon(GuiGraphics graphics, int x, int y, ItemStack stack) {
        if (graphics == null) {
            return false;
        }
        Holder<IAspect> aspect = aspectOf(stack);
        if (aspect == null) {
            return false;
        }
        GuiBlend.withAlphaBlend(graphics, () -> {
            RenderSystem.disableDepthTest();
            try {
                graphics.blit(
                        ASPECT_BACK_TEXTURE,
                        x, y,
                        ICON_SIZE, ICON_SIZE,
                        0.0F, 0.0F,
                        BACK_TEXTURE_SIZE, BACK_TEXTURE_SIZE,
                        BACK_TEXTURE_SIZE, BACK_TEXTURE_SIZE);
                int color = aspect.value().color();
                graphics.setColor(
                        ((color >> 16) & 0xFF) / 255.0F,
                        ((color >> 8) & 0xFF) / 255.0F,
                        (color & 0xFF) / 255.0F,
                        1.0F);
                graphics.blit(
                        aspect.value().texture(),
                        x, y,
                        ICON_SIZE, ICON_SIZE,
                        0.0F, 0.0F,
                        ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE,
                        ASPECT_TEXTURE_SIZE, ASPECT_TEXTURE_SIZE);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            } finally {
                RenderSystem.enableDepthTest();
            }
        });
        return true;
    }

    private static boolean isAspectVesselPath(String path) {
        return path.equals(PHIAL_PATH) || path.equals(ESSENTIA_CRYSTAL_PATH);
    }
}
