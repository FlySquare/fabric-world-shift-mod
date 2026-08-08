package com.flyzen.changeworldeverysecond.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.config.ModLanguage;
import com.flyzen.changeworldeverysecond.util.ModConstants;
import com.flyzen.changeworldeverysecond.world.WorldType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
	private static final String CONFIG_FILE_NAME = ModConstants.MOD_ID + ".json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private final Path configPath;
	private ModConfig config = ModConfig.createDefault();

	public ConfigManager() {
		this(FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME));
	}

	public ConfigManager(Path configPath) {
		this.configPath = Objects.requireNonNull(configPath, "configPath");
	}

	public void init() {
		loadOrCreate();
		WorldShift.LOGGER.info("Config ready: {}", config);
	}

	public void loadOrCreate() {
		try {
			if (Files.notExists(configPath)) {
				config = ModConfig.createDefault();
				config.validateAndNormalize();
				save();
				return;
			}

			try (BufferedReader reader = Files.newBufferedReader(configPath)) {
				ModConfig loaded = GSON.fromJson(reader, ModConfig.class);
				config = loaded == null ? ModConfig.createDefault() : loaded;
			}

			config.validateAndNormalize();
			save();
		} catch (IOException exception) {
			WorldShift.LOGGER.error("Failed to load config, using defaults", exception);
			config = ModConfig.createDefault();
			config.validateAndNormalize();
		}
	}

	public void save() {
		try {
			Files.createDirectories(configPath.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(configPath)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException exception) {
			WorldShift.LOGGER.error("Failed to save config", exception);
		}
	}

	public ModConfig getConfig() {
		return config;
	}

	public int getTransitionDelaySeconds() {
		return config.getTransitionDelaySeconds();
	}

	public void setTransitionDelaySeconds(int transitionDelaySeconds) {
		if (transitionDelaySeconds < 0) {
			throw new IllegalArgumentException("transitionDelaySeconds must be >= 0");
		}

		config.setTransitionDelaySeconds(transitionDelaySeconds);
		save();
	}

	public int getCountdownSeconds() {
		return config.getCountdownSeconds();
	}

	public void setCountdownSeconds(int countdownSeconds) {
		if (countdownSeconds < 1) {
			throw new IllegalArgumentException("countdownSeconds must be >= 1");
		}

		config.setCountdownSeconds(countdownSeconds);
		save();
	}

	public ModLanguage getLanguage() {
		return config.getLanguage();
	}

	public void setLanguage(ModLanguage language) {
		config.setLanguage(language);
		save();
	}

	public WorldType getStartingWorld() {
		return config.getStartingWorld();
	}

	public void setStartingWorld(WorldType startingWorld) {
		config.setStartingWorld(startingWorld);
		save();
	}

	public boolean isDebug() {
		return config.isDebug();
	}

	public void setDebug(boolean debug) {
		config.setDebug(debug);
		save();
	}

	public Set<WorldType> getEnabledWorlds() {
		return config.getEnabledWorldSet();
	}

	public Path getConfigPath() {
		return configPath;
	}
}
