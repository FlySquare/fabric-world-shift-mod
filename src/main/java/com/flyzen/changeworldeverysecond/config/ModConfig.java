package com.flyzen.changeworldeverysecond.config;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.flyzen.changeworldeverysecond.world.WorldType;
import com.google.gson.annotations.SerializedName;

public final class ModConfig {
	public static final int DEFAULT_COUNTDOWN_SECONDS = 60;
	public static final int DEFAULT_TRANSITION_DELAY_SECONDS = 0;
	public static final String DEFAULT_LANGUAGE = ModLanguage.EN.code();

	@SerializedName("countdownSeconds")
	private int countdownSeconds = DEFAULT_COUNTDOWN_SECONDS;

	@SerializedName("transitionDelaySeconds")
	private int transitionDelaySeconds = DEFAULT_TRANSITION_DELAY_SECONDS;

	@SerializedName("language")
	private String language = DEFAULT_LANGUAGE;

	@SerializedName("startingWorld")
	private String startingWorld = WorldType.OVERWORLD.name();

	@SerializedName("debug")
	private boolean debug;

	@SerializedName("enabledWorlds")
	private List<String> enabledWorlds = defaultEnabledWorldNames();

	public static ModConfig createDefault() {
		return new ModConfig();
	}

	public void validateAndNormalize() {
		if (countdownSeconds < 1) {
			countdownSeconds = DEFAULT_COUNTDOWN_SECONDS;
		}
		if (transitionDelaySeconds < 0) {
			transitionDelaySeconds = DEFAULT_TRANSITION_DELAY_SECONDS;
		}

		language = ModLanguage.fromCode(language).code();

		WorldType starting = WorldType.byName(startingWorld).orElse(WorldType.OVERWORLD);
		startingWorld = starting.name();

		Set<WorldType> enabled = getEnabledWorldSet();
		if (enabled.isEmpty()) {
			enabledWorlds = defaultEnabledWorldNames();
			enabled = getEnabledWorldSet();
		}

		if (!enabled.contains(starting)) {
			startingWorld = enabled.iterator().next().name();
		}
	}

	public int getCountdownSeconds() {
		return countdownSeconds;
	}

	public void setCountdownSeconds(int countdownSeconds) {
		this.countdownSeconds = countdownSeconds;
	}

	public int getTransitionDelaySeconds() {
		return transitionDelaySeconds;
	}

	public void setTransitionDelaySeconds(int transitionDelaySeconds) {
		this.transitionDelaySeconds = transitionDelaySeconds;
	}

	public ModLanguage getLanguage() {
		return ModLanguage.fromCode(language);
	}

	public void setLanguage(ModLanguage language) {
		this.language = Objects.requireNonNull(language, "language").code();
	}

	public WorldType getStartingWorld() {
		return WorldType.byName(startingWorld).orElse(WorldType.OVERWORLD);
	}

	public void setStartingWorld(WorldType startingWorld) {
		this.startingWorld = Objects.requireNonNull(startingWorld, "startingWorld").name();
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Set<WorldType> getEnabledWorldSet() {
		EnumSet<WorldType> worlds = EnumSet.noneOf(WorldType.class);
		if (enabledWorlds == null) {
			return worlds;
		}

		for (String name : enabledWorlds) {
			WorldType.byName(name).ifPresent(worlds::add);
		}

		return worlds;
	}

	public void setEnabledWorlds(Set<WorldType> worlds) {
		enabledWorlds = new ArrayList<>();
		for (WorldType worldType : worlds) {
			enabledWorlds.add(worldType.name());
		}
	}

	private static List<String> defaultEnabledWorldNames() {
		List<String> names = new ArrayList<>(WorldType.values().length);
		for (WorldType worldType : WorldType.values()) {
			names.add(worldType.name());
		}
		return names;
	}

	@Override
	public String toString() {
		return "ModConfig{countdownSeconds=" + countdownSeconds
				+ ", transitionDelaySeconds=" + transitionDelaySeconds
				+ ", language=" + language
				+ ", startingWorld=" + startingWorld.toUpperCase(Locale.ROOT)
				+ ", debug=" + debug
				+ ", enabledWorlds=" + enabledWorlds
				+ '}';
	}
}
