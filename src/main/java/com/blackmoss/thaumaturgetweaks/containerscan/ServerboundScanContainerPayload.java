// 客户端 -> 服务器：请求扫描指定容器方块内的所有物品。
package com.blackmoss.thaumaturgetweaks.containerscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ServerboundScanContainerPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ServerboundScanContainerPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ThaumaturgeTweaks.MODID, "scan_container"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundScanContainerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    ServerboundScanContainerPayload::pos,
                    ServerboundScanContainerPayload::new);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
