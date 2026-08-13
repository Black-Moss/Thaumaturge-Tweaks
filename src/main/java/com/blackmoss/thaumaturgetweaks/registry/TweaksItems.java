package com.blackmoss.thaumaturgetweaks.registry;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import com.blackmoss.thaumaturgetweaks.content.item.AkashicRecordsItem;
import com.blackmoss.thaumaturgetweaks.content.item.ExtremelyPrimordialPearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TweaksItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumaturgeTweaks.MODID);

    // 极元始珍珠：由 8 颗终末珍珠注魔而成，用作高价值注魔材料。
    public static final DeferredItem<ExtremelyPrimordialPearlItem> EXTREMELY_PRIMORDIAL_PEARL = ITEMS.registerItem(
            "extremely_primordial_pearl",
            ExtremelyPrimordialPearlItem::new,
            props -> props.stacksTo(1).rarity(Rarity.EPIC));

    // 阿卡西记录（Akashic Records）：使用后给予玩家全部要素的研究点。
    public static final DeferredItem<AkashicRecordsItem> AKASHIC_RECORDS = ITEMS.registerItem(
            "akashic_record",
            AkashicRecordsItem::new,
            props -> props.stacksTo(1).rarity(Rarity.EPIC));

    private TweaksItems() {
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
