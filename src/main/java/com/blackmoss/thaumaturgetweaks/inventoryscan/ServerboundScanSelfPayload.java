// 客户端 -> 服务器：请求扫描玩家自身。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ServerboundScanSelfPayload() implements CustomPacketPayload {
    public static final ServerboundScanSelfPayload INSTANCE = new ServerboundScanSelfPayload();

    public static final Type<ServerboundScanSelfPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ThaumaturgeTweaks.MODID, "scan_self"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundScanSelfPayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
