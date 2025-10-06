package com.plank.terra_atmos.utils;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec2;

import java.util.Optional;

public interface Geography {
    Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
    float windStrength = wind.length();
    static String getBiome(Level level, BlockPos blockPos){
        Optional<ResourceKey<Biome>> biomeKey = level.getBiome(blockPos).unwrapKey();
        return biomeKey.map(biomeResourceKey -> biomeResourceKey.location().getPath()).orElse("");
    }
    static float getTemperature(Level level, BlockPos blockPos){ return Climate.getTemperature(level, blockPos); }
    static float getRainfall(Level level, BlockPos blockPos){ return Climate.getRainfall(level, blockPos); }
}