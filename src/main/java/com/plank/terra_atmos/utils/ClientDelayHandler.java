package com.plank.terra_atmos.utils;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientDelayHandler {
    private static final Queue<DelayedTask> TASKS = new ConcurrentLinkedQueue<>();
    // 在Mod初始化时注册
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(ClientDelayHandler::onClientTick);
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<DelayedTask> iterator = TASKS.iterator();
            while (iterator.hasNext()) {
                DelayedTask task = iterator.next();
                if (--task.ticksRemaining <= 0) {
                    task.runnable.run();
                    iterator.remove();
                }
            }
        }
    }
    // 添加延迟任务（1秒 = 20 ticks）
    public static void schedule(int delayTicks, Runnable runnable) {
        TASKS.add(new DelayedTask(delayTicks, runnable));
    }
    private static class DelayedTask {
        int ticksRemaining;
        Runnable runnable;
        DelayedTask(int ticks, Runnable run) {
            this.ticksRemaining = ticks;
            this.runnable = run;
        }
    }
}