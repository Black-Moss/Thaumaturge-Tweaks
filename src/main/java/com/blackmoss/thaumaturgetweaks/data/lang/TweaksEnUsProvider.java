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
        add("thaumaturgetweaks.inventoryscan.scanning", "Scanning");
        add("thaumaturgetweaks.inventoryscan.thaumometer_tooltip", "Used for inventory scanning");
        add("thaumaturgetweaks.inventoryscan.thaumometer_tooltip_more", "Scan items in your inventory or containers by hovering over them with the Thaumometer held.");

        add("enchantment.thaumaturge.arcing.desc", "When you strike an enemy with a weapon enchanted with this, an arc of biting wind will hit an additional target nearby for half your weapons damage. Additional ranks will increase the number of additional targets and the range.");
        add("enchantment.thaumaturge.burrowing.desc", "When you try to harvest a tree or ore the furthest block will be harvested, instead of the one you are trying to break.");
        add("enchantment.thaumaturge.collector.desc", "Whenever this tool harvests an item it will automatically float towards you.。");
        add("enchantment.thaumaturge.destructive.desc", "When you harvest a block the 8 blocks surrounding (3x3 area) it will also be harvested if they are normally harvestable by the tool. Each additional block harvested causes durability loss as if you had harvested them by hand.");
        add("enchantment.thaumaturge.essence.desc", "When you you kill a creature with a weapon enchanted with this it has a chance of dropping some of its essence in crystal form. Increasing the rank of this enchantment increases the chance of crystals dropping and may also increase the number dropped.");
        add("enchantment.thaumaturge.lamplight.desc", "When you break a block this enchantment will place an invisible and intangible light source at the location if the light level is below 10.");
        add("enchantment.thaumaturge.refining.desc", "Whenever you harvest ore there is a chance of gaining a native cluster instead. Increasing the rank of the enchant will improve the chance.");
        add("enchantment.thaumaturge.sounding.desc", "You can sneak + right click on a block with this tool to send out a sounding pulse. This pulse will reveal any ores hidden nearby. Using this ability will cause some damage to the tool.");
    }
}
