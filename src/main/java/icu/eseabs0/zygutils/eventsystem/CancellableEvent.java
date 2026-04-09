package icu.eseabs0.zygutils.eventsystem;

import java.util.EventListener;

public abstract class CancellableEvent <T extends EventListener> extends Event <T> {
    private boolean cancelled = false;
    public void cancel() { cancelled = true; }
    public boolean isCancelled() { return cancelled; }
}
