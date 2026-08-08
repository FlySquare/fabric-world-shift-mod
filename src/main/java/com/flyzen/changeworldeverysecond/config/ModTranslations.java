package com.flyzen.changeworldeverysecond.config;

import java.util.HashMap;
import java.util.Map;

final class ModTranslations {
	private static final Map<String, String> EN = new HashMap<>();
	private static final Map<String, String> TR = new HashMap<>();

	static {
		EN.put("shift.banner", "WORLD SHIFT");
		EN.put("label.current", "Now");
		EN.put("label.next", "Next");
		EN.put("hud.world", "World");
		EN.put("hud.next", "Next");
		EN.put("hud.time", "Time");
		EN.put("cmd.usage", "Usage: /worldshift <start|stop|next|timer|debug|world>");
		EN.put("cmd.already_running", "World Shift is already running.");
		EN.put("cmd.started", "World Shift started.");
		EN.put("cmd.not_running", "World Shift is not running.");
		EN.put("cmd.stopped", "World Shift stopped.");
		EN.put("cmd.not_running_start", "World Shift is not running. Use /worldshift start.");
		EN.put("cmd.transition_active", "A transition is already in progress.");
		EN.put("cmd.forcing_shift", "Forcing world shift...");
		EN.put("cmd.timer_set", "Countdown set to %d seconds.");
		EN.put("cmd.unknown_world", "Unknown world: %s");
		EN.put("cmd.world_disabled", "This world is disabled in config: %s");
		EN.put("cmd.teleported", "Teleported to %s.");
		EN.put("cmd.debug_title", "World Shift debug");
		EN.put("debug.current", "Current World");
		EN.put("debug.next", "Next World");
		EN.put("debug.countdown", "Countdown");
		EN.put("debug.coords", "Coordinates");
		EN.put("debug.dimension", "Dimension");
		EN.put("debug.transition", "Transition");
		EN.put("debug.running", "Running");
		EN.put("debug.selection", "Active Selection");
		EN.put("debug.none", "none");
		EN.put("debug.active", "active");
		EN.put("debug.frozen", "frozen");
		EN.put("debug.idle", "idle");
		EN.put("debug.na", "n/a");
		EN.put("world.overworld", "Overworld");
		EN.put("world.nether", "Nether");
		EN.put("world.end", "End");
		EN.put("world.skyblock", "Skyblock");
		EN.put("world.deep_dark", "Deep Dark");
		EN.put("world.mushroom_island", "Mushroom Island");
		EN.put("menu.settings_title", "World Shift Settings");
		EN.put("menu.settings_button", "WS");
		EN.put("menu.settings_tooltip", "World Shift settings");
		EN.put("menu.language", "Language");
		EN.put("menu.interval", "Seconds between worlds");
		EN.put("menu.done", "Done");
		EN.put("menu.language.en", "English");
		EN.put("menu.language.tr", "Turkish");

		TR.put("shift.banner", "DÜNYA DEĞİŞİMİ");
		TR.put("label.current", "Şu an");
		TR.put("label.next", "Sıradaki");
		TR.put("hud.world", "Dünya");
		TR.put("hud.next", "Sıradaki");
		TR.put("hud.time", "Süre");
		TR.put("cmd.usage", "Kullanım: /worldshift <start|stop|next|timer|debug|world>");
		TR.put("cmd.already_running", "World Shift zaten çalışıyor.");
		TR.put("cmd.started", "World Shift başlatıldı.");
		TR.put("cmd.not_running", "World Shift çalışmıyor.");
		TR.put("cmd.stopped", "World Shift durduruldu.");
		TR.put("cmd.not_running_start", "World Shift çalışmıyor. /worldshift start yaz.");
		TR.put("cmd.transition_active", "Zaten bir geçiş devam ediyor.");
		TR.put("cmd.forcing_shift", "Dünya değişimi zorlanıyor...");
		TR.put("cmd.timer_set", "Geri sayım %d saniye olarak ayarlandı.");
		TR.put("cmd.unknown_world", "Bilinmeyen dünya: %s");
		TR.put("cmd.world_disabled", "Bu dünya configde kapalı: %s");
		TR.put("cmd.teleported", "%s dünyasına ışınlandın.");
		TR.put("cmd.debug_title", "World Shift hata ayıklama");
		TR.put("debug.current", "Mevcut Dünya");
		TR.put("debug.next", "Sıradaki Dünya");
		TR.put("debug.countdown", "Geri Sayım");
		TR.put("debug.coords", "Koordinatlar");
		TR.put("debug.dimension", "Boyut");
		TR.put("debug.transition", "Geçiş Durumu");
		TR.put("debug.running", "Çalışıyor");
		TR.put("debug.selection", "Aktif Seçim");
		TR.put("debug.none", "yok");
		TR.put("debug.active", "aktif");
		TR.put("debug.frozen", "donduruldu");
		TR.put("debug.idle", "boşta");
		TR.put("debug.na", "yok");
		TR.put("world.overworld", "Ana Dünya");
		TR.put("world.nether", "Nether");
		TR.put("world.end", "End");
		TR.put("world.skyblock", "Skyblock");
		TR.put("world.deep_dark", "Derin Karanlık");
		TR.put("world.mushroom_island", "Mantar Adası");
		TR.put("menu.settings_title", "World Shift Ayarları");
		TR.put("menu.settings_button", "WS");
		TR.put("menu.settings_tooltip", "World Shift ayarları");
		TR.put("menu.language", "Dil");
		TR.put("menu.interval", "Dünyalar arası saniye");
		TR.put("menu.done", "Tamam");
		TR.put("menu.language.en", "İngilizce");
		TR.put("menu.language.tr", "Türkçe");
	}

	private ModTranslations() {
	}

	static String translate(ModLanguage language, String key) {
		Map<String, String> primary = language == ModLanguage.TR ? TR : EN;
		String value = primary.get(key);
		if (value != null) {
			return value;
		}
		return EN.getOrDefault(key, key);
	}
}
