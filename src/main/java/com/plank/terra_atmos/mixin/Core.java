package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.atmos.WildLife;
import com.plank.terra_atmos.atmos.Tide;
import com.plank.terra_atmos.atmos.Wind;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(LocalPlayer.class)
public class Core {
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci){
        new Wind();
        new WildLife();
        new Tide();
    }
}
