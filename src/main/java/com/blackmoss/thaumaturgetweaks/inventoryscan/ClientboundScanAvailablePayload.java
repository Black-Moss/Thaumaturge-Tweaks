// 服务器 -> 客户端：通知客户端服务器已安装本模组，可以发送扫描请求。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ClientboundScanAvailablePayload() implements CustomPacketPayload {
    public static final ClientboundScanAvailablePayload INSTANCE = new ClientboundScanAvailablePayload();

    public static final Type<ClientboundScanAvailablePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ThaumaturgeTweaks.MODID, "scan_available"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundScanAvailablePayload> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
