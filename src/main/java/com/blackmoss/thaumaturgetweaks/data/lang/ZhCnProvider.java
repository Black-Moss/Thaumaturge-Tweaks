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
        add("trinkets.slot.chest.charm", "护符");
        add("category.thaumaturgetweaks.infernal_furnace.chance", "概率：%s%%");
        add("itemGroup.thaumaturgetweaks.aspect_items", "Thaumaturge | 要素物品");
        add("itemGroup.thaumaturgetweaks.wands", "Thaumaturge | 法杖");
    }
}
