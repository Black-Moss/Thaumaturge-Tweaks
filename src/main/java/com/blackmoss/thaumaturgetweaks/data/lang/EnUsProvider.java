package com.blackmoss.thaumaturgetweaks.data.lang;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class EnUsProvider extends LanguageProvider {
    public EnUsProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("thaumaturgetweaks.containerscan.tooltip", "Use it on a container block to scan every item inside it");
        add("trinkets.slot.chest.charm", "Charm");
    }
}
