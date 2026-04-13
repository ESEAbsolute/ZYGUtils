package icu.eseabs0.zygutils.events;

import icu.eseabs0.zygutils.eventsystem.Event;

import java.util.ArrayList;
import java.util.EventListener;

public interface WorldTimeUpdateListener extends EventListener {
    void onWorldTimeUpdate(long time, long timeOfDay);

    class WorldTimeUpdateEvent extends Event<WorldTimeUpdateListener> {
        private final long time, timeOfDay;

        public WorldTimeUpdateEvent(long time, long timeOfDay) {
            this.time = time;
            this.timeOfDay = timeOfDay;
        }

        @Override
        public void fire(ArrayList<WorldTimeUpdateListener> listeners) {
            for(WorldTimeUpdateListener listener : listeners) {
                listener.onWorldTimeUpdate(time, timeOfDay);
            }
        }
        @Override
        public Class<WorldTimeUpdateListener> getListenerType() {
            return WorldTimeUpdateListener.class;
        }
    }
}
