package com.blackmoss.thaumaturgetweaks.compat.rei.utils;

import com.leclowndu93150.thaumaturge.api.recipe.ResearchGate;
import com.leclowndu93150.thaumaturge.api.research.IResearchEntry;
import com.leclowndu93150.thaumaturge.content.research.ResearchManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ResearchUtils {
    private ResearchUtils() {
    }

    public static <T> List<Holder.Reference<T>> listAll(
            RegistryAccess access, ResourceKey<? extends Registry<? extends T>> key) {
        return access.registry(key)
                .map(registry -> registry.holders().toList())
                .orElse(List.of());
    }

    public static <T> Optional<T> find(
            RegistryAccess access, ResourceKey<? extends Registry<? extends T>> key, ResourceLocation id) {
        return access.registry(key)
                .flatMap(registry -> registry.getHolder(id))
                .map(Holder::value);
    }

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
            IResearchEntry entry = find(access, IResearchEntry.REGISTRY_KEY, gate.entry()).orElse(null);
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
