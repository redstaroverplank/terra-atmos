package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.sounds.Wave;
import com.plank.terra_atmos.sounds.WildLife;
import com.plank.terra_atmos.utils.Geography;
import net.dries007.tfc.client.ClientForgeEventHandler;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import com.plank.terra_atmos.sounds.Sounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientForgeEventHandler.class)
public abstract class ClientForgeEventHandlerMixin implements Geography {
    @Inject(method = "tickWind", at = @At("HEAD"), remap = false, cancellable = true)
    private static void wind(CallbackInfo ci)
    {
        final Level level = ClientHelpers.getLevel();
        final Player player = ClientHelpers.getPlayer();
        if (level != null && level.getGameTime() % 2 == 0 && player != null)
        {
            Season season = Calendars.get(level.isClientSide).getCalendarMonthOfYear().getSeason();
            final BlockPos blockPos = player.blockPosition();
            SoundEvent sound = switch (season) {
                case SPRING -> Sounds.SPRING_WIND.get();
                case SUMMER -> Sounds.SUMMER_WIND.get();
                case FALL -> Sounds.FALL_WIND.get();
                case WINTER -> Sounds.WINTER_WIND.get();
            };
            int count;
            if (windStrength > 0.25f) count = (int) (windStrength * 8);
            else if (player.getVehicle() != null) count = 2;
            else count = (int) (windStrength * 4);
            if (count == 0) return;
            final double xBias = wind.x > 0 ? 6 : -6;
            final double zBias = wind.y > 0 ? 6 : -6;
            final ParticleOptions particle = ClimateRenderCache.INSTANCE.getTemperature() < 0f && level.getRainLevel(0) > 0 ? TFCParticles.SNOWFLAKE.get() : TFCParticles.WIND.get();
            for (int i = 0; i < count; i++)
            {
                final double x = blockPos.getX() + Mth.nextDouble(level.random, -12 - xBias, 12 - xBias);
                final double y = blockPos.getY() + Mth.nextDouble(level.random, -1, 6);
                final double z = blockPos.getZ() + Mth.nextDouble(level.random, -12 - zBias, 12 - zBias);
                if (level.canSeeSky(BlockPos.containing(x, y, z)))
                {
                    level.addParticle(particle, x, y, z, 0D, 0D, 0D);
                    // 如果应该播放声音，在粒子位置播放
                    if (level.dimension().location().getPath().equals("overworld") && windStrength / 20 >= Math.random() && blockPos.getY() >= 60 && i == 0) {
                        level.playSound(player, x, y, z, sound, SoundSource.AMBIENT, windStrength * 0.5f + 0.25f, 1.0f);
                        switch (Geography.getBiome(level, blockPos)) {
                            case "lake", "river":
                                level.playSound(player, x, y, z, Sounds.LAKE_WIND.get(), SoundSource.AMBIENT, windStrength * 0.5f + 0.25f, 1.0f);
                                break;
                            default: return;
                        }
                    }
                }
            }
            new WildLife().playSound(level, player);
            new Wave().playSound(level, player);
        }
        ci.cancel();
    }
}