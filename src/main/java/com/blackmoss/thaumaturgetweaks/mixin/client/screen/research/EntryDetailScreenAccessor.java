package com.blackmoss.thaumaturgetweaks.mixin.client.screen.research;

import com.leclowndu93150.thaumaturge.client.screen.research.EntryDetailScreen;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryDetailScreen.class)
public interface EntryDetailScreenAccessor {
    @Invoker("nextPage")
    void thaumaturgetweaks$nextPage();

    @Invoker("prevPage")
    void thaumaturgetweaks$prevPage();

    @Accessor("shownRecipe")
    @Nullable Identifier thaumaturgetweaks$shownRecipe();

    @Accessor("showingAspects")
    boolean thaumaturgetweaks$showingAspects();

    @Accessor("showingKnowledge")
    boolean thaumaturgetweaks$showingKnowledge();
}
