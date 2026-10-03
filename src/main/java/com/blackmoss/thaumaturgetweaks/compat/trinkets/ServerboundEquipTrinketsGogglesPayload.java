package com.blackmoss.thaumaturgetweaks.compat.trinkets;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record ServerboundEquipTrinketsGogglesPayload() implements CustomPacketPayload {
    public static final ServerboundEquipTrinketsGogglesPayload INSTANCE = new ServerboundEquipTrinketsGogglesPayload();

    public static final Type<ServerboundEquipTrinketsGogglesPayload> TYPE =
            new Type<>(ThaumaturgeTweaks.identifier("equip_trinkets_goggles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundEquipTrinketsGogglesPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
