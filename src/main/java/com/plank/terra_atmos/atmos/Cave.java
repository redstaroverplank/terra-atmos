package com.plank.terra_atmos.atmos;

import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Cave {
    private static float moodiness = 0.0f;
    private static final LocalPlayer player = (LocalPlayer) ClientHelpers.getPlayer();
    public Cave() {
        Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        Level level;
        if (player != null) {
            level = player.level();
            RandomSource random = level.getRandom();
            double px = player.getX();
            double py = player.getEyeY();
            double pz = player.getZ();
            float force = (float)Math.sqrt((wind.x * wind.x) + (wind.y * wind.y));
            float voice = (float) (Math.random() * 0.5 + 0.25);
            BlockPos blockPos = BlockPos.containing(px + (double)random.nextInt(17) - 8, py + (double)random.nextInt(17) - 8, pz + (double)random.nextInt(17) - 8);
            int brightness = level.getBrightness(LightLayer.SKY, blockPos);
            float magnification = 1.0f;
            if(level.dimension().location().getPath().equals("overworld") && py <= 60) {
                if (brightness < 5) moodiness += (float) brightness * force * magnification / 15;
                else moodiness = Math.max(moodiness - (magnification / 20), 0.0f);
            }
            if (moodiness >= 1.0F) {
                double dx = (double)blockPos.getX() + 0.5D - px;
                double dy = (double)blockPos.getY() + 0.5D - py;
                double dz = (double)blockPos.getZ() + 0.5D - pz;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double v = (distance + 2) * distance;
                SoundManager manager = Minecraft.getInstance().getSoundManager();
                SimpleSoundInstance sound = new SimpleSoundInstance(
                        Sounds.CAVE.get(),
                        SoundSource.AMBIENT,
                        voice,
                        1.0F,
                        random,
                        px + dx / v,
                        py + dy / v,
                        pz + dz / v);
                manager.play(sound);
                moodiness = 0.0F;
            }
        }
    }
}