package com.plank.terra_atmos.sounds;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.Random;

public class Wave {
    public Wave(){

    }
    public void playSound(Level level, Player player) {
        if (player.getY() > 50 && Math.random() <= 0.25 && level.dimension().location().getPath().equals("overworld") && player.getY() < 70) {
            if (level.isClientSide()) {
                final Random random = new Random();
                BlockPos centerPos = player.blockPosition();
                // 随机偏移，范围在±30格内
                int offsetX = random.nextInt(20) - 10;
                int offsetZ = random.nextInt(20) - 10;

                BlockPos checkPos = centerPos.offset(offsetX, 0, offsetZ).atY(62);
                // 检查是否为海岸
                if (isSaltWater(level, checkPos) && isCoast(level, checkPos)) {
                    // 播放波浪音效
                    level.playSound(
                            player,
                            checkPos.getX() + 0.5D,
                            63,
                            checkPos.getZ() + 0.5D,
                            Sounds.WAVE.get(),
                            SoundSource.AMBIENT,
                            0.8F,
                            (random.nextFloat() * 0.4F) + 0.8F
                    );
                    System.out.println("Wave!");
                }
            }
        }
    }
    private static boolean isCoast(Level level, BlockPos pos) {
        if (isSaltWater(level, pos.relative(Direction.DOWN))) return false;
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
            if (!isSaltWater(level, pos.relative(direction))) {
                count++;
            }
            if (count == 2) return true;
        }
        return false;
    }
    private static boolean isSaltWater(Level level, BlockPos pos) {
        // 优先检查方块状态
        BlockState blockState = level.getBlockState(pos);
        // 检查是否是液体方块
        if (blockState.getBlock() instanceof LiquidBlock) {
            FluidState fluidState = level.getFluidState(pos);
            if (fluidState.isEmpty()) {
                return false;
            }
            return fluidState.getFluidType() == TFCFluids.SALT_WATER.type().get();
        }
        return false;
    }
}
