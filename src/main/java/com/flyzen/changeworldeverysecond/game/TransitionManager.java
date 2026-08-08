package com.flyzen.changeworldeverysecond.game;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;
import com.flyzen.changeworldeverysecond.events.CountdownCompletedEvent;
import com.flyzen.changeworldeverysecond.events.EventBus;
import com.flyzen.changeworldeverysecond.events.TransitionFinishedEvent;
import com.flyzen.changeworldeverysecond.events.TransitionStartedEvent;
import com.flyzen.changeworldeverysecond.events.WorldShiftEvent;
import com.flyzen.changeworldeverysecond.world.WorldManager;
import com.flyzen.changeworldeverysecond.world.WorldSelection;

import net.minecraft.server.MinecraftServer;

public final class TransitionManager {
	private EventBus eventBus;
	private TransitionFxManager fxManager;
	private boolean active;
	private int ticksRemaining;
	private int delaySeconds;
	private WorldSelection activeSelection;

	public void init(EventBus eventBus, TransitionFxManager fxManager) {
		this.eventBus = eventBus;
		this.fxManager = fxManager;
		this.eventBus.subscribe(CountdownCompletedEvent.class, this::onCountdownCompleted);
		resetState();
		WorldShift.LOGGER.info("TransitionManager ready");
	}

	private void onCountdownCompleted(CountdownCompletedEvent event) {
		if (!GameManager.getInstance().isRunning() || active) {
			return;
		}

		MinecraftServer server = event.server();
		CountdownManager.getInstance().freeze();

		WorldSelection selection = worldManager().performShift();
		eventBus.publish(new WorldShiftEvent(server, selection));

		beginTransition(server, selection);
	}

	public boolean forceShift(MinecraftServer server) {
		if (!GameManager.getInstance().isRunning() || active) {
			return false;
		}

		CountdownManager.getInstance().freeze();
		WorldSelection selection = worldManager().performShift();
		eventBus.publish(new WorldShiftEvent(server, selection));
		beginTransition(server, selection);
		return true;
	}

	private void beginTransition(MinecraftServer server, WorldSelection selection) {

		delaySeconds = 0;
		active = true;
		activeSelection = selection;
		ticksRemaining = 0;

		eventBus.publish(new TransitionStartedEvent(server, selection, delaySeconds));

		WorldShift.LOGGER.info(
				"Transition started (instant): {} -> {}",
				selection.currentWorld().displayName(),
				selection.nextWorld().displayName()
		);

		finishTransition(server);
	}

	public void tick(MinecraftServer server) {
		if (!active) {
			return;
		}

		if (fxManager != null) {
			fxManager.tick(server);
		}

		ticksRemaining--;

		if (ticksRemaining <= 0) {
			finishTransition(server);
		}
	}

	private void finishTransition(MinecraftServer server) {
		WorldSelection completed = activeSelection;
		active = false;
		ticksRemaining = 0;
		activeSelection = null;

		eventBus.publish(new TransitionFinishedEvent(server, completed));

		WorldShift.LOGGER.info(
				"Transition finished: {}",
				completed != null ? completed.nextWorld().displayName() : "unknown"
		);
	}

	public void cancel() {
		MinecraftServer server = GameManager.getInstance().getServer();
		if (fxManager != null) {
			fxManager.cancel(server);
		}
		active = false;
		ticksRemaining = 0;
		activeSelection = null;
	}

	public boolean isActive() {
		return active;
	}

	public WorldSelection getActiveSelection() {
		return activeSelection;
	}

	public int getTicksRemaining() {
		return ticksRemaining;
	}

	private void resetState() {
		active = false;
		ticksRemaining = 0;
		delaySeconds = 0;
		activeSelection = null;
	}

	private static WorldManager worldManager() {
		return WorldShift.getInstance().getWorldManager();
	}
}
