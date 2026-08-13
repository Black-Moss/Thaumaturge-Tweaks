// 研究门控提示生成：对标 Thaumaturge 本体 JEI 插件的 ResearchUtils。
// 当配方带有的 ResearchGate 未通过时，返回缺失研究的本地化名称列表，供 REI 显示。
package com.blackmoss.thaumaturgetweaks.compat.rei.utils;

import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.leclowndu93150.thaumaturge.api.research.IResearchEntry;
import com.leclowndu93150.thaumaturge.content.research.ResearchManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class ResearchUtils {

    private ResearchUtils() {
    }

    // 生成未通过研究门槛的研究条目名称列表。
    @NotNull
    public static List<Component> generateMissingResearchList(ResearchGate... research) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("jei.thaumaturge.research.missing_research")
                .withStyle(ChatFormatting.GOLD));

        Minecraft minecraft = Minecraft.getInstance();
        for (ResearchGate gate : research) {
            if (ResearchManager.doesPassGate(minecraft.player, gate)) {
                continue;
            }
            RegistryAccess access = minecraft.level == null
                    ? null
                    : minecraft.level.registryAccess();
            if (access == null) {
                list.add(Component.literal("- ")
                        .append(gate.entry().toString())
                        .withStyle(ChatFormatting.RED));
                continue;
            }
            IResearchEntry entry = access.lookup(IResearchEntry.REGISTRY_KEY)
                    .flatMap(registry -> registry.get(gate.entry()))
                    .map(Holder::value)
                    .orElse(null);
            if (entry != null) {
                list.add(Component.literal("- ")
                        .append(Component.translatable(entry.nameKey()))
                        .withStyle(ChatFormatting.RED));
            } else {
                list.add(Component.literal("- ")
                        .append(gate.entry().toString())
                        .withStyle(ChatFormatting.RED));
            }
        }
        return list;
    }
}
