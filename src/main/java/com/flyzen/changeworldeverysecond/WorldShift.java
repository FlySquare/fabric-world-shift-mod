package com.flyzen.changeworldeverysecond;

import com.flyzen.changeworldeverysecond.commands.CommandManager;
import com.flyzen.changeworldeverysecond.config.ConfigManager;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;
import com.flyzen.changeworldeverysecond.events.EventManager;
import com.flyzen.changeworldeverysecond.game.GameManager;
import com.flyzen.changeworldeverysecond.game.TransitionFxManager;
import com.flyzen.changeworldeverysecond.game.TransitionManager;
import com.flyzen.changeworldeverysecond.hud.HudManager;
import com.flyzen.changeworldeverysecond.teleport.TeleportManager;
import com.flyzen.changeworldeverysecond.util.ModConstants;
import com.flyzen.changeworldeverysecond.world.WorldManager;
import com.flyzen.changeworldeverysecond.world.generation.CustomDimensionBootstrap;
import com.flyzen.changeworldeverysecond.world.generation.DeepDarkWardenDirector;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldShift implements ModInitializer {
	public static final String MOD_ID = ModConstants.MOD_ID;
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static WorldShift instance;

	private ConfigManager configManager;
	private CommandManager commandManager;
	private EventManager eventManager;
	private TransitionManager transitionManager;
	private TransitionFxManager transitionFxManager;
	private TeleportManager teleportManager;
	private WorldManager worldManager;
	private HudManager hudManager;
	private CustomDimensionBootstrap customDimensionBootstrap;
	private DeepDarkWardenDirector deepDarkWardenDirector;

	@Override
	public void onInitialize() {
		instance = this;

		configManager = new ConfigManager();
		commandManager = new CommandManager();
		eventManager = new EventManager();
		transitionManager = new TransitionManager();
		transitionFxManager = new TransitionFxManager();
		worldManager = new WorldManager();
		teleportManager = new TeleportManager(worldManager.getDimensionResolver());
		hudManager = new HudManager();
		customDimensionBootstrap = new CustomDimensionBootstrap();
		deepDarkWardenDirector = new DeepDarkWardenDirector();

		configManager.init();
		worldManager.init();
		CountdownManager.getInstance().init();
		hudManager.init();
		customDimensionBootstrap.init();

		transitionFxManager.init(eventManager.getEventBus());
		teleportManager.init(eventManager.getEventBus());
		GameManager.getInstance().init(eventManager.getEventBus());
		transitionManager.init(eventManager.getEventBus(), transitionFxManager);
		commandManager.init();
		eventManager.init();

		LOGGER.info("World Shift initialized");
	}

	public static WorldShift getInstance() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public CountdownManager getCountdownManager() {
		return CountdownManager.getInstance();
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public TransitionManager getTransitionManager() {
		return transitionManager;
	}

	public GameManager getGameManager() {
		return GameManager.getInstance();
	}

	public TeleportManager getTeleportManager() {
		return teleportManager;
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public HudManager getHudManager() {
		return hudManager;
	}

	public DeepDarkWardenDirector getDeepDarkWardenDirector() {
		return deepDarkWardenDirector;
	}
}
