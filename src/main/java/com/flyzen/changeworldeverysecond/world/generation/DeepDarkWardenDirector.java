package com.flyzen.changeworldeverysecond.world.generation;

import java.util.List;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.util.ModConstants;
import com.flyzen.changeworldeverysecond.world.DimensionKeys;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

public final class DeepDarkWardenDirector {
	private static final int TARGET_WARDEN_COUNT = 3;
	private static final int SEARCH_RADIUS = 64;
	private static final int MAX_ANGER = 150;
	private static final BlockPos[] SPAWN_POINTS = {
			new BlockPos(18, DeepDarkCavernGenerator.SPAWN_Y, 16),
			new BlockPos(-16, DeepDarkCavernGenerator.SPAWN_Y, 12),
			new BlockPos(10, DeepDarkCavernGenerator.SPAWN_Y, -18)
	};

	private int tickCounter;

	public void tick(MinecraftServer server) {
		tickCounter++;
		if (tickCounter < ModConstants.TICKS_PER_SECOND) {
			return;
		}
		tickCounter = 0;

		clearDarkness(server);

		ServerLevel level = server.getLevel(DimensionKeys.DEEP_DARK);
		if (level == null) {
			return;
		}

		List<ServerPlayer> players = level.players();
		if (players.isEmpty()) {
			return;
		}

		DeepDarkCavernGenerator.placeAmbientLights(level);
		ensureWardens(level);
		angerWardensAtPlayers(level, players);
	}

	public void onPlayersEntered(ServerLevel level) {
		resetEncounter(level);
	}

	public void resetEncounter(ServerLevel level) {
		if (level == null || !level.dimension().equals(DimensionKeys.DEEP_DARK)) {
			return;
		}

		DeepDarkCavernGenerator.placeAmbientLights(level);
		clearWardens(level);
		for (BlockPos pos : SPAWN_POINTS) {
			spawnAggressiveWarden(level, pos);
		}
		angerWardensAtPlayers(level, level.players());
		WorldShift.LOGGER.info("Reset Deep Dark encounter with {} wardens", TARGET_WARDEN_COUNT);
	}

	private void clearWardens(ServerLevel level) {
		AABB area = searchArea();
		for (Warden warden : List.copyOf(level.getEntitiesOfClass(Warden.class, area))) {
			warden.discard();
		}
	}

	private static void clearDarkness(MinecraftServer server) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.hasEffect(MobEffects.DARKNESS)) {
				player.removeEffect(MobEffects.DARKNESS);
			}
		}
	}

	private void ensureWardens(ServerLevel level) {
		AABB area = searchArea();
		List<Warden> wardens = level.getEntitiesOfClass(Warden.class, area);
		int missing = TARGET_WARDEN_COUNT - wardens.size();
		for (int i = 0; i < missing; i++) {
			BlockPos pos = SPAWN_POINTS[i % SPAWN_POINTS.length];
			spawnAggressiveWarden(level, pos);
		}
	}

	private void angerWardensAtPlayers(ServerLevel level, List<ServerPlayer> players) {
		if (players.isEmpty()) {
			return;
		}

		for (Warden warden : level.getEntitiesOfClass(Warden.class, searchArea())) {
			ServerPlayer nearest = null;
			double nearestDist = Double.MAX_VALUE;
			for (ServerPlayer player : players) {
				if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
					continue;
				}
				double dist = warden.distanceToSqr(player);
				if (dist < nearestDist) {
					nearestDist = dist;
					nearest = player;
				}
			}

			if (nearest != null) {
				armWarden(warden, nearest);
			}
		}
	}

	private static AABB searchArea() {
		return new AABB(
				-SEARCH_RADIUS,
				DeepDarkCavernGenerator.FLOOR_Y - 8,
				-SEARCH_RADIUS,
				SEARCH_RADIUS,
				DeepDarkCavernGenerator.FLOOR_Y + DeepDarkCavernGenerator.RADIUS_Y + 8,
				SEARCH_RADIUS
		);
	}

	public static void spawnAggressiveWarden(ServerLevel level, BlockPos pos) {
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
		level.setBlock(pos.above(2), Blocks.AIR.defaultBlockState(), 3);
		level.setBlock(pos.below(), Blocks.SCULK.defaultBlockState(), 3);

		Warden warden = EntityTypes.WARDEN.spawn(level, pos, EntitySpawnReason.EVENT);
		if (warden == null) {
			return;
		}

		warden.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

		ServerPlayer nearest = null;
		double nearestDist = Double.MAX_VALUE;
		for (ServerPlayer player : level.players()) {
			if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
				continue;
			}
			double dist = warden.distanceToSqr(player);
			if (dist < nearestDist) {
				nearestDist = dist;
				nearest = player;
			}
		}
		if (nearest != null) {
			armWarden(warden, nearest);
		}

		WorldShift.LOGGER.info("Spawned aggressive Warden at {}", pos);
	}

	public static void armWarden(Warden warden, ServerPlayer target) {
		warden.increaseAngerAt(target, MAX_ANGER, false);
		warden.setAttackTarget(target);
	}
}
