// 英文 lang DataGen。
package com.blackmoss.thaumaturgetweaks.data.lang;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public final class TweaksEnglishProvider extends LanguageProvider {

    public TweaksEnglishProvider(PackOutput output) {
        super(output, ThaumaturgeTweaks.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("thaumaturgetweaks.inventoryscan.scanning", "Scanning");
        add("thaumaturgetweaks.inventoryscan.thaumometer_tooltip", "Used for inventory scanning");
        add(
                "thaumaturgetweaks.inventoryscan.thaumometer_tooltip_more",
                "Scan items in your inventory or containers by hovering over them with the Thaumometer held.");
    }
}
