package com.flyzen.changeworldeverysecond.util;

import com.flyzen.changeworldeverysecond.config.ModLanguage;

public final class ModTexts {
	private ModTexts() {
	}

	public static String shiftBanner() {
		return t("shift.banner");
	}

	public static String currentLabel() {
		return t("label.current");
	}

	public static String nextLabel() {
		return t("label.next");
	}

	public static String hudWorld() {
		return t("hud.world");
	}

	public static String hudNext() {
		return t("hud.next");
	}

	public static String hudTime() {
		return t("hud.time");
	}

	public static String cmdUsage() {
		return t("cmd.usage");
	}

	public static String cmdAlreadyRunning() {
		return t("cmd.already_running");
	}

	public static String cmdStarted() {
		return t("cmd.started");
	}

	public static String cmdNotRunning() {
		return t("cmd.not_running");
	}

	public static String cmdStopped() {
		return t("cmd.stopped");
	}

	public static String cmdNotRunningStart() {
		return t("cmd.not_running_start");
	}

	public static String cmdTransitionActive() {
		return t("cmd.transition_active");
	}

	public static String cmdForcingShift() {
		return t("cmd.forcing_shift");
	}

	public static String cmdTimerSet() {
		return t("cmd.timer_set");
	}

	public static String cmdUnknownWorld() {
		return t("cmd.unknown_world");
	}

	public static String cmdWorldDisabled() {
		return t("cmd.world_disabled");
	}

	public static String cmdTeleported() {
		return t("cmd.teleported");
	}

	public static String cmdDebugTitle() {
		return t("cmd.debug_title");
	}

	public static String debugCurrent() {
		return t("debug.current");
	}

	public static String debugNext() {
		return t("debug.next");
	}

	public static String debugCountdown() {
		return t("debug.countdown");
	}

	public static String debugCoords() {
		return t("debug.coords");
	}

	public static String debugDimension() {
		return t("debug.dimension");
	}

	public static String debugTransition() {
		return t("debug.transition");
	}

	public static String debugRunning() {
		return t("debug.running");
	}

	public static String debugSelection() {
		return t("debug.selection");
	}

	public static String debugNone() {
		return t("debug.none");
	}

	public static String debugActive() {
		return t("debug.active");
	}

	public static String debugFrozen() {
		return t("debug.frozen");
	}

	public static String debugIdle() {
		return t("debug.idle");
	}

	public static String debugNa() {
		return t("debug.na");
	}

	public static String menuSettingsTitle() {
		return t("menu.settings_title");
	}

	public static String menuSettingsButton() {
		return t("menu.settings_button");
	}

	public static String menuSettingsTooltip() {
		return t("menu.settings_tooltip");
	}

	public static String menuLanguage() {
		return t("menu.language");
	}

	public static String menuInterval() {
		return t("menu.interval");
	}

	public static String menuDone() {
		return t("menu.done");
	}

	public static String menuLanguageEn() {
		return t("menu.language.en");
	}

	public static String menuLanguageTr() {
		return t("menu.language.tr");
	}

	private static String t(String key) {
		return ModLanguage.current().translate(key);
	}
}
