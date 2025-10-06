package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.sounds.LootBeamsReforkSoundPlayer;
import me.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import me.clefal.lootbeams.modules.sound.SoundModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundModule.class)
public class SoundModuleMixin {
    @Inject(method = "onEnableModule", at = @At("HEAD"), remap = false, cancellable = true)
    private void onEnableModule(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event, CallbackInfo ci){
        LootBeamsReforkSoundPlayer.onEnableModule(event);
        ci.cancel();
    }
}
