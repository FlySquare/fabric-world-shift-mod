package com.flyzen.changeworldeverysecond.game;

import com.flyzen.changeworldeverysecond.util.ModConstants;
import com.flyzen.changeworldeverysecond.util.ModTexts;
import com.flyzen.changeworldeverysecond.world.WorldSelection;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class TransitionTitleDisplay {
	private static final int FADE_IN_TICKS = 5;
	private static final int FADE_OUT_TICKS = 10;

	public void show(MinecraftServer server, WorldSelection selection, int staySeconds) {
		Component title = Component.literal(ModTexts.shiftBanner()).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
		Component subtitle = buildSubtitle(selection);
		int stayTicks = Math.max(staySeconds, 1) * ModConstants.TICKS_PER_SECOND;

		ClientboundClearTitlesPacket clearPacket = new ClientboundClearTitlesPacket(false);
		ClientboundSetTitlesAnimationPacket timingPacket =
				new ClientboundSetTitlesAnimationPacket(FADE_IN_TICKS, stayTicks, FADE_OUT_TICKS);
		ClientboundSetTitleTextPacket titlePacket = new ClientboundSetTitleTextPacket(title);
		ClientboundSetSubtitleTextPacket subtitlePacket = new ClientboundSetSubtitleTextPacket(subtitle);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.send(clearPacket);
			player.connection.send(timingPacket);
			player.connection.send(titlePacket);
			player.connection.send(subtitlePacket);
		}
	}

	public void clear(MinecraftServer server) {
		ClientboundClearTitlesPacket clearPacket = new ClientboundClearTitlesPacket(true);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			player.connection.send(clearPacket);
		}
	}

	private Component buildSubtitle(WorldSelection selection) {
		MutableComponent subtitle = Component.empty();
		subtitle.append(Component.literal(ModTexts.currentLabel() + ": ").withStyle(ChatFormatting.GRAY));
		subtitle.append(Component.literal(selection.currentWorld().displayName()).withStyle(ChatFormatting.YELLOW));
		subtitle.append(Component.literal("  →  ").withStyle(ChatFormatting.DARK_GRAY));
		subtitle.append(Component.literal(ModTexts.nextLabel() + ": ").withStyle(ChatFormatting.GRAY));
		subtitle.append(Component.literal(selection.nextWorld().displayName()).withStyle(ChatFormatting.AQUA));
		return subtitle;
	}
}
