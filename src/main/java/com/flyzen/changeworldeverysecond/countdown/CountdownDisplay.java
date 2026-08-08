package com.flyzen.changeworldeverysecond.countdown;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.game.GameManager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class CountdownDisplay {
	private static final int HIGHLIGHT_SECONDS = 10;
	private static final int BEEP_SECONDS = 3;

	public void showSeconds(MinecraftServer server, int seconds) {
		showStatus(server, seconds);
		playTickBeep(server, seconds);
	}

	public void showStatus(MinecraftServer server, int secondsRemaining) {
		boolean highlight = secondsRemaining > 0 && secondsRemaining <= HIGHLIGHT_SECONDS;
		Component message = Component.literal(formatTime(secondsRemaining)).withStyle(
				highlight ? ChatFormatting.RED : ChatFormatting.YELLOW
		);
		broadcast(server, message);
	}

	public void hide(MinecraftServer server) {
		broadcast(server, Component.empty());
	}

	public void showIdleStatus(MinecraftServer server) {
		var transitionManager = WorldShift.getInstance().getTransitionManager();

		if (transitionManager.isActive()) {
			broadcast(server, Component.literal(formatTime(0)).withStyle(ChatFormatting.RED));
			return;
		}

		if (!GameManager.getInstance().isRunning()) {
			hide(server);
			return;
		}

		showStatus(server, CountdownManager.getInstance().getSecondsRemaining());
	}

	private static void playTickBeep(MinecraftServer server, int secondsRemaining) {
		if (secondsRemaining <= 0 || secondsRemaining > BEEP_SECONDS) {
			return;
		}

		float pitch = secondsRemaining == 1 ? 1.8F : 1.5F;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.level().playSound(
					null,
					player.getX(),
					player.getY(),
					player.getZ(),
					SoundEvents.NOTE_BLOCK_PLING.value(),
					SoundSource.PLAYERS,
					0.55F,
					pitch
			);
		}
	}

	private static String formatTime(int totalSeconds) {
		int clamped = Math.max(totalSeconds, 0);
		int minutes = clamped / 60;
		int seconds = clamped % 60;
		return String.format("%02d:%02d", minutes, seconds);
	}

	private static void broadcast(MinecraftServer server, Component message) {
		ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(message);
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.send(packet);
		}
	}
}
