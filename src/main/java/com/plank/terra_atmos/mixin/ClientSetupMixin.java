package com.plank.terra_atmos.mixin;

import com.lootbeams.ClientSetup;
import com.plank.terra_atmos.sounds.lootbeams.LootBeamsSoundPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientSetup.class)
public class ClientSetupMixin {
    @Inject(method = "playDropSound", at = @At("HEAD"), remap = false, cancellable = true)
    private static void playDropSound(ItemEntity itemEntity, CallbackInfo ci) {
        LootBeamsSoundPlayer.playDropSound(itemEntity);
        ci.cancel();
    }
}