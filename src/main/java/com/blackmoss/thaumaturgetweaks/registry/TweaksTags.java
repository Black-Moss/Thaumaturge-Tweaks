package com.blackmoss.thaumaturgetweaks.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TweaksTags {
    public static final TagKey<Item> CURIOS =
            TagKey.create(net.minecraft.core.registries.Registries.ITEM, Identifier.fromNamespaceAndPath("thaumaturge", "curios"));
}
