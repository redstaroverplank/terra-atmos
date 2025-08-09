package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.atmos.Cave;
import com.plank.terra_atmos.atmos.Tide;
import com.plank.terra_atmos.atmos.WildLife;
import net.dries007.tfc.client.ClientForgeEventHandler;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import com.plank.terra_atmos.Sounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientForgeEventHandler.class)
public abstract class ClientForgeEventHandlerMixin
{
    @Inject(method = "tickWind", at = @At("HEAD"), remap = false, cancellable = true)
    private static void wind(CallbackInfo ci)
    {
        final Level level = ClientHelpers.getLevel();
        final Player player = ClientHelpers.getPlayer();
        if (player != null && level != null && level.getGameTime() % 2 == 0)
        {
            Season season = Calendars.get(level.isClientSide).getCalendarMonthOfYear().getSeason();
            final BlockPos pos = player.blockPosition();
            final Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
            SoundEvent soundEvent = switch (season) {
                case SPRING -> Sounds.SPRING_WIND.get();
                case SUMMER -> Sounds.SUMMER_WIND.get();
                case FALL -> Sounds.FALL_WIND.get();
                case WINTER -> Sounds.WINTER_WIND.get();
            };
            final float windStrength = wind.length();
            int count;
            if (windStrength > 0.3f) count = (int) (windStrength * 8);
            else if (player.getVehicle() != null) count = 2;
            else count = (int) (windStrength * 4);
            if (count == 0) return;
            final double xBias = wind.x > 0 ? 6 : -6;
            final double zBias = wind.y > 0 ? 6 : -6;
            final ParticleOptions particle = ClimateRenderCache.INSTANCE.getTemperature() < 0f && level.getRainLevel(0) > 0 ? TFCParticles.SNOWFLAKE.get() : TFCParticles.WIND.get();
            for (int i = 0; i < count; i++)
            {
                final double x = pos.getX() + Mth.nextDouble(level.random, -12 - xBias, 12 - xBias);
                final double y = pos.getY() + Mth.nextDouble(level.random, -1, 6);
                final double z = pos.getZ() + Mth.nextDouble(level.random, -12 - zBias, 12 - zBias);
                if (level.canSeeSky(BlockPos.containing(x, y, z)))
                {
                    level.addParticle(particle, x, y, z, 0D, 0D, 0D);
                    // 如果应该播放声音，在粒子位置播放
                    if (level.dimension().location().getPath().equals("overworld") && y >= 60 && level.getGameTime() % 40 == 0 && i == 0 && windStrength >= Math.random()) {
                        float volume = Mth.clamp(windStrength * 0.5f, 0.25f, 0.75f);
                        level.playLocalSound(x, y, z, soundEvent, SoundSource.AMBIENT, volume, 1.0f, false);
                    }
                }
            }
        }
        // 每10秒（200 tick）执行一次
        if (level != null && level.getGameTime() % 200 == 0) {
            new Cave();
            new WildLife();
            new Tide();
        }
        ci.cancel();
    }
}