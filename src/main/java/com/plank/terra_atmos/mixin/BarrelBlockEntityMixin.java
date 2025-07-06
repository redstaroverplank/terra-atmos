package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
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
        // 使用 API 方法安全获取流体数据
        barrel.getCapability(Capabilities.FLUID, null).ifPresent(fluidHandler -> {
            // 获取第一个流体槽的数据（桶只有一个流体槽）
            final FluidStack fluidStack = fluidHandler.getFluidInTank(0);
            final int waterAmount = fluidStack.getAmount();
            final int capacity = fluidHandler.getTankCapacity(0);
            // 根据水量比例计算音调 (0.0f - 1.0f 范围)
            float pitch = (float) waterAmount / capacity;
            // 获取降雨强度
            float rainfall = Climate.getRainfall(level, pos) / 500;
            if (!sealed && facing == Direction.UP && level.getGameTime() % 4L == 0L && level.isRainingAt(pos.above()) && Math.random() <= rainfall)
                level.playSound(null, pos, Sounds.WATER_DRIP.get(), SoundSource.BLOCKS, rainfall, pitch);
        });
    }
}
