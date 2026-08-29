package com.blackmoss.thaumaturgetweaks.data.lang;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class TweaksZhCnProvider extends LanguageProvider {

    public TweaksZhCnProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        // 物品栏扫描已由本体 Thaumaturge 0.2.0 内置，相关文案已移除。
        add("thaumaturgetweaks.containerscan.tooltip", "对容器方块使用可一并扫描其中的所有物品");
    }
}
