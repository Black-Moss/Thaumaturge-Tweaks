// 研究台界面（ResearchTableScreen）accessor：
// 暴露调色板翻页状态（page）、已发现要素、命中检测与拖拽/合成所需私有成员，供本模组的增强功能使用。
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

    // 调色板页码。
    @Accessor("page")
    int thaumaturgetweaks$page();

    @Accessor("page")
    void thaumaturgetweaks$setPage(int page);

    // 当前拖拽中的要素。
    @Accessor("draggedAspect")
    @Nullable Holder<IAspect> thaumaturgetweaks$draggedAspect();

    @Accessor("draggedAspect")
    void thaumaturgetweaks$setDraggedAspect(@Nullable Holder<IAspect> aspect);

    // 已发现要素列表（调色板数据源）。
    @Invoker("discoveredAspects")
    List<Holder<IAspect>> thaumaturgetweaks$discoveredAspects();

    // 鼠标位置对应的调色板要素（越界/空白返回 null）。
    @Invoker("paletteAspectAt")
    @Nullable Holder<IAspect> thaumaturgetweaks$paletteAspectAt(double mouseX, double mouseY);

    // 研究台方块实体（用于计算 bonus 要素）。
    @Invoker("table")
    @Nullable BlockEntityResearchTable thaumaturgetweaks$table();

    // 要素合成参考（帮助面板）开关与页码。
    @Accessor("helperOpen")
    boolean thaumaturgetweaks$helperOpen();

    @Accessor("helperOpen")
    void thaumaturgetweaks$setHelperOpen(boolean helperOpen);

    @Accessor("helperPage")
    int thaumaturgetweaks$helperPage();

    @Accessor("helperPage")
    void thaumaturgetweaks$setHelperPage(int helperPage);

    // 已发现的化合物（双父要素），帮助面板数据源。
    @Invoker("discoveredCompounds")
    List<Holder<IAspect>> thaumaturgetweaks$discoveredCompounds();

    // 鼠标位置对应的六边形格子（研究纸张区域外返回 null）。
    @Invoker("hexAt")
    @Nullable HexGrid.Hex thaumaturgetweaks$hexAt(double mouseX, double mouseY);

    // 当前研究笔记数据（槽位 1 中的笔记，无笔记返回 null）。
    @Invoker("noteData")
    @Nullable ResearchNoteData thaumaturgetweaks$noteData();
}
