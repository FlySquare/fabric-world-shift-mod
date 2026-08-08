package com.flyzen.changeworldeverysecond.teleport;

import java.util.Objects;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.events.EventBus;
import com.flyzen.changeworldeverysecond.events.TransitionFinishedEvent;
import com.flyzen.changeworldeverysecond.events.TransitionStartedEvent;
import com.flyzen.changeworldeverysecond.game.GameManager;
import com.flyzen.changeworldeverysecond.persistence.PlayerLocation;
import com.flyzen.changeworldeverysecond.persistence.WorldShiftSavedData;
import com.flyzen.changeworldeverysecond.world.DimensionResolver;
import com.flyzen.changeworldeverysecond.world.WorldType;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

public final class TeleportManager {
	private final SpawnLocator spawnLocator = new SpawnLocator();
	private final DimensionResolver dimensionResolver;

	public TeleportManager(DimensionResolver dimensionResolver) {
		this.dimensionResolver = Objects.requireNonNull(dimensionResolver, "dimensionResolver");
	}

	public void init(EventBus eventBus) {
		Objects.requireNonNull(eventBus, "eventBus");
		eventBus.subscribe(TransitionStartedEvent.class, this::onTransitionStarted);
		eventBus.subscribe(TransitionFinishedEvent.class, this::onTransitionFinished);

		ServerLivingEntityEvents.AFTER_DEATH.register(this::onPlayerDeath);
		ServerPlayerEvents.AFTER_RESPAWN.register(this::onPlayerRespawn);

		WorldShift.LOGGER.info("TeleportManager ready");
	}

	private void onTransitionStarted(TransitionStartedEvent event) {
		saveAllPlayersInCurrentWorld(event.server(), event.selection().currentWorld());
	}

	private void onTransitionFinished(TransitionFinishedEvent event) {
		teleportAllPlayers(event.server(), event.selection().nextWorld());
		WorldShiftSavedData.get(event.server()).setCurrentWorld(event.selection().nextWorld());
	}

	private void onPlayerDeath(LivingEntity entity, DamageSource source) {
		if (!(entity instanceof ServerPlayer player)) {
			return;
		}

		MinecraftServer server = player.level().getServer();
		if (server == null) {
			return;
		}

		dimensionResolver.worldTypeFor(player.level()).ifPresent(worldType -> {
			WorldShiftSavedData data = WorldShiftSavedData.get(server);
			data.clearLocation(player.getUUID(), worldType);
			WorldShift.LOGGER.info(
					"Cleared unsafe/death location for {} in {}",
					player.getGameProfile().name(),
					worldType.displayName()
			);
		});
	}

	private void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
		if (!GameManager.getInstance().isRunning()) {
			return;
		}

