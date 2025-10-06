package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.sounds.Sounds;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FALLING;

@Mixin(WaterFluid.class)
public class WaterFluidMixin {
    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    public void tick(Level level, BlockPos pos, FluidState state, RandomSource source, CallbackInfo ci){
        if (!state.isSource() && !state.getValue(FALLING)) {
            if (source.nextInt(128) == 0) {
                level.playLocalSound(
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        Sounds.WATER_AMBIENT.get(),
                        SoundSource.BLOCKS,
                        source.nextFloat() * 0.25F + 0.75F,
                        source.nextFloat() + 0.5F,
                        false);
            }
        } else if (source.nextInt(10) == 0) {
            level.addParticle(
                    ParticleTypes.UNDERWATER,
                    pos.getX() + source.nextDouble(),
                    pos.getY() + source.nextDouble(),
                    pos.getZ() + source.nextDouble(),
                    0.0D,
                    0.0D,
                    0.0D);
        } else if(pos.getY() > 61 && state.getType() == TFCFluids.RIVER_WATER.get() && source.nextInt(256) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    Sounds.WATER_AMBIENT.get(),
                    SoundSource.BLOCKS,
                    0.75F,
                    0.5F,
                    false
            );
        } else if (isCoast(level, pos) && source.nextInt(16) == 0){
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    Sounds.WAVE.get(),
                    SoundSource.BLOCKS,
                    1,
                    1,
                    false
            );
        }
        ci.cancel();
    }
    @Unique
    private static boolean isCoast(Level level, BlockPos pos) {
        if (pos.getY() <= 60 || isSaltWater(level.getFluidState(pos.relative(Direction.DOWN)))) return false;
        int count = 0;
        // 检查5个方向：前、后、左、右、下
        Direction[] directionsToCheck = {
                Direction.DOWN,    // 下
                Direction.NORTH,  // 前
                Direction.SOUTH,  // 后
                Direction.EAST,   // 右
                Direction.WEST   // 左
        };
        for (Direction direction : directionsToCheck) {
            if (!isSaltWater(level.getFluidState(pos.relative(direction)))) {
                count++;
            }
            if (count == 2) return true;
        }
        return false;
    }
    @Unique
    private static boolean isSaltWater(FluidState state) {
        return state.getType() == TFCFluids.SALT_WATER.source().get();
    }
}
