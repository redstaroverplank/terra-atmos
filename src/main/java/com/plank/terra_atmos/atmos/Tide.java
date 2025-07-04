package com.plank.terra_atmos.atmos;

import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.client.ClientHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class Tide {
    private static float moodiness = 0.0f;
    private static final LocalPlayer player = (LocalPlayer) ClientHelpers.getPlayer();
    public Tide() {
        if (player != null) {
            Level level = player.level();
            RandomSource random = level.getRandom();
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();
            BlockPos blockPos = BlockPos.containing(px + (double)random.nextInt(17) - 8, py + (double)random.nextInt(17) - 8, pz + (double)random.nextInt(17) - 8);
            int brightness = level.getBrightness(LightLayer.SKY, blockPos);
            float magnification = 0.01f;
            if(py>=60) {
                if (brightness > 5) moodiness += (float) brightness * magnification / 15;
                else moodiness = Math.max(moodiness - (magnification / 20), 0.0f);
            }
            if (moodiness >= 1.0F) {
                double dx = (double)blockPos.getX() + 0.5D - px;
                double dy = (double)blockPos.getY() + 0.5D - py;
                double dz = (double)blockPos.getZ() + 0.5D - pz;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double v = (distance + 2) * distance;
                SoundManager manager = Minecraft.getInstance().getSoundManager();
                Holder<Biome> biomeHolder = level.getBiome(blockPos);
                Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
                SoundEvent sound = null;
                if(biomeKey.isPresent()){
                    sound = switch (biomeKey.get().location().getPath()) {
                        case "lake", "river" -> Sounds.WATER.get();
                        case "shore", "tidal_flats", "ocean", "ocean_reef", "deep_ocean", "deep_ocean_trench" ->
                                Sounds.WAVE.get();
                        default -> null;
                    };
                }
                if (sound != null) {
                    manager.play(new SimpleSoundInstance(sound, SoundSource.AMBIENT, 1.0f, 1.0F, random, px + dx / v, py + dy / v, pz + dz / v));
                }
                moodiness = 0.0F;
            }
        }

    }
}
