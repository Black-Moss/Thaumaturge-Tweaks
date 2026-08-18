// 神秘炼金塔界面（ThaumatoriumScreen）accessor：
// 暴露私有翻页偏移 index（每步 2 个配方），供本模组的滚轮/键盘翻页功能使用。
package com.blackmoss.thaumaturgetweaks.mixin.client.screen.essentia;

import com.leclowndu93150.thaumaturge.client.screen.ThaumatoriumScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThaumatoriumScreen.class)
public interface ThaumatoriumScreenAccessor {

    // 配方网格当前翻页偏移（从第 index*2 个配方开始显示，最多 6 个）。
    @Accessor("index")
    int thaumaturgetweaks$index();

    @Accessor("index")
    void thaumaturgetweaks$setIndex(int index);
}