		MinecraftServer server = newPlayer.level().getServer();
		WorldType currentWorld = WorldShift.getInstance().getWorldManager().getCurrentWorld();
		WorldShiftSavedData.get(server).clearLocation(newPlayer.getUUID(), currentWorld);
		teleportPlayer(server, newPlayer, currentWorld, true);
	}

	public void saveAllPlayersInCurrentWorld(MinecraftServer server, WorldType currentWorld) {
		if (currentWorld == WorldType.DEEP_DARK) {
			WorldShiftSavedData.get(server).clearLocationsForWorld(WorldType.DEEP_DARK);
			return;
		}

		WorldShiftSavedData data = WorldShiftSavedData.get(server);
		ServerLevel expectedLevel = dimensionResolver.resolve(server, currentWorld);

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.level() != expectedLevel) {
				continue;
			}

			PlayerLocation location = PlayerLocation.from(player);
			if (!spawnLocator.isSafeLocation(expectedLevel, location.asVec3())) {
				WorldShift.LOGGER.info(
						"Skipping unsafe location save for {} in {}",
						player.getGameProfile().name(),
						currentWorld.displayName()
				);
				continue;
			}

			data.saveLocation(player.getUUID(), currentWorld, location);
		}
	}

	public void teleportAllPlayers(MinecraftServer server, WorldType targetWorld) {
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			teleportPlayer(server, player, targetWorld, false);
		}
		if (targetWorld == WorldType.DEEP_DARK) {
			ServerLevel deepDark = dimensionResolver.resolve(server, WorldType.DEEP_DARK);
			WorldShift.getInstance().getDeepDarkWardenDirector().resetEncounter(deepDark);
		}
	}

	public void teleportPlayer(MinecraftServer server, ServerPlayer player, WorldType targetWorld) {
		teleportPlayer(server, player, targetWorld, false);
		if (targetWorld == WorldType.DEEP_DARK) {
			ServerLevel deepDark = dimensionResolver.resolve(server, WorldType.DEEP_DARK);
			WorldShift.getInstance().getDeepDarkWardenDirector().resetEncounter(deepDark);
		}
	}

	public void teleportPlayer(MinecraftServer server, ServerPlayer player, WorldType targetWorld, boolean forceSpawn) {
		Objects.requireNonNull(server, "server");
		Objects.requireNonNull(player, "player");
		Objects.requireNonNull(targetWorld, "targetWorld");

		if (targetWorld == WorldType.DEEP_DARK) {
			forceSpawn = true;
		}

		ServerLevel targetLevel = dimensionResolver.resolve(server, targetWorld);
		WorldShiftSavedData data = WorldShiftSavedData.get(server);

		Vec3 position;
		float yaw;
		float pitch;

		PlayerLocation stored = forceSpawn ? null : data.getLocation(player.getUUID(), targetWorld).orElse(null);
		if (stored != null && spawnLocator.isSafeLocation(targetLevel, stored.asVec3())) {
			position = stored.asVec3();
			yaw = stored.yaw();
			pitch = stored.pitch();
		} else {
			if (stored != null) {
				data.clearLocation(player.getUUID(), targetWorld);
			}

			position = spawnLocator.findSpawn(targetLevel, targetWorld);
			yaw = player.getYRot();
			pitch = player.getXRot();
			if (targetWorld != WorldType.DEEP_DARK) {
				data.saveLocation(
						player.getUUID(),
						targetWorld,
						new PlayerLocation(position.x, position.y, position.z, yaw, pitch)
				);
			} else {
				data.clearLocation(player.getUUID(), WorldType.DEEP_DARK);
			}
		}

		TeleportTransition transition = new TeleportTransition(
				targetLevel,
				position,
				Vec3.ZERO,
				yaw,
				pitch,
				TeleportTransition.DO_NOTHING
		);

		player.teleport(transition);
		updateRespawnPoint(player, targetLevel, position, yaw, pitch);
		playArrivalFx(player);

		WorldShift.LOGGER.info(
				"Teleported {} to {} at {}, {}, {}",
				player.getGameProfile().name(),
				targetWorld.displayName(),
				position.x,
				position.y,
				position.z
		);
	}

	private void playArrivalFx(ServerPlayer player) {
		ServerLevel level = player.level();
		Vec3 pos = player.position();

		level.playSound(
				null,
				pos.x,
				pos.y,
				pos.z,
				SoundEvents.ENDERMAN_TELEPORT,
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);
		level.sendParticles(ParticleTypes.PORTAL, pos.x, pos.y + 1.0, pos.z, 48, 0.6, 0.9, 0.6, 0.7);
		level.sendParticles(ParticleTypes.REVERSE_PORTAL, pos.x, pos.y + 1.0, pos.z, 20, 0.4, 0.7, 0.4, 0.2);
	}

	private void updateRespawnPoint(ServerPlayer player, ServerLevel level, Vec3 position, float yaw, float pitch) {
		BlockPos blockPos = BlockPos.containing(position.x, position.y, position.z);
		LevelData.RespawnData respawnData = LevelData.RespawnData.of(level.dimension(), blockPos, yaw, pitch);
		player.setRespawnPosition(new ServerPlayer.RespawnConfig(respawnData, true), false);
	}
}
