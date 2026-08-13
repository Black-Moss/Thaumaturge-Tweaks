package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.leclowndu93150.thaumaturge.api.aspect.AspectComponents;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AspectEntryHelper {
    public static final String ENTRY_UID = "thaumaturgetweaks:aspect";

    private static final Identifier UNKNOWN = Identifier.fromNamespaceAndPath("thaumaturgetweaks", "unknown");

    private AspectEntryHelper() {
    }

    @Nullable
    public static ResourceKey<IAspect> keyOf(@Nullable Holder<IAspect> aspect) {
        if (aspect == null) {
            return null;
        }
        return aspect.unwrapKey().orElse(null);
    }

    @NotNull
    public static Identifier identifierOf(@Nullable Holder<IAspect> aspect) {
        ResourceKey<IAspect> key = keyOf(aspect);
        return key == null
                ? UNKNOWN
                : key.identifier();
    }

    @NotNull
    public static String getUid(@NotNull AspectInstance instance) {
        Holder<IAspect> holder = instance.aspect();
        return identifierOf(holder) + "@" + instance.amount();
    }

    @NotNull
    public static Component getDisplayName(@NotNull AspectInstance instance) {
        Holder<IAspect> holder = instance.aspect();
        if (holder == null || !holder.isBound()) {
            return Component.literal("Unknown Aspect");
        }
        return AspectComponents.name(holder);
    }

    public static int compare(@Nullable AspectInstance a, @Nullable AspectInstance b) {
        if (a == b) {
            return 0;
        }
        if (a == null) {
            return -1;
        }
        if (b == null) {
            return 1;
        }
        ResourceKey<IAspect> ka = keyOf(a.aspect());
        ResourceKey<IAspect> kb = keyOf(b.aspect());
        int keyCompare = String.valueOf(ka).compareTo(String.valueOf(kb));
        if (keyCompare != 0) {
            return keyCompare;
        }
        return Integer.compare(a.amount(), b.amount());
    }
}
