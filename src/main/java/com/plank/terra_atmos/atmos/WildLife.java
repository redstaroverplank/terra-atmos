package com.plank.terra_atmos.atmos;

import com.plank.terra_atmos.Sounds;
import com.plank.terra_atmos.TerraAtmos;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.climate.Climate;
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
public class WildLife {
    private static float moodiness = 0.0f;
    private static final LocalPlayer player = (LocalPlayer) ClientHelpers.getPlayer();
    public WildLife() {
        if (player != null) {
            Level level = player.level();
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();
            RandomSource random = level.getRandom();
            BlockPos blockPos = BlockPos.containing(px + (double)random.nextInt(17) - 8, py + (double)random.nextInt(17) - 8, pz + (double)random.nextInt(17) - 8);
            int brightness = level.getBrightness(LightLayer.SKY, blockPos);
            float magnification = 0.002f;
            if (brightness > 5) moodiness += (float)brightness * magnification / 15;
            else moodiness = Math.max(moodiness - (magnification / 20), 0.0f);
            if (moodiness >= 1.0F) {
                double dx = (double)blockPos.getX() + 0.5D - px;
                double dy = (double)blockPos.getY() + 0.5D - py;
                double dz = (double)blockPos.getZ() + 0.5D - pz;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double v = (distance + 2) * distance;
                SoundManager manager = Minecraft.getInstance().getSoundManager();
                float temperature = Climate.getTemperature(level, blockPos);
                Holder<Biome> biomeHolder = level.getBiome(blockPos);
                Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
                SoundEvent sounds = null;
                if(biomeKey.isPresent()){
                    switch (biomeKey.get().location().getPath()) {
                        case "shore":
                        case "tidal_flats":
                        case "ocean":
                        case "ocean_reef":
                            sounds = Sounds.SEABIRD.get();
                            break;
                        default:
                            if(temperature >= 15.0f && temperature <= 25.0f ){
                                sounds = Sounds.SPRING.get();
                            }
                            if(temperature >= 25.0f){
                                sounds = Sounds.SUMMER.get();
                            }
                    }
                }
                if(sounds!=null) {
                    SimpleSoundInstance sound = new SimpleSoundInstance(
                            sounds,
                            SoundSource.AMBIENT,
                            1.0f,
                            1.0F,
                            random,
                            px + dx / v,
                            py + dy / v,
                            pz + dz / v);
                    manager.play(sound);
                    TerraAtmos.ClientDelayHandler.schedule(20, () -> manager.play(sound));
                    TerraAtmos.ClientDelayHandler.schedule(40, () -> manager.play(sound));
                    TerraAtmos.ClientDelayHandler.schedule(60, () -> manager.play(sound));
                    TerraAtmos.ClientDelayHandler.schedule(80, () -> manager.play(sound));
                }
                moodiness = 0.0F;
            }
        }
    }
}
