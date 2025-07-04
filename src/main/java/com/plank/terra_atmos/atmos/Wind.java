package com.plank.terra_atmos.atmos;

import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Wind {
    private static float moodiness = 0.0f;
    private SoundEvent sound;
    private static final LocalPlayer player = (LocalPlayer) ClientHelpers.getPlayer();
    public Season season = Calendars.get(player.level().isClientSide).getCalendarMonthOfYear().getSeason();
    public Wind() {
        Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        Level level = player.level();
        RandomSource random = level.getRandom();
        double px = player.getX();
        double py = player.getEyeY();
        double pz = player.getZ();
        float force = (float)Math.sqrt((wind.x * wind.x) + (wind.y * wind.y));
        BlockPos blockPos = BlockPos.containing(px + (double)random.nextInt(17) - 8, py + (double)random.nextInt(17) - 8, pz + (double)random.nextInt(17) - 8);
        int brightness = level.getBrightness(LightLayer.SKY, blockPos);
        float magnification = 0.005f;
        if(py >= 60) {
            if (brightness > 5) moodiness += (float) brightness * force * magnification / 15;
            else moodiness = Math.max(moodiness - (magnification / 20), 0.0f);
        }
        else {
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
            switch (season){
                case SPRING:
                    sound = Sounds.SPRING_WIND.get();
                    break;
                case SUMMER:
                    sound = Sounds.SUMMER_WIND.get();
                    break;
                case FALL:
                    sound = Sounds.FALL_WIND.get();
                    break;
                case WINTER:
                    sound = Sounds.WINTER_WIND.get();
            }
            if (py<64) sound = Sounds.CAVE.get();
            SimpleSoundInstance sound = new SimpleSoundInstance(
                    this.sound,
                    SoundSource.AMBIENT,
                    1.0f,
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
