package com.flyzen.changeworldeverysecond.commands;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.config.ConfigManager;
import com.flyzen.changeworldeverysecond.countdown.CountdownManager;
import com.flyzen.changeworldeverysecond.game.GameManager;
import com.flyzen.changeworldeverysecond.game.TransitionManager;
import com.flyzen.changeworldeverysecond.persistence.WorldShiftSavedData;
import com.flyzen.changeworldeverysecond.teleport.TeleportManager;
import com.flyzen.changeworldeverysecond.util.ModTexts;
import com.flyzen.changeworldeverysecond.world.WorldManager;
import com.flyzen.changeworldeverysecond.world.WorldSelection;
import com.flyzen.changeworldeverysecond.world.WorldType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public final class WorldShiftCommand {
	private WorldShiftCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
		dispatcher.register(
				Commands.literal("worldshift")
						.executes(WorldShiftCommand::usage)
						.then(Commands.literal("start").executes(WorldShiftCommand::start))
						.then(Commands.literal("stop").executes(WorldShiftCommand::stop))
						.then(Commands.literal("next").executes(WorldShiftCommand::next))
						.then(Commands.literal("timer")
								.then(Commands.argument("seconds", IntegerArgumentType.integer(1, 3600))
										.executes(WorldShiftCommand::timer)))
						.then(Commands.literal("debug").executes(WorldShiftCommand::debug))
						.then(Commands.literal("world")
								.then(Commands.argument("world", StringArgumentType.word())
										.suggests(WorldShiftCommand::suggestWorlds)
										.executes(WorldShiftCommand::world)))
		);
	}

	private static int usage(CommandContext<CommandSourceStack> context) {
		context.getSource().sendSuccess(
				() -> Component.literal(ModTexts.cmdUsage()).withStyle(ChatFormatting.GRAY),
				false
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int start(CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		GameManager gameManager = GameManager.getInstance();

		if (!gameManager.start(server)) {
			context.getSource().sendFailure(Component.literal(ModTexts.cmdAlreadyRunning()));
			return 0;
		}

		context.getSource().sendSuccess(
				() -> Component.literal(ModTexts.cmdStarted()).withStyle(ChatFormatting.GREEN),
				true
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int stop(CommandContext<CommandSourceStack> context) {
		GameManager gameManager = GameManager.getInstance();

		if (!gameManager.stop()) {
			context.getSource().sendFailure(Component.literal(ModTexts.cmdNotRunning()));
			return 0;
		}

		context.getSource().sendSuccess(
				() -> Component.literal(ModTexts.cmdStopped()).withStyle(ChatFormatting.YELLOW),
				true
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int next(CommandContext<CommandSourceStack> context) {
		MinecraftServer server = context.getSource().getServer();
		GameManager gameManager = GameManager.getInstance();

		if (!gameManager.isRunning()) {
			context.getSource().sendFailure(Component.literal(ModTexts.cmdNotRunningStart()));
			return 0;
		}

		if (!WorldShift.getInstance().getTransitionManager().forceShift(server)) {
			context.getSource().sendFailure(Component.literal(ModTexts.cmdTransitionActive()));
			return 0;
		}

		context.getSource().sendSuccess(
				() -> Component.literal(ModTexts.cmdForcingShift()).withStyle(ChatFormatting.AQUA),
				true
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int timer(CommandContext<CommandSourceStack> context) {
		int seconds = IntegerArgumentType.getInteger(context, "seconds");
		ConfigManager configManager = WorldShift.getInstance().getConfigManager();
		configManager.setCountdownSeconds(seconds);

		context.getSource().sendSuccess(
				() -> Component.literal(String.format(Locale.ROOT, ModTexts.cmdTimerSet(), seconds))
						.withStyle(ChatFormatting.GREEN),
				true
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int world(CommandContext<CommandSourceStack> context) {
		String raw = StringArgumentType.getString(context, "world");
		WorldType target = WorldType.byName(raw).orElse(null);

		if (target == null) {
			context.getSource().sendFailure(Component.literal(String.format(Locale.ROOT, ModTexts.cmdUnknownWorld(), raw)));
			return 0;
		}

		WorldManager worldManager = WorldShift.getInstance().getWorldManager();
		if (!worldManager.getRegistry().isRegistered(target)) {
			context.getSource().sendFailure(
					Component.literal(String.format(Locale.ROOT, ModTexts.cmdWorldDisabled(), target.displayName()))
			);
			return 0;
		}

		MinecraftServer server = context.getSource().getServer();
		TeleportManager teleportManager = WorldShift.getInstance().getTeleportManager();
		WorldType current = worldManager.getCurrentWorld();

		if (current != target) {
			teleportManager.saveAllPlayersInCurrentWorld(server, current);
		}

		worldManager.forceWorld(target);
		teleportManager.teleportAllPlayers(server, target);
		WorldShiftSavedData.get(server).setCurrentWorld(target);

		context.getSource().sendSuccess(
				() -> Component.literal(String.format(Locale.ROOT, ModTexts.cmdTeleported(), target.displayName()))
						.withStyle(ChatFormatting.GREEN),
				true
		);
		return Command.SINGLE_SUCCESS;
	}

	private static int debug(CommandContext<CommandSourceStack> context) {
		GameManager gameManager = GameManager.getInstance();
		CountdownManager countdown = CountdownManager.getInstance();
		TransitionManager transitionManager = WorldShift.getInstance().getTransitionManager();
		WorldManager worldManager = WorldShift.getInstance().getWorldManager();
		ConfigManager configManager = WorldShift.getInstance().getConfigManager();
		WorldSelection activeSelection = transitionManager.getActiveSelection();
		boolean running = gameManager.isRunning();

		CommandSourceStack source = context.getSource();
		String coordinates = ModTexts.debugNa();
		String dimension = ModTexts.debugNa();

		if (source.getEntity() instanceof ServerPlayer player) {
			Vec3 pos = player.position();
			coordinates = String.format(Locale.ROOT, "%.2f %.2f %.2f", pos.x, pos.y, pos.z);
			dimension = player.level().dimension().identifier().toString();
		}

		String transitionState;
		if (transitionManager.isActive()) {
			transitionState = "AKTIF (" + transitionManager.getTicksRemaining() + " tick)";
		} else if (countdown.isFrozen()) {
			transitionState = "DONDURULDU";
		} else if (countdown.isActive()) {
			transitionState = "GERI SAYIM";
		} else if (running) {
			transitionState = "BOS";
		} else {
			transitionState = "DURDURULDU";
		}

		String countdownState = countdown.isActive()
				? ModTexts.debugActive()
				: countdown.isFrozen() ? ModTexts.debugFrozen() : ModTexts.debugIdle();

		String finalCoordinates = coordinates;
		String finalDimension = dimension;
		String finalTransitionState = transitionState;

		source.sendSuccess(
				() -> Component.literal(ModTexts.cmdDebugTitle())
						.withStyle(ChatFormatting.AQUA)
						.append(Component.literal("\n  " + ModTexts.debugCurrent() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(worldManager.getCurrentWorld().displayName()))
						.append(Component.literal("\n  " + ModTexts.debugNext() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(worldManager.getNextWorld().displayName()))
						.append(Component.literal("\n  " + ModTexts.debugCountdown() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(countdown.getSecondsRemaining() + "sn (" + countdownState + ")"))
						.append(Component.literal("\n  " + ModTexts.debugCoords() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(finalCoordinates))
						.append(Component.literal("\n  " + ModTexts.debugDimension() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(finalDimension))
						.append(Component.literal("\n  " + ModTexts.debugTransition() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(finalTransitionState))
						.append(Component.literal("\n  " + ModTexts.debugRunning() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(running ? "evet" : "hayir")
								.withStyle(running ? ChatFormatting.GREEN : ChatFormatting.RED))
						.append(Component.literal("\n  " + ModTexts.debugSelection() + ": ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(activeSelection == null
								? ModTexts.debugNone()
								: activeSelection.currentWorld().displayName()
										+ " -> "
										+ activeSelection.nextWorld().displayName()))
						.append(Component.literal("\n  configSure: ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(Integer.toString(configManager.getCountdownSeconds())))
						.append(Component.literal("\n  configGecis: ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(Integer.toString(configManager.getTransitionDelaySeconds())))
						.append(Component.literal("\n  debug: ").withStyle(ChatFormatting.GRAY))
						.append(Component.literal(configManager.isDebug() ? "acik" : "kapali")),
				false
		);
		return Command.SINGLE_SUCCESS;
	}

	private static CompletableFuture<Suggestions> suggestWorlds(
			CommandContext<CommandSourceStack> context,
			SuggestionsBuilder builder
	) {
		return SharedSuggestionProvider.suggest(
				Arrays.stream(WorldType.values()).map(world -> world.name().toLowerCase(Locale.ROOT)),
				builder
		);
	}
}
