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
        add("trinkets.slot.chest.charm", "Charm");
        add("category.thaumaturgetweaks.infernal_furnace.chance", "Chance: %s%%");
    }
}
