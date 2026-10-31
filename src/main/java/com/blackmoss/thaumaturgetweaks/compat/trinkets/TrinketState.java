package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import net.neoforged.fml.ModList;

public final class TrinketState {
    public static final String MOD_ID = "trinkets_updated";

    private static final boolean LOADED = ModList.get().isLoaded(MOD_ID);

    private TrinketState() {
    }

    public static boolean isLoaded() {
        return LOADED;
    }
}
