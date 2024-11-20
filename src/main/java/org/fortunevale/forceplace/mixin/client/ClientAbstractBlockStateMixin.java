package org.fortunevale.forceplace.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.fortunevale.forceplace.client.ForceplaceClient;
import org.fortunevale.forceplace.payloads.ForceplacePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin( value = AbstractBlock.AbstractBlockState.class )
public class ClientAbstractBlockStateMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void canPlaceAt(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!ForceplaceClient.localForcePlaceEnabled)
            return;

        ClientPlayNetworking.send(new ForceplacePayload(pos));

        cir.setReturnValue(true);
        cir.cancel();
    }
}
