package com.blackmoss.thaumaturgetweaks.data.lang;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class ZhCnProvider extends LanguageProvider {

    public ZhCnProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID, "zh_cn");
    }

    @Override
    protected void addTranslations() {
        add("thaumaturgetweaks.containerscan.tooltip", "对容器方块使用可一并扫描其中的所有物品");
        add("trinkets.slot.chest.charm", "护符");
    }
}
