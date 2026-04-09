package icu.eseabs0.zygutils.modules;

import icu.eseabs0.zygutils.events.UpdateListener;

public final class GlobalTick implements UpdateListener {
    private static final GlobalTick INSTANCE = new GlobalTick();
    public static GlobalTick getInstance() { return INSTANCE; }

    private GlobalTick() {}

    private long masterTick = 0;

    @Override
    public void onUpdate() {
        masterTick++;
    }

    public long getMasterTick() {
        return masterTick;
    }
}