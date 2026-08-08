package com.flyzen.changeworldeverysecond.config;

import java.util.Locale;

public enum ModLanguage {
	EN("en"),
	TR("tr");

	private final String code;

	ModLanguage(String code) {
		this.code = code;
	}

	public String code() {
		return code;
	}

	public boolean isTurkish() {
		return this == TR;
	}

	public static ModLanguage fromCode(String code) {
		if (code == null || code.isBlank()) {
			return EN;
		}
		String normalized = code.trim().toLowerCase(Locale.ROOT);
		if (normalized.startsWith("tr")) {
			return TR;
		}
		return EN;
	}

	public static ModLanguage current() {
		try {
			return com.flyzen.changeworldeverysecond.WorldShift.getInstance()
					.getConfigManager()
					.getLanguage();
		} catch (Exception ignored) {
			return EN;
		}
	}

	public String translate(String key) {
		return ModTranslations.translate(this, key);
	}
}
