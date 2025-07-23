package com.plank.terra_atmos.mixin;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.particle.WindParticle;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import com.plank.terra_atmos.Sounds;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WindParticle.class)
public abstract class WindParticleMixin extends TextureSheetParticle
{
    @Unique
    private int terra_atmos$nextSoundTick;
    protected WindParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        // 设置初始声音触发时间（20-40 tick 的随机延迟）
        this.terra_atmos$nextSoundTick = age + 20 + random.nextInt(20);
    }
    @Inject(method = "m_5989_", at = @At("TAIL"), remap = false)
    private void onTick(CallbackInfo ci)
    {
        Season season = Calendars.get(level.isClientSide).getCalendarMonthOfYear().getSeason();
        Vec2 wind = ClimateRenderCache.INSTANCE.getWind();
        float force = (float)Math.sqrt((wind.x * wind.x) + (wind.y * wind.y));
        SoundEvent soundEvent = switch (season) {
            case SPRING -> Sounds.SPRING_WIND.get();
            case SUMMER -> Sounds.SUMMER_WIND.get();
            case FALL -> Sounds.FALL_WIND.get();
            case WINTER -> Sounds.WINTER_WIND.get();
        };
        if (age >= terra_atmos$nextSoundTick)
        {
            // 30% 概率触发风声
            if (random.nextFloat() < 0.3f)
            {
                SimpleSoundInstance sound = new SimpleSoundInstance(
                        soundEvent,
                        SoundSource.AMBIENT,
                        1.0F + force,
                        1.0F, random, x, y, z);
                Minecraft.getInstance().getSoundManager().play(sound);
            }
            // 设置下次触发时间（20-40 tick 后）
            terra_atmos$nextSoundTick = age + 20 + random.nextInt(20);
        }
    }
}