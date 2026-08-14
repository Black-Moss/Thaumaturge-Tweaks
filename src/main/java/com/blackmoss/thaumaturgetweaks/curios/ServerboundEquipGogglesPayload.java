// 客户端 -> 服务器：请求将手持的揭示之护目镜装备到饰品栏 head 槽。
// 服务端做权威校验与放入，结果通过 Curios 槽数据同步回客户端。
package com.blackmoss.thaumaturgetweaks.curios;

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
