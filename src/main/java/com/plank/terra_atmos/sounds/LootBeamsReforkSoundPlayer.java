package com.plank.terra_atmos.sounds;

import com.plank.terra_atmos.utils.Size;
import me.clefal.lootbeams.LootBeamsConstants;
import me.clefal.lootbeams.config.configs.SoundConfig;
import me.clefal.lootbeams.data.lbitementity.LBItemEntity;
import me.clefal.lootbeams.events.EntityRenderDispatcherHookEvent;
import me.clefal.lootbeams.modules.sound.SoundConfigHandler;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class LootBeamsReforkSoundPlayer {

    private static boolean canSound(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event) {
        if (SoundConfigHandler.checkInBlackList(event.LBItemEntity)) return false;
        return event.LBItemEntity.isRare();
    }

    //@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEnableModule(EntityRenderDispatcherHookEvent.RenderLootBeamEvent event) {
        if (event.LBItemEntity.isSounded()) return;
        ItemEntity itemEntity = event.LBItemEntity.item();

        if (event.LBItemEntity.canBeRender() == LBItemEntity.RenderState.REJECT) return;

        if (event.LBItemEntity.canBeRender() == LBItemEntity.RenderState.PASS && canSound(event))
        {

            WeighedSoundEvents sound = Minecraft.getInstance().getSoundManager().getSoundEvent(LootBeamsConstants.LOOT_DROP);
            ItemStack stack = itemEntity.getItem();
            IItemSize size = ItemSizeManager.get(stack);

            if (sound != null && Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.playSound(
                        Minecraft.getInstance().player,
                        itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        SoundEvent.createFixedRangeEvent(LootBeamsConstants.LOOT_DROP, 8.0f),
                        SoundSource.AMBIENT,
                        Size.volume(size.getWeight(stack)) * SoundConfig.soundConfig.sound.sound_volume.get(),
                        Size.pitch(size.getSize(stack)));
                event.LBItemEntity.updateSounded();
            }
        }
    }
}
