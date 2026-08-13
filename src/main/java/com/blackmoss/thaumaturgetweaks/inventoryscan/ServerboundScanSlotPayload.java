// 客户端 -> 服务器：请求扫描玩家打开容器中的某个槽位物品。
package com.blackmoss.thaumaturgetweaks.inventoryscan;

import com.blackmoss.thaumaturgetweaks.ThaumaturgeTweaks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerboundScanSlotPayload(int slotNumber) implements CustomPacketPayload {
    public static final Type<ServerboundScanSlotPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(ThaumaturgeTweaks.MODID, "scan_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundScanSlotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    ServerboundScanSlotPayload::slotNumber,
                    ServerboundScanSlotPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
