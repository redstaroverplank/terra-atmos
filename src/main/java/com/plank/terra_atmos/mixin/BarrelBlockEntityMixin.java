package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BarrelBlockEntity.class)
public abstract class BarrelBlockEntityMixin{
    @Inject(method = "serverTick", at = @At("TAIL"), remap = false)
    private static void Drip(Level level, BlockPos pos, BlockState state, BarrelBlockEntity barrel, CallbackInfo ci){
        final boolean sealed = state.getValue(BarrelBlock.SEALED);
        final Direction facing = state.getValue(BarrelBlock.FACING);
        if (!sealed && facing == Direction.UP && level.getGameTime() % 8L == 0L && level.isRainingAt(pos.above()))
        {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, Sounds.WATER_DRIP.get(), SoundSource.BLOCKS, ClimateRenderCache.INSTANCE.getRainfall() / 500, 1.0f);
        }
    }
}
