// 魔导手册研究页（EntryDetailScreen）accessor：
// 暴露 private 的翻页方法（nextPage/prevPage）与子视图状态字段，供本模组的事件处理器调用。
// 使用 Mixin 而非反射：编译期验证签名、无反射开销，缺失时直接报错而不会静默降级。
package com.blackmoss.thaumaturgetweaks.mixin.client.screen.research;

import com.leclowndu93150.thaumaturge.client.screen.research.EntryDetailScreen;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryDetailScreen.class)
public interface EntryDetailScreenAccessor {

    // 翻页（每次两页，翻页时播放翻页音效）。
    @Invoker("nextPage")
    void thaumaturgetweaks$nextPage();

    @Invoker("prevPage")
    void thaumaturgetweaks$prevPage();

    // 子视图状态：是否正在查看配方。
    @Accessor("shownRecipe")
    @Nullable Identifier thaumaturgetweaks$shownRecipe();

    // 子视图状态：是否正在查看要素。
    @Accessor("showingAspects")
    boolean thaumaturgetweaks$showingAspects();

    // 子视图状态：是否正在查看知识。
    @Accessor("showingKnowledge")
    boolean thaumaturgetweaks$showingKnowledge();
}
