package org.fortunevale.forceplace;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.fortunevale.forceplace.payloads.CheckForceplaceStatePayload;
import org.fortunevale.forceplace.payloads.ToggleForceplacePayload;

public class Constants {
    public static final String MOD_ID = "forceplace";

    public static final CustomPayload.Id<ToggleForceplacePayload> TOGGLE_FORCEPLACE =
            new CustomPayload.Id<>(Identifier.of(MOD_ID, "toggle_forceplace"));

    public static final CustomPayload.Id<CheckForceplaceStatePayload> CHECK_FORCEPLACESTATE =
            new CustomPayload.Id<>(Identifier.of(MOD_ID, "check_forceplacestate"));

    public static final CustomPayload.Id<CheckForceplaceStatePayload> FORCEPLACE =
            new CustomPayload.Id<>(Identifier.of(MOD_ID, "forceplace"));

    public static final String UsePermission = "forceplace.use";
}
