package com.plank.terra_atmos.utils;

import net.dries007.tfc.common.capabilities.size.Weight;
import net.minecraft.world.level.block.NoteBlock;

public interface Size {
     static float volume(Weight weight) {
        // 每级重量增加10%音量
        return (float) weight.ordinal() / (Weight.values().length - 1) * 0.5f + 0.25f;
    }
    static float pitch(net.dries007.tfc.common.capabilities.size.Size size) {
        // 将物品大小映射到0.0-1.0范围
        float sizeRatio = 1.0f - ((float) size.ordinal() / (net.dries007.tfc.common.capabilities.size.Size.values().length - 1));
        // 使用公式计算音调
        return NoteBlock.getPitchFromNote((int) Math.ceil(sizeRatio * 24));
    }
}
