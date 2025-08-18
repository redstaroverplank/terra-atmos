package com.plank.terra_atmos.mixin;

import com.plank.terra_atmos.Sounds;
import com.plank.terra_atmos.Tags;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.dries007.tfc.common.capabilities.size.IItemSize;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow public int selected;
    @Final
    @Shadow
    public Player player;
    @Unique
    private int terra_atmos$lastSelectedSlot = -1;
    @Unique
    private Item terra_atmos$item = null;
    @Inject(method = "tick", at = @At("HEAD"))
    private void onInventoryTick(CallbackInfo ci) {
        terra_atmos$detectWeaponSwitch();
    }
    @Unique
    private void terra_atmos$detectWeaponSwitch() {
        int currentSlot = this.selected;
        // 初始化
        if (terra_atmos$lastSelectedSlot == -1) {
            terra_atmos$lastSelectedSlot = currentSlot;
            return;
        }
        ItemStack currentItem = getItem(currentSlot);
        if (terra_atmos$item == null) {
            terra_atmos$item = currentItem.getItem();
            return;
        }
        // 检测槽位变化
        if (currentSlot != terra_atmos$lastSelectedSlot || terra_atmos$item != currentItem.getItem()) {
            SoundEvent sound = Sounds.DEFAULT.get();
            IItemSize ISize = ItemSizeManager.get(currentItem);

            if (currentItem.is(Tags.ARMOR)) sound = Sounds.ARMOR.get();
            if (currentItem.is(Tags.TOOL)) sound = Sounds.TOOL.get();
            if (currentItem.is(Tags.RANGED_WEAPON)) sound = Sounds.RANGED_WEAPON.get();
            if (currentItem.is(Tags.MELEE_WEAPON)) sound = Sounds.MELEE_WEAPON.get();
            if (currentItem.is(Tags.FOOD)) sound = Sounds.FOOD.get();
            if (!currentItem.isEmpty() && !(currentItem.getItem() instanceof AbstractGunItem)) {
                player.level().playSound(
                        null,
                        player.blockPosition(),
                        sound,
                        SoundSource.PLAYERS,
                        Sounds.volume(ISize.getWeight(currentItem)),
                        Sounds.pitch(ISize.getSize(currentItem))
                );
            }
            terra_atmos$lastSelectedSlot = currentSlot;
            terra_atmos$item = currentItem.getItem();
        }
    }
    @Shadow
    public abstract ItemStack getItem(int slot);
}