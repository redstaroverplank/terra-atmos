package com.plank.terra_atmos;

import com.plank.terra_atmos.sounds.Sounds;
import com.plank.terra_atmos.utils.ClientDelayHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TerraAtmos.MODID)
public class TerraAtmos {
    public static final String MODID = "terra_atmos";
    public TerraAtmos() {
        @SuppressWarnings("removal")
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        Sounds.SOUNDS.register(bus);
        ClientDelayHandler.init();
    }
}
