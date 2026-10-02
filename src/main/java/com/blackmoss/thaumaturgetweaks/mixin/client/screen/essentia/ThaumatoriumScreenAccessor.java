package com.blackmoss.thaumaturgetweaks.mixin.client.screen.essentia;

import com.leclowndu93150.thaumaturge.client.screen.ThaumatoriumScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThaumatoriumScreen.class)
public interface ThaumatoriumScreenAccessor {
    @Accessor("index")
    int thaumaturgetweaks$index();

    @Accessor("index")
    void thaumaturgetweaks$setIndex(int index);
}
