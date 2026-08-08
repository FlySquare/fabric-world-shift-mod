package com.flyzen.changeworldeverysecond.client;

import com.flyzen.changeworldeverysecond.client.gui.WorldShiftSettingsScreen;
import com.flyzen.changeworldeverysecond.util.ModTexts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

public final class WorldShiftClient implements ClientModInitializer {
	private static WorldShiftClient instance;

	private static final float TIMER_DOWN_OFFSET = 12.0F;
	private static final int SETTINGS_BUTTON_SIZE = 20;

	@Override
	public void onInitializeClient() {
		instance = this;

		HudElementRegistry.replaceElement(VanillaHudElements.OVERLAY_MESSAGE, original ->
				(HudElement) (graphics, deltaTracker) -> {
					graphics.pose().pushMatrix();
					graphics.pose().translate(0.0F, TIMER_DOWN_OFFSET);
					original.extractRenderState(graphics, deltaTracker);
					graphics.pose().popMatrix();
				}
		);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (!(screen instanceof TitleScreen)) {
				return;
			}

			AbstractWidget singleplayer = findSingleplayerButton(screen);
			if (singleplayer == null) {
				return;
			}

			int x = singleplayer.getX() + singleplayer.getWidth() + 4;
			int y = singleplayer.getY() + (singleplayer.getHeight() - SETTINGS_BUTTON_SIZE) / 2;

			Button settingsButton = Button.builder(
							Component.literal(ModTexts.menuSettingsButton()),
							button -> client.setScreenAndShow(new WorldShiftSettingsScreen(screen))
					)
					.bounds(x, y, SETTINGS_BUTTON_SIZE, SETTINGS_BUTTON_SIZE)
					.tooltip(Tooltip.create(Component.literal(ModTexts.menuSettingsTooltip())))
					.build();

			Screens.getWidgets(screen).add(settingsButton);
		});
	}

	private static AbstractWidget findSingleplayerButton(net.minecraft.client.gui.screens.Screen screen) {
		for (AbstractWidget widget : Screens.getWidgets(screen)) {
			if (isSingleplayerButton(widget)) {
				return widget;
			}
		}
		return null;
	}

	private static boolean isSingleplayerButton(AbstractWidget widget) {
		Component message = widget.getMessage();
		if (message.getContents() instanceof TranslatableContents contents) {
			return "menu.singleplayer".equals(contents.getKey());
		}
		String plain = message.getString();
		return plain.equalsIgnoreCase("Singleplayer") || plain.equalsIgnoreCase("Tek oyuncu");
	}

	public static WorldShiftClient getInstance() {
		return instance;
	}
}
