package org.fortunevale.forceplace.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.fortunevale.forceplace.Constants;

public record ToggleForceplacePayload(Boolean isEnabled) implements CustomPayload {
    public static final CustomPayload.Id<ToggleForceplacePayload> ID =
            new CustomPayload.Id<>(Constants.TOGGLE_FORCEPLACE.id());

    public static final PacketCodec<RegistryByteBuf, ToggleForceplacePayload> CODEC =
            PacketCodec.tuple(PacketCodecs.BOOL, ToggleForceplacePayload::isEnabled, ToggleForceplacePayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
