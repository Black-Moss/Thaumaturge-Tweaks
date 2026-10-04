package com.blackmoss.thaumaturgetweaks.mixin.client.screen;

import com.blackmoss.thaumaturgetweaks.client.AspectSlotAnnotations;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenAspectIconMixin {

    @Inject(method = "renderSlotContents", at = @At("TAIL"))
    private void thaumaturgetweaks$renderAspectIconOverlay(
            GuiGraphics graphics,
            ItemStack itemstack,
            Slot slot,
            String countString,
            CallbackInfo ci) {
        if (!Screen.hasShiftDown()) {
            return;
        }
        AspectSlotAnnotations.renderAspectIcon(graphics, slot.x, slot.y, itemstack);
    }
}
