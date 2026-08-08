package com.flyzen.changeworldeverysecond.game;

import java.util.Objects;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.events.EventBus;
import com.flyzen.changeworldeverysecond.events.TransitionFinishedEvent;
import com.flyzen.changeworldeverysecond.events.TransitionStartedEvent;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public final class TransitionFxManager {
	private static final int PARTICLE_INTERVAL_TICKS = 2;

	private int tickCounter;
	private boolean active;

	public void init(EventBus eventBus) {
		Objects.requireNonNull(eventBus, "eventBus");
		eventBus.subscribe(TransitionStartedEvent.class, this::onTransitionStarted);
		eventBus.subscribe(TransitionFinishedEvent.class, this::onTransitionFinished);
		WorldShift.LOGGER.info("TransitionFxManager ready");
	}

	private void onTransitionStarted(TransitionStartedEvent event) {
		active = true;
		tickCounter = 0;

		for (ServerPlayer player : event.server().getPlayerList().getPlayers()) {
			burstPortalParticles(player, 28);
		}
	}

	private void onTransitionFinished(TransitionFinishedEvent event) {
		active = false;
		tickCounter = 0;
	}

	public void tick(MinecraftServer server) {
		if (!active) {
			return;
		}

		tickCounter++;

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (tickCounter % PARTICLE_INTERVAL_TICKS == 0) {
				ambientPortalParticles(player);
			}
		}
	}

	public void cancel(MinecraftServer server) {
		active = false;
		tickCounter = 0;
	}

	private void ambientPortalParticles(ServerPlayer player) {
		ServerLevel level = player.level();
		Vec3 pos = player.position();
		RandomSource random = player.getRandom();

		level.sendParticles(
				ParticleTypes.PORTAL,
				pos.x,
				pos.y + player.getBbHeight() * 0.55,
				pos.z,
				10,
				0.4,
				0.65,
				0.4,
				0.45
		);
		level.sendParticles(
				ParticleTypes.REVERSE_PORTAL,
				pos.x + (random.nextDouble() - 0.5) * 0.9,
				pos.y + random.nextDouble() * player.getBbHeight(),
				pos.z + (random.nextDouble() - 0.5) * 0.9,
				3,
				0.05,
				0.05,
				0.05,
				0.03
		);
	}

	private void burstPortalParticles(ServerPlayer player, int count) {
		ServerLevel level = player.level();
		Vec3 pos = player.position();

		level.sendParticles(
				ParticleTypes.PORTAL,
				pos.x,
				pos.y + 1.0,
				pos.z,
				count,
				0.55,
				0.85,
				0.55,
				0.65
		);
		level.sendParticles(
				ParticleTypes.REVERSE_PORTAL,
				pos.x,
				pos.y + 1.0,
				pos.z,
				count / 2,
				0.35,
				0.6,
				0.35,
				0.2
		);
	}
}
