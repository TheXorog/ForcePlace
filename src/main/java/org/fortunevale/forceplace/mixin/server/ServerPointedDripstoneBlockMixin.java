package org.fortunevale.forceplace.mixin.server;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import org.fortunevale.forceplace.Util;
import org.fortunevale.forceplace.client.ForceplaceClient;
import org.fortunevale.forceplace.payloads.ForceplacePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( value = PointedDripstoneBlock.class )
public class ServerPointedDripstoneBlockMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void canPlaceAt(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!Util.CheckIfQueued(pos))
            return;

        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(method = "canPlaceAtWithDirection", at = @At("HEAD"), cancellable = true)
    private static void canPlaceAtWithDirection(WorldView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir)
    {
        if (!Util.CheckIfQueued(pos))
            return;

        cir.setReturnValue(true);
        cir.cancel();
    }


}
