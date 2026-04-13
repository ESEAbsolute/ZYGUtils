package icu.eseabs0.zygutils.modules;

import icu.eseabs0.zygutils.events.UpdateListener;
import icu.eseabs0.zygutils.events.WorldTimeUpdateListener;

public final class GlobalTick implements UpdateListener, WorldTimeUpdateListener {
    private static final GlobalTick INSTANCE = new GlobalTick();
    public static GlobalTick getInstance() {
        return INSTANCE;
    }

    private GlobalTick() {}

    private long masterTick = 0;
    private static long lastWorldTime = -1L, tickDelta = 1L;
    private static long lastRealTime = -1L, realDelta = 50L;

    @Override
    public void onUpdate() {
        masterTick++;
    }

    @Override
    public void onWorldTimeUpdate(long time, long timeOfDay) {
        long now = System.currentTimeMillis();
        if (lastWorldTime != -1L) {
            tickDelta = time - lastWorldTime;
            realDelta = now - lastRealTime;
        }
        lastWorldTime = time;
        lastRealTime = now;
    }

    public long getMasterTick() {
        return masterTick;
    }

    public double getEstimatedServerTPS() {
        if (realDelta > 0) {
            return 1000.0 * tickDelta / realDelta;
        } else {
            return 20.0;
        }
    }

    public double getEstimatedServerMSPT() {
        if (realDelta > 0) {
            return 1.0 * realDelta / tickDelta;
        } else {
            return 50.0;
        }
    }
}