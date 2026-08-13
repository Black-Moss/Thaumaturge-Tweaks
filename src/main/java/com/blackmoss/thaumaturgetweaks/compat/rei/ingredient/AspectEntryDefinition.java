package com.blackmoss.thaumaturgetweaks.compat.rei.ingredient;

import com.leclowndu93150.thaumaturge.api.aspect.AspectInstance;
import com.leclowndu93150.thaumaturge.api.aspect.IAspect;
import com.leclowndu93150.thaumaturge.content.item.PhialItem;
import com.mojang.serialization.Codec;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class AspectEntryDefinition implements EntryDefinition<AspectInstance> {
    public static final AspectEntryDefinition INSTANCE = new AspectEntryDefinition();
    public static final EntryType<AspectInstance> ENTRY_TYPE =
            EntryType.deferred(Identifier.parse(AspectEntryHelper.ENTRY_UID));

    private AspectEntryDefinition() {
    }

    @Override
    @NotNull
    public Class<AspectInstance> getValueType() {
        return AspectInstance.class;
    }

    @Override
    @NotNull
    public EntryType<AspectInstance> getType() {
        return ENTRY_TYPE;
    }

    @Override
    @NotNull
    public EntryRenderer<AspectInstance> getRenderer() {
        return AspectEntryRenderer.INSTANCE;
    }

    @Override
    @NotNull
    public Identifier getIdentifier(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return AspectEntryHelper.identifierOf(value.aspect());
    }

    @Override
    @NotNull
    public String getContainingNamespace(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return AspectEntryHelper.identifierOf(value.aspect()).getNamespace();
    }

    @Override
    public boolean isEmpty(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return value.aspect() == null || value.amount() <= 0;
    }

    @Override
    @NotNull
    public AspectInstance copy(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return new AspectInstance(value.aspect(), value.amount());
    }

    @Override
    @NotNull
    public AspectInstance normalize(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return value;
    }

    @Override
    @NotNull
    public AspectInstance wildcard(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return value;
    }

    @Override
    public long hash(@NotNull EntryStack<AspectInstance> entryStack,
                     @NotNull AspectInstance value,
                     @NotNull ComparisonContext context) {
        return AspectEntryHelper.getUid(value).hashCode();
    }

    @Override
    public boolean equals(@NotNull AspectInstance a, @NotNull AspectInstance b, @NotNull ComparisonContext context) {
        if (a == b) {
            return true;
        }
        return AspectEntryHelper.getUid(a).equals(AspectEntryHelper.getUid(b));
    }

    @Override
    @NotNull
    public EntrySerializer<AspectInstance> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    @NotNull
    public Component asFormattedText(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return AspectEntryHelper.getDisplayName(value);
    }

    @Override
    @NotNull
    public Stream<? extends TagKey<?>> getTagsFor(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        return Stream.empty();
    }

    @Override
    @NotNull
    public ItemStack cheatsAs(@NotNull EntryStack<AspectInstance> entryStack, @NotNull AspectInstance value) {
        Holder<IAspect> holder = value.aspect();
        if (holder == null || !holder.isBound()) {
            return ItemStack.EMPTY;
        }
        return PhialItem.makeFilled(holder);
    }

    private static final class Serializer implements EntrySerializer<AspectInstance> {
        private static final Serializer INSTANCE = new Serializer();

        private Serializer() {
        }

        @Override
        @NotNull
        public Codec<AspectInstance> codec() {
            return AspectInstance.CODEC;
        }

        @Override
        @NotNull
        public StreamCodec<RegistryFriendlyByteBuf, AspectInstance> streamCodec() {
            return AspectInstance.STREAM_CODEC;
        }
    }
}
