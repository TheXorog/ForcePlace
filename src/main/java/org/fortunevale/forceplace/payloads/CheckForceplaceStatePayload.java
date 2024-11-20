package org.fortunevale.forceplace.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.fortunevale.forceplace.Constants;

import java.util.Optional;

public record CheckForceplaceStatePayload(Boolean isRequest, Integer isEnabled) implements CustomPayload {
    public static final Id<CheckForceplaceStatePayload> ID =
            new Id<>(Constants.CHECK_FORCEPLACESTATE.id());

    public static final PacketCodec<RegistryByteBuf, CheckForceplaceStatePayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, CheckForceplaceStatePayload::isRequest,
                              PacketCodecs.INTEGER, CheckForceplaceStatePayload::isEnabled,
                              CheckForceplaceStatePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
