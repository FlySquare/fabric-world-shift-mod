package com.flyzen.changeworldeverysecond.game;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;
import com.flyzen.changeworldeverysecond.events.EventBus;
import com.flyzen.changeworldeverysecond.events.TransitionFinishedEvent;
import com.flyzen.changeworldeverysecond.world.WorldManager;

import net.minecraft.server.MinecraftServer;

public final class GameManager {
	private static final GameManager INSTANCE = new GameManager();

	private boolean running;
	private MinecraftServer server;

	private GameManager() {
	}

	public static GameManager getInstance() {
		return INSTANCE;
	}

	public void init(EventBus eventBus) {
		running = false;
		server = null;
		eventBus.subscribe(TransitionFinishedEvent.class, this::onTransitionFinished);
		WorldShift.LOGGER.info("GameManager ready");
	}

	public boolean start(MinecraftServer server) {
		if (running) {
			return false;
		}

		this.server = server;
		running = true;
		worldManager().onSessionStart(server);
		WorldShift.getInstance().getTeleportManager().teleportAllPlayers(server, worldManager().getCurrentWorld());
		CountdownManager.getInstance().start(server);
		WorldShift.LOGGER.info("World Shift session started (current world: {})", worldManager().getCurrentWorld());
		return true;
	}

	public boolean stop() {
		if (!running) {
			return false;
		}

		CountdownManager.getInstance().stop();
		WorldShift.getInstance().getHudManager().getDisplay().hide(server);
		WorldShift.getInstance().getTransitionManager().cancel();
		worldManager().onSessionStop();
		running = false;
		this.server = null;
		WorldShift.LOGGER.info("World Shift session stopped");
		return true;
	}

	private void onTransitionFinished(TransitionFinishedEvent event) {
		if (!running) {
			return;
		}

		worldManager().prepareNextWorld();
		CountdownManager.getInstance().start(event.server());
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isTransitioning() {
		return WorldShift.getInstance().getTransitionManager().isActive();
	}

	public MinecraftServer getServer() {
		return server;
	}

	private static WorldManager worldManager() {
		return WorldShift.getInstance().getWorldManager();
	}
}
