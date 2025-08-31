package com.plank.terra_atmos.sounds.lootbeams;

import com.lootbeams.Configuration;
import com.lootbeams.LootBeams;
import com.lootbeams.compat.ApotheosisCompat;
import com.plank.terra_atmos.TerraAtmos;
import com.plank.terra_atmos.sounds.Sounds;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * 处理LootBeams声音播放的工具类
 * 替代原来的ClientSetupMixin
 */
@Mod.EventBusSubscriber(modid = TerraAtmos.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootBeamsSoundPlayer {

    public static int playSoundCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.START)) {
            if(playSoundCooldown > 0) playSoundCooldown--;
        }
    }
    /**
     * 播放物品掉落声音
     *
     * @param itemEntity 物品实体
     */
    public static void playDropSound(ItemEntity itemEntity) {
        if(playSoundCooldown != 0) return;
        if (!Configuration.SOUND.get()) {
            return;
        }

        Item item = itemEntity.getItem().getItem();
        ItemStack stack = itemEntity.getItem();
        IItemSize size = ItemSizeManager.get(stack);
        if ((Configuration.SOUND_ALL_ITEMS.get() && !isItemInRegistryList(Configuration.BLACKLIST.get(), item))
                || (Configuration.SOUND_ONLY_EQUIPMENT.get() && isEquipmentItem(item))
                || (Configuration.SOUND_ONLY_RARE.get() && compatRarityCheck(itemEntity, false))
                || isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item)) {
            WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeams.LOOT_DROP);
            if(sound != null && Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.playSound(
                        Minecraft.getInstance().player,
                        itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        SoundEvent.createFixedRangeEvent(LootBeams.LOOT_DROP, 8.0f),
                        SoundSource.AMBIENT,
                        Sounds.volume(size.getWeight(stack)) * Configuration.SOUND_VOLUME.get().floatValue(),
                        Sounds.pitch(size.getSize(stack)));
                playSoundCooldown = 3;
            }
        }
    }

    private static boolean isItemInRegistryList(List<String> registryNames, Item item) {
        if (registryNames.isEmpty()) {
            return false;
        }

        for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
            if (!id.contains(":") && ForgeRegistries.ITEMS.getKey(item).getNamespace().equals(id)) {
                return true;
            }

            ResourceLocation itemResource = ResourceLocation.tryParse(id);
            if (itemResource != null && ForgeRegistries.ITEMS.getValue(itemResource).asItem() == item.asItem()) {
                return true;
            }
        }

        return false;
    }

    static boolean compatRarityCheck(ItemEntity item, boolean shouldRender) {
        if (ModList.get().isLoaded("apotheosis")) {
            if (ApotheosisCompat.isApotheosisItem(item.getItem())) {
                shouldRender = !ApotheosisCompat.getRarityName(item.getItem()).contains("apotheosis:common") || item.getItem().getRarity() != Rarity.COMMON;
            } else if (item.getItem().getRarity() != Rarity.COMMON) {
                shouldRender = true;
            }
        } else {
            if (item.getItem().getRarity() != Rarity.COMMON) {
                shouldRender = true;
            }
        }
        return shouldRender;
    }

    public static boolean isEquipmentItem(Item item) {
        return item instanceof TieredItem || item instanceof ArmorItem || item instanceof ShieldItem || item instanceof BowItem || item instanceof CrossbowItem;
    }
}