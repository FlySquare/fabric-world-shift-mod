package com.flyzen.changeworldeverysecond.hud;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.countdown.CountdownDisplay;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;
import com.flyzen.changeworldeverysecond.game.GameManager;
import com.flyzen.changeworldeverysecond.util.ModConstants;

import net.minecraft.server.MinecraftServer;

public final class HudManager {
	private final CountdownDisplay display = new CountdownDisplay();
	private int tickCounter;

	public void init() {
		WorldShift.LOGGER.info("HudManager ready (client countdown sync)");
	}

	public void tick(MinecraftServer server) {
		if (!GameManager.getInstance().isRunning()) {
			return;
		}

		tickCounter++;
		if (tickCounter < ModConstants.TICKS_PER_SECOND) {
			return;
		}

		tickCounter = 0;

		CountdownManager countdown = CountdownManager.getInstance();
		if (countdown.isActive()) {
			return;
		}

		display.showIdleStatus(server);
	}

	public CountdownDisplay getDisplay() {
		return display;
	}
}
