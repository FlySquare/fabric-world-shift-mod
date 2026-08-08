package com.flyzen.changeworldeverysecond.world;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public final class DimensionResolver {
	public ResourceKey<Level> keyFor(WorldType worldType) {
		Objects.requireNonNull(worldType, "worldType");

		return switch (worldType) {
			case OVERWORLD -> Level.OVERWORLD;
			case NETHER -> Level.NETHER;
			case END -> Level.END;
			case SKYBLOCK -> DimensionKeys.SKYBLOCK;
			case DEEP_DARK -> DimensionKeys.DEEP_DARK;
			case MUSHROOM_ISLAND -> DimensionKeys.MUSHROOM_ISLAND;
		};
	}

	public Optional<WorldType> worldTypeFor(ResourceKey<Level> dimension) {
		if (dimension == null) {
			return Optional.empty();
		}

		if (dimension.equals(Level.OVERWORLD)) {
			return Optional.of(WorldType.OVERWORLD);
		}
		if (dimension.equals(Level.NETHER)) {
			return Optional.of(WorldType.NETHER);
		}
		if (dimension.equals(Level.END)) {
			return Optional.of(WorldType.END);
		}
		if (dimension.equals(DimensionKeys.SKYBLOCK)) {
			return Optional.of(WorldType.SKYBLOCK);
		}
		if (dimension.equals(DimensionKeys.DEEP_DARK)) {
			return Optional.of(WorldType.DEEP_DARK);
		}
		if (dimension.equals(DimensionKeys.MUSHROOM_ISLAND)) {
			return Optional.of(WorldType.MUSHROOM_ISLAND);
		}

		return Optional.empty();
	}

	public Optional<WorldType> worldTypeFor(ServerLevel level) {
		return level == null ? Optional.empty() : worldTypeFor(level.dimension());
	}

	public ServerLevel resolve(MinecraftServer server, WorldType worldType) {
		Objects.requireNonNull(server, "server");
		ResourceKey<Level> key = keyFor(worldType);
		ServerLevel level = server.getLevel(key);

		if (level == null) {
			throw new IllegalStateException("Dimension is not loaded: " + key.identifier());
		}

		return level;
	}

	public Optional<ServerLevel> tryResolve(MinecraftServer server, WorldType worldType) {
		Objects.requireNonNull(server, "server");
		return Optional.ofNullable(server.getLevel(keyFor(worldType)));
	}
}
