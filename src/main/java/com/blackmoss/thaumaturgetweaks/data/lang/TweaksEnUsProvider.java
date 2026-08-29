package com.blackmoss.thaumaturgetweaks.data.lang;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class TweaksEnUsProvider extends LanguageProvider {

    public TweaksEnUsProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // 物品栏扫描已由本体 Thaumaturge 0.2.0 内置，相关文案已移除。
        add("thaumaturgetweaks.containerscan.tooltip", "Use it on a container block to scan every item inside it");
    }
}
