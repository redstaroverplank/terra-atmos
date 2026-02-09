package com.plank.terra_atmos.sounds;

import com.plank.terra_atmos.utils.ClientDelayHandler;
import com.plank.terra_atmos.utils.Geography;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public class WildLife {
    public WildLife(){

    }
    public void playSound(Level level, Player player) {
        if (Math.random() <= 0.0025 && level.dimension().location().getPath().equals("overworld")) {
            BlockPos blockPos = player.blockPosition();
            float temperature = Geography.getTemperature(level, blockPos);
            float rainfall = Geography.getRainfall(level, blockPos);
            if (temperature < 10.0f || rainfall <= 200f) return;
            switch (Geography.getBiome(level, blockPos)) {
                case "salt_marsh":
                case "shore":
                case "tidal_flats":
                case "ocean":
                case "ocean_reef":
                    if (isDay(level.getDayTime())) playSound(level, player, false, Sounds.SEAGULL.get(), 80, 3);
                    else playSound(level, player, true, Sounds.COASTBIRD.get(), 20, 5);
                    break;
                default:
                    if (temperature <= 20.0f) {
                        if (isDay(level.getDayTime())) playSound(level, player, false, Sounds.BIRD.get(), 20, 7);
                        else playSound(level, player, true, Sounds.KATYDID.get(), 20, 7);
                    } else if (rainfall >= 300f)
                        playSound(level, player, false, Sounds.CICADA.get(), 60, 2);
            }
        }
    }
    private static void playSound(Level level, Player player, boolean OnGround, SoundEvent sound, int wait, int times){
        BlockPos blockPos = OnGround ? player.blockPosition() : player.blockPosition().offset(0, 5, 0);
        if(level.isRainingAt(blockPos)) return;
        for(int i = 0; i < times; i++){
            final double x = blockPos.getX() + Mth.nextDouble(level.random, -6, 6);
            final double y = blockPos.getY() + Mth.nextDouble(level.random, -1, 1);
            final double z = blockPos.getZ() + Mth.nextDouble(level.random, -6, 6);
            ClientDelayHandler.schedule(i * wait, () -> level.playSound(player, x, y, z, sound, SoundSource.AMBIENT, 1.0f, 1.0f));
        }
    }
    // 使用游戏时间判断是否为晚上的方法
    private static boolean isDay(long gameTime) {
        // Minecraft中一天有24000刻，夜晚从13000刻开始到23000刻结束
        return gameTime % 24000 < 12000;
    }
}
