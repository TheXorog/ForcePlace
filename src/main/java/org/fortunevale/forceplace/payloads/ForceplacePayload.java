package org.fortunevale.forceplace.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import org.fortunevale.forceplace.Constants;

public record ForceplacePayload(BlockPos blockPos) implements CustomPayload {
    public static final Id<ForceplacePayload> ID =
            new Id<>(Constants.FORCEPLACE.id());

    public static final PacketCodec<RegistryByteBuf, ForceplacePayload> CODEC =
            PacketCodec.tuple(BlockPos.PACKET_CODEC, ForceplacePayload::blockPos, ForceplacePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
