package org.fortunevale.forceplace.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import org.fortunevale.forceplace.Forceplace;
import org.fortunevale.forceplace.payloads.CheckForceplaceStatePayload;
import org.fortunevale.forceplace.payloads.ToggleForceplacePayload;

public class ForceplaceClient implements ClientModInitializer {
    public static Boolean localForcePlaceEnabled = false;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ToggleForceplacePayload.ID, (payload, context) -> {
            if (ForceplaceClient.localForcePlaceEnabled == payload.isEnabled())
                return;

            ForceplaceClient.localForcePlaceEnabled = payload.isEnabled();
            context.player().sendMessage(
                    Text.literal("Force place is now " + (ForceplaceClient.localForcePlaceEnabled ? "enabled" : "disabled") + "."));
        });

        ClientPlayNetworking.registerGlobalReceiver(CheckForceplaceStatePayload.ID, (payload, context) -> {
            if (payload.isRequest())
            {
                ForceplaceClient.localForcePlaceEnabled = false;

                Forceplace.LOGGER.error("Received check_forceplacestate request packet on client, this is not normal behaviour.");
                context.player().networkHandler.getConnection().disconnect(Text.literal("Received invalid packet."));
                return;
            }

            ForceplaceClient.localForcePlaceEnabled = payload.isEnabled() == 1;

            if (ForceplaceClient.localForcePlaceEnabled)
                context.player().sendMessage(Text.literal("Force place is enabled."));
        });

        ClientPlayConnectionEvents.JOIN.register((networkHandler, packetSender, minecraftClient) -> {
            ForceplaceClient.localForcePlaceEnabled = false;
            packetSender.sendPacket(new CheckForceplaceStatePayload(true, -1));
        });
    }
}
