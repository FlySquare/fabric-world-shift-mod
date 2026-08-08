package com.flyzen.changeworldeverysecond.events;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class EventManager {
	private final EventBus eventBus = new EventBus();

	public void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			WorldShift.getInstance().getTransitionManager().tick(server);
			CountdownManager.getInstance().tick(server);
			WorldShift.getInstance().getHudManager().tick(server);
			WorldShift.getInstance().getDeepDarkWardenDirector().tick(server);
		});

		WorldShift.LOGGER.info("EventManager ready");
	}

	public EventBus getEventBus() {
		return eventBus;
	}
}
