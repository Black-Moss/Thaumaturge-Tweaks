package com.blackmoss.thaumaturgetweaks.mixin.client.screen.research;

import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.client.screen.research.ResearchTableScreen;
import com.leclowndu93150.thaumaturge.content.research.note.HexGrid;
import com.leclowndu93150.thaumaturge.content.research.note.ResearchNoteData;
import com.leclowndu93150.thaumaturge.content.research.table.BlockEntityResearchTable;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ResearchTableScreen.class)
public interface ResearchTableScreenAccessor {
    @Accessor("page")
    int thaumaturgetweaks$page();

    @Accessor("page")
    void thaumaturgetweaks$setPage(int page);

    @Accessor("draggedAspect")
    @Nullable Holder<IAspect> thaumaturgetweaks$draggedAspect();

    @Accessor("draggedAspect")
    void thaumaturgetweaks$setDraggedAspect(@Nullable Holder<IAspect> aspect);

    @Invoker("discoveredAspects")
    List<Holder<IAspect>> thaumaturgetweaks$discoveredAspects();

    @Invoker("paletteAspectAt")
    @Nullable Holder<IAspect> thaumaturgetweaks$paletteAspectAt(double mouseX, double mouseY);

    @Invoker("table")
    @Nullable BlockEntityResearchTable thaumaturgetweaks$table();

    @Accessor("helperOpen")
    boolean thaumaturgetweaks$helperOpen();

    @Accessor("helperOpen")
    void thaumaturgetweaks$setHelperOpen(boolean helperOpen);

    @Accessor("helperPage")
    int thaumaturgetweaks$helperPage();

    @Accessor("helperPage")
    void thaumaturgetweaks$setHelperPage(int helperPage);

    @Invoker("discoveredCompounds")
    List<Holder<IAspect>> thaumaturgetweaks$discoveredCompounds();

    @Invoker("hexAt")
    @Nullable HexGrid.Hex thaumaturgetweaks$hexAt(double mouseX, double mouseY);

    @Invoker("noteData")
    @Nullable ResearchNoteData thaumaturgetweaks$noteData();
}
