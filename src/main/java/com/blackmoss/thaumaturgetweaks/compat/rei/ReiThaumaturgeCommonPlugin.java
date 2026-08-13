package com.blackmoss.thaumaturgetweaks.compat.rei;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryTypeRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.forge.REIPluginCommon;

@REIPluginCommon
public final class ReiThaumaturgeCommonPlugin implements REICommonPlugin {
    @Override
    public void registerEntryTypes(EntryTypeRegistry registry) {
        registry.register(AspectEntryDefinition.ENTRY_TYPE, AspectEntryDefinition.INSTANCE);
    }
}
