package com.blackmoss.thaumaturgetweaks.compat.rei.category;

import com.blackmoss.thaumaturgetweaks.compat.rei.ingredient.AspectEntryDefinition;
import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

// REI Display 承载一份要素合成关系，供搜索与列表匹配。
public final class AspectCompositionDisplay implements Display {

    private final AspectCompositionCategory.Composition composition;

    AspectCompositionDisplay(AspectCompositionCategory.Composition composition) {
        this.composition = composition;
    }

    AspectCompositionCategory.Composition composition() {
        return composition;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return List.of(
                EntryIngredient.of(EntryStack.of(
                        AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(composition.left(), 1))),
                EntryIngredient.of(EntryStack.of(
                        AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(composition.right(), 1))));
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredient.of(EntryStack.of(
                AspectEntryDefinition.ENTRY_TYPE, new AspectInstance(composition.result(), 1))));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AspectCompositionCategory.ID;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.empty();
    }
}
