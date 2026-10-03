package com.blackmoss.thaumaturgetweaks.containerscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundScanContainerPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ServerboundScanContainerPayload> TYPE =
            new Type<>(ThaumaturgeTweaks.rl("scan_container"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundScanContainerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    ServerboundScanContainerPayload::pos,
                    ServerboundScanContainerPayload::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
