package com.plank.terra_atmos;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class Tags {
    private static TagKey<Item> create(String id)
    {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TerraAtmos.MODID, id));
    }
    public static final TagKey<Item> RANGED_WEAPON = create("ranged_weapon");
    public static final TagKey<Item> MELEE_WEAPON = create("melee_weapon");
    public static final TagKey<Item> TOOL = create("tool");
    public static final TagKey<Item> ARMOR = create("armor");
    public static final TagKey<Item> FOOD = create("food");
}
