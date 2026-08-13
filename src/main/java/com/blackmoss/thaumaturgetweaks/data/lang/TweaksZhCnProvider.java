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

        add("item.thaumaturgetweaks.extremely_primordial_pearl", "极元始珍珠");
        add("item.thaumaturgetweaks.akashic_record", "阿卡西记录");

        add("enchantment.thaumaturge.arcing.desc", "类似风雷剑，当你使用带有该附魔的武器进行攻击时可以触发连锁闪电的特性，弹射的电弧会对周围的目标造成一半的伤害。电弧的目标数及范围随附魔等级而提升");
        add("enchantment.thaumaturge.burrowing.desc", "类似奔流斧，带有这项附魔的工具在破坏方块时会从距离你所挖掘方块最远的相同方块开始挖掘。");
        add("enchantment.thaumaturge.collector.desc", "这项附魔工具所开采的所有物品都会自动飘向你。");
        add("enchantment.thaumaturge.destructive.desc", "使用带有该附魔的工具破坏方块时会连带目标方块周围的8个可被该工具破坏的方块一起破坏。每个被破坏的方块都会按照正常开采来扣除工具耐久度。");
        add("enchantment.thaumaturge.essence.desc", "被带有该项附魔的武器所击杀的生物有几率掉落其所含要素对应的晶化源质。掉落概率及数量随附魔等级而提升。");
        add("enchantment.thaumaturge.lamplight.desc", "当你破坏一个方块时，如果该位置的亮度低于10，这个附魔将会在此处放置一个不可见的光源。");
        add("enchantment.thaumaturge.refining.desc", "类似炽心镐，这项附魔使你在开采矿石时偶尔会获得原矿簇。掉落概率随附魔等级而提升。");
        add("enchantment.thaumaturge.sounding.desc", "类似炽心镐，使用带有该附魔的工具右击方块以勘探附近的矿石。这项操作会消耗一些工具耐久度。");
    }
}
