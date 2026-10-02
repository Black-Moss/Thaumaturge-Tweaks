package com.blackmoss.thaumaturgetweaks.compat.curios;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ServerboundEquipGogglesPayload() implements CustomPacketPayload {
    public static final ServerboundEquipGogglesPayload INSTANCE = new ServerboundEquipGogglesPayload();

    public static final Type<ServerboundEquipGogglesPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ThaumaturgeTweaks.MODID, "equip_goggles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEquipGogglesPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
