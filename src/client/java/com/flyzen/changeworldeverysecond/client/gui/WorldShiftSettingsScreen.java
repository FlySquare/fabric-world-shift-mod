package com.flyzen.changeworldeverysecond.client.gui;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.config.ConfigManager;
import com.flyzen.changeworldeverysecond.config.ModLanguage;
import com.flyzen.changeworldeverysecond.util.ModTexts;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class WorldShiftSettingsScreen extends Screen {
	private final Screen parent;
	private EditBox intervalBox;

	public WorldShiftSettingsScreen(Screen parent) {
		super(Component.literal(ModTexts.menuSettingsTitle()));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int centerX = width / 2;
		int y = height / 2 - 50;

		addRenderableWidget(new StringWidget(
				centerX - 100,
				y,
				200,
				12,
				Component.literal(ModTexts.menuSettingsTitle()),
				font
		));

		y += 24;
		addRenderableWidget(new StringWidget(
				centerX - 100,
				y,
				200,
				12,
				Component.literal(ModTexts.menuLanguage()),
				font
		));

		y += 14;
		addRenderableWidget(
				CycleButton.builder(this::languageLabel, config().getLanguage())
						.withValues(ModLanguage.EN, ModLanguage.TR)
						.create(centerX - 100, y, 200, 20, Component.literal(ModTexts.menuLanguage()), (button, value) -> {
							config().setLanguage(value);
							rebuildWidgets();
						})
		);

		y += 28;
		addRenderableWidget(new StringWidget(
				centerX - 100,
				y,
				200,
				12,
				Component.literal(ModTexts.menuInterval()),
				font
		));

		y += 14;
		intervalBox = addRenderableWidget(new EditBox(
				font,
				centerX - 100,
				y,
				200,
				20,
				Component.literal(ModTexts.menuInterval())
		));
		intervalBox.setMaxLength(4);
		intervalBox.setValue(Integer.toString(config().getCountdownSeconds()));
		intervalBox.setResponder(value -> {
			String digits = value.replaceAll("\\D", "");
			if (!digits.equals(value)) {
				intervalBox.setValue(digits);
			}
		});

		y += 36;
		addRenderableWidget(
				Button.builder(Component.literal(ModTexts.menuDone()), button -> onClose())
						.bounds(centerX - 100, y, 200, 20)
						.build()
		);
	}

	@Override
	public void onClose() {
		saveInterval();
		if (minecraft != null) {
			minecraft.setScreenAndShow(parent);
		}
	}

	private void saveInterval() {
		if (intervalBox == null) {
			return;
		}
		try {
			int seconds = Integer.parseInt(intervalBox.getValue().trim());
			if (seconds >= 1 && seconds <= 3600) {
				config().setCountdownSeconds(seconds);
			}
		} catch (NumberFormatException ignored) {
		}
	}

	private Component languageLabel(ModLanguage language) {
		return Component.literal(language == ModLanguage.TR ? ModTexts.menuLanguageTr() : ModTexts.menuLanguageEn());
	}

	private static ConfigManager config() {
		return WorldShift.getInstance().getConfigManager();
	}
}
