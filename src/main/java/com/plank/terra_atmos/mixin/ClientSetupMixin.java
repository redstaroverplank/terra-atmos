package com.plank.terra_atmos.mixin;

import com.lootbeams.ClientSetup;
import com.lootbeams.Configuration;
import com.lootbeams.LootBeams;
import com.lootbeams.compat.ApotheosisCompat;
import com.plank.terra_atmos.Sounds;
import net.dries007.tfc.common.capabilities.size.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(ClientSetup.class)
public class ClientSetupMixin {
    @Inject(method = "playDropSound", at = @At("HEAD"), remap = false, cancellable = true)
    private static void playDropSound(ItemEntity itemEntity, CallbackInfo ci) {
        if (!Configuration.SOUND.get()) {
            return;
        }

        Item item = itemEntity.getItem().getItem();
        ItemStack stack = item.getDefaultInstance();
        IItemSize ISize = ItemSizeManager.get(stack);
        if ((Configuration.SOUND_ALL_ITEMS.get() && !terra_atmos$isItemInRegistryList(Configuration.BLACKLIST.get(), item))
                || (Configuration.SOUND_ONLY_EQUIPMENT.get() && ClientSetup.isEquipmentItem(item))
                || (Configuration.SOUND_ONLY_RARE.get() && terra_atmos$compatRarityCheck(itemEntity))
                || terra_atmos$isItemInRegistryList(Configuration.SOUND_ONLY_WHITELIST.get(), item)) {
            itemEntity.level().playSound(
                    itemEntity,
                    itemEntity.blockPosition(),
                    SoundEvent.createFixedRangeEvent(LootBeams.LOOT_DROP, 8),
                    SoundSource.AMBIENT,
                    Sounds.volume(ISize.getWeight(stack)),
                    Sounds.pitch(ISize.getSize(stack))
            );
        }
        ci.cancel();
    }
    @Unique
    private static boolean terra_atmos$isItemInRegistryList(List<String> registryNames, Item item) {
        if (registryNames.isEmpty()) {
            return false;
        }

        for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
            if (!id.contains(":") && Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getNamespace().equals(id)) {
                return true;
            }

            ResourceLocation itemResource = ResourceLocation.tryParse(id);
            if (itemResource != null && Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(itemResource)).asItem() == item.asItem()) {
                return true;
            }
        }

        return false;
    }
    @Unique
    private static boolean terra_atmos$compatRarityCheck(ItemEntity item) {
        return item.getItem().getRarity() != Rarity.COMMON || (ModList.get().isLoaded("apotheosis") && ApotheosisCompat.isApotheosisItem(item.getItem()) && !ApotheosisCompat.getRarityName(item.getItem()).equals("common"));
    }
}
