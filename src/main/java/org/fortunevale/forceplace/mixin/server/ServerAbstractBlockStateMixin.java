package org.fortunevale.forceplace.mixin.server;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.fortunevale.forceplace.Util;
import org.fortunevale.forceplace.client.ForceplaceClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin( value = AbstractBlock.AbstractBlockState.class )
public class ServerAbstractBlockStateMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void canPlaceAt(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir)
    {
        if (!Util.CheckIfQueued(pos))
            return;

        cir.setReturnValue(true);
        cir.cancel();
    }
}
