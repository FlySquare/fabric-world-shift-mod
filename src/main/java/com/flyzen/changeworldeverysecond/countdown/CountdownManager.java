package com.flyzen.changeworldeverysecond.countdown;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.events.CountdownCompletedEvent;
import com.flyzen.changeworldeverysecond.util.ModConstants;

import net.minecraft.server.MinecraftServer;

public final class CountdownManager {
	private static final CountdownManager INSTANCE = new CountdownManager();

	private final CountdownDisplay display = new CountdownDisplay();

	private boolean active;
	private boolean frozen;
	private int secondsRemaining;
	private int tickCounter;
	private int durationSeconds;

	private CountdownManager() {
	}

	public static CountdownManager getInstance() {
		return INSTANCE;
	}

	public void init() {
		durationSeconds = WorldShift.getInstance().getConfigManager().getCountdownSeconds();
		reset();
		WorldShift.LOGGER.info("CountdownManager ready ({}s)", durationSeconds);
	}

	public void start(MinecraftServer server) {
		durationSeconds = WorldShift.getInstance().getConfigManager().getCountdownSeconds();
		active = true;
		frozen = false;
		secondsRemaining = durationSeconds;
		tickCounter = 0;
		display.showSeconds(server, secondsRemaining);
		WorldShift.LOGGER.info("Countdown started ({}s)", durationSeconds);
	}

	public void freeze() {
		active = false;
		frozen = true;
		tickCounter = 0;
		WorldShift.LOGGER.info("Countdown frozen");
	}

	public void stop() {
		reset();
		WorldShift.LOGGER.info("Countdown stopped");
	}

	public void tick(MinecraftServer server) {
		if (!active || frozen) {
			return;
		}

		tickCounter++;
		if (tickCounter < ModConstants.TICKS_PER_SECOND) {
			return;
		}

		tickCounter = 0;
		onSecondElapsed(server);
	}

	private void onSecondElapsed(MinecraftServer server) {
		if (secondsRemaining > 1) {
			secondsRemaining--;
			display.showSeconds(server, secondsRemaining);
			return;
		}

		secondsRemaining = 0;
		active = false;
		tickCounter = 0;
		WorldShift.getInstance().getEventManager().getEventBus().publish(new CountdownCompletedEvent(server));
	}

	public boolean isActive() {
		return active;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public int getSecondsRemaining() {
		return secondsRemaining;
	}

	public int getDurationSeconds() {
		return durationSeconds;
	}

	private void reset() {
		active = false;
		frozen = false;
		secondsRemaining = durationSeconds > 0
				? durationSeconds
				: WorldShift.getInstance().getConfigManager().getCountdownSeconds();
		tickCounter = 0;
	}
}
