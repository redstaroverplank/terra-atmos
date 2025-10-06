package com.plank.terra_atmos.sounds;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Sounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, "terra_atmos");
    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("terra_atmos", name)));
    }
    public static final RegistryObject<SoundEvent> SPRING_WIND = create("ambient.wind.spring");
    public static final RegistryObject<SoundEvent> SUMMER_WIND = create("ambient.wind.summer");
    public static final RegistryObject<SoundEvent> FALL_WIND = create("ambient.wind.fall");
    public static final RegistryObject<SoundEvent> WINTER_WIND = create("ambient.wind.winter");
    public static final RegistryObject<SoundEvent> LAKE_WIND = create("ambient.wind.lake");

    public static final RegistryObject<SoundEvent> WAVE = create("ambient.wave");
    public static final RegistryObject<SoundEvent> BIRD = create("ambient.bird");
    public static final RegistryObject<SoundEvent> CICADA = create("ambient.cicada");
    public static final RegistryObject<SoundEvent> KATYDID = create("ambient.katydid");
    public static final RegistryObject<SoundEvent> SEAGULL = create("ambient.seagull");
    public static final RegistryObject<SoundEvent> COASTBIRD = create("ambient.coastbird");

    public static final RegistryObject<SoundEvent> WATER_AMBIENT = create("block.water.ambient");
    public static final RegistryObject<SoundEvent> WATER_DRIP = create("block.water.drip");

    public static final RegistryObject<SoundEvent> RANGED_WEAPON = create("entity.player.draw.weapon.ranged");
    public static final RegistryObject<SoundEvent> MELEE_WEAPON = create("entity.player.draw.weapon.melee");
    public static final RegistryObject<SoundEvent> TOOL = create("entity.player.draw.tool");
    public static final RegistryObject<SoundEvent> ARMOR = create("entity.player.draw.armor");
    public static final RegistryObject<SoundEvent> FOOD = create("entity.player.draw.food");
    public static final RegistryObject<SoundEvent> DEFAULT = create("entity.player.draw.generic");

}
