// 简体中文 lang DataGen。
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
        add("thaumaturgetweaks.inventoryscan.scanning", "扫描中");
        add("thaumaturgetweaks.inventoryscan.thaumometer_tooltip", "用于物品栏扫描");
        add("thaumaturgetweaks.inventoryscan.thaumometer_tooltip_more", "手持魔导透镜并悬停在物品栏或存储容器的物品上即可进行扫描。");
    }
}
