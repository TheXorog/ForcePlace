package org.fortunevale.forceplace;

import me.lucko.fabric.api.permissions.v0.PermissionCheckEvent;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.fortunevale.forceplace.payloads.CheckForceplaceStatePayload;
import org.fortunevale.forceplace.payloads.ForceplacePayload;
import org.fortunevale.forceplace.payloads.ToggleForceplacePayload;
import org.fortunevale.forceplace.simplifiedClasses.SimpleBlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static net.minecraft.server.command.CommandManager.*;


public class Forceplace implements ModInitializer {
    public final static ConcurrentHashMap<SimpleBlockPos, Integer> queuedBlocks = new ConcurrentHashMap<>();
    public final static ArrayList<String> enabledForcePlaceUsers = new ArrayList<>();
    public final static ArrayList<String> hasModUsers = new ArrayList<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_ID);

    @Override
    public void onInitialize() {
        this.registerPacket(ToggleForceplacePayload.ID, ToggleForceplacePayload.CODEC);
        this.registerPacket(ForceplacePayload.ID, ForceplacePayload.CODEC);
        this.registerPacket(CheckForceplaceStatePayload.ID, CheckForceplaceStatePayload.CODEC);

        PermissionCheckEvent.EVENT.register((uuid, permission) ->
        {
            if (permission.equalsIgnoreCase(Constants.UsePermission) && uuid.hasPermissionLevel(2))
                return TriState.TRUE;

            return TriState.DEFAULT;
        });

        ServerTickEvents.END_SERVER_TICK.register((minecraftServer) -> {
            List<SimpleBlockPos> keysToRemove = new ArrayList<>();
            for (Map.Entry<SimpleBlockPos, Integer> pos : queuedBlocks.entrySet())
            {
                queuedBlocks.put(pos.getKey(), pos.getValue() + 1);

                if (pos.getValue() >= 1)
                {
                    keysToRemove.add(pos.getKey());
                }
            }

            for (SimpleBlockPos remove : keysToRemove)
            {
                queuedBlocks.remove(remove);
            }
        });

        ServerPlayConnectionEvents.INIT.register((networkHandler, minecraftServer) -> {
            ServerPlayerEntity player = networkHandler.getPlayer();
            String currentUuid = player.getUuid().toString();

            hasModUsers.remove(currentUuid);

            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!hasModUsers.contains(currentUuid))
                    enabledForcePlaceUsers.remove(currentUuid);
            }).start();
        });

        ServerPlayNetworking.registerGlobalReceiver(ForceplacePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            String currentUuid = player.getUuid().toString();

            if (!enabledForcePlaceUsers.contains(currentUuid) || !Permissions.check(player, Constants.UsePermission))
            {
                Forceplace.LOGGER.warn("{}'s ForcePlace state was no longer synced, re-syncing..", player.getName().getString());
                context.responseSender().sendPacket(new CheckForceplaceStatePayload(false, 0));
                return;
            }

            if (!player.isInCreativeMode())
            {
                enabledForcePlaceUsers.remove(currentUuid);

                context.player().sendMessage(Text.literal("You can only force-place blocks in creative mode."));
                context.responseSender().sendPacket(new CheckForceplaceStatePayload(false, 0));
                return;
            }

            SimpleBlockPos simple = new SimpleBlockPos(payload.blockPos());

            queuedBlocks.put(simple, 0);
        });

        ServerPlayNetworking.registerGlobalReceiver(CheckForceplaceStatePayload.ID, (payload, context) -> {
            if (!payload.isRequest())
            {
                Forceplace.LOGGER.error("Received check_forceplacestate response packet on server, this is not normal behaviour.");
                context.player().networkHandler.disconnect(Text.literal("Received invalid packet."));
                return;
            }

            ServerPlayerEntity player = context.player();
            String currentUuid = player.getUuid().toString();

            hasModUsers.add(currentUuid);

            context.responseSender().sendPacket(
                    new CheckForceplaceStatePayload(false, enabledForcePlaceUsers.contains(currentUuid) ? 1 : 0));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)
                -> dispatcher.register(literal("toggleforceplace")
                .requires(source -> {
                    return Permissions.check(source, Constants.UsePermission) && source.isExecutedByPlayer();
                })
                .executes(context -> {
                    if (!context.getSource().isExecutedByPlayer())
                    {
                        context.getSource().sendFeedback(() -> Text.literal("This command must be run as player."), false);
                        return 1;
                    }

                    ServerPlayerEntity requestingPlayer = context.getSource().getPlayer();
                    assert requestingPlayer != null;
                    String currentUuid = requestingPlayer.getUuid().toString();

                    if (!hasModUsers.contains(currentUuid))
                    {
                        context.getSource().sendFeedback(() -> Text.literal("You do not have the ForcePlace mod installed."), false);
                        return 1;
                    }

                    if (!requestingPlayer.isInCreativeMode())
                    {
                        requestingPlayer.sendMessage(Text.literal("You need to be in creative mode to use this command."));
                        return 1;
                    }

                    if (!enabledForcePlaceUsers.contains(currentUuid))
                        enabledForcePlaceUsers.add(currentUuid);
                    else
                        enabledForcePlaceUsers.remove(currentUuid);

                    for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList())
                    {
                        ServerPlayNetworking.send(player, new ToggleForceplacePayload(enabledForcePlaceUsers.contains(currentUuid)));
                    }
                    return 1;
                })));
    }

    public void registerPacket(CustomPayload.Id var1, PacketCodec var2)
    {
        PayloadTypeRegistry.playS2C().register(var1, var2);
        PayloadTypeRegistry.playC2S().register(var1, var2);
    }
}
