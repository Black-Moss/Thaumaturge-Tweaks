package com.blackmoss.thaumaturgetweaks.mixin;

import com.blackmoss.thaumaturgetweaks.registry.TTCreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {
    @Shadow
    @Final
    private Supplier<ItemStack> iconGenerator;

    @Inject(method = "getIconItem", at = @At("HEAD"), cancellable = true)
    private void thaumaturgetweaks$cycleIcon(CallbackInfoReturnable<ItemStack> cir) {
        if (TTCreativeModeTabs.usesDynamicIcon((CreativeModeTab) (Object) this)) {
            cir.setReturnValue(this.iconGenerator.get());
        }
    }
}
