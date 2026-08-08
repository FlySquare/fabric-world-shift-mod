package com.flyzen.changeworldeverysecond.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.flyzen.changeworldeverysecond.util.IdentifierUtil;
import com.flyzen.changeworldeverysecond.world.WorldType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class WorldShiftSavedData extends SavedData {
	private static final Codec<LocationEntry> LOCATION_ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			UUIDUtil.STRING_CODEC.fieldOf("player").forGetter(LocationEntry::playerId),
			WorldType.CODEC.fieldOf("world").forGetter(LocationEntry::worldType),
			PlayerLocation.CODEC.fieldOf("location").forGetter(LocationEntry::location)
	).apply(instance, LocationEntry::new));

	public static final Codec<WorldShiftSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			WorldType.CODEC.optionalFieldOf("current_world", WorldType.OVERWORLD).forGetter(data -> data.currentWorld),
			LOCATION_ENTRY_CODEC.listOf().optionalFieldOf("locations", List.of()).forGetter(WorldShiftSavedData::encodeLocations),
			WorldType.CODEC.listOf().optionalFieldOf("bootstrapped_worlds", List.of()).forGetter(data -> List.copyOf(data.bootstrappedWorlds))
	).apply(instance, WorldShiftSavedData::fromEncoded));

	public static final SavedDataType<WorldShiftSavedData> TYPE = new SavedDataType<>(
			IdentifierUtil.of("world_shift_state"),
			WorldShiftSavedData::new,
			CODEC,
			null
	);

	private WorldType currentWorld;
	private final Map<UUID, Map<WorldType, PlayerLocation>> locations;
	private final Set<WorldType> bootstrappedWorlds;

	public WorldShiftSavedData() {
		this.currentWorld = WorldType.OVERWORLD;
		this.locations = new HashMap<>();
		this.bootstrappedWorlds = new HashSet<>();
	}

	private WorldShiftSavedData(
			WorldType currentWorld,
			Map<UUID, Map<WorldType, PlayerLocation>> locations,
			Set<WorldType> bootstrappedWorlds
	) {
		this.currentWorld = currentWorld == null ? WorldType.OVERWORLD : currentWorld;
		this.locations = locations;
		this.bootstrappedWorlds = bootstrappedWorlds;
	}

	private static WorldShiftSavedData fromEncoded(
			WorldType currentWorld,
			List<LocationEntry> locationEntries,
			List<WorldType> bootstrapped
	) {
		Map<UUID, Map<WorldType, PlayerLocation>> locations = new HashMap<>();
		for (LocationEntry entry : locationEntries) {
			locations.computeIfAbsent(entry.playerId(), ignored -> new HashMap<>())
					.put(entry.worldType(), entry.location());
		}

		return new WorldShiftSavedData(currentWorld, locations, new HashSet<>(bootstrapped));
	}

	private List<LocationEntry> encodeLocations() {
		List<LocationEntry> entries = new ArrayList<>();
		locations.forEach((playerId, worldMap) -> worldMap.forEach((worldType, location) ->
				entries.add(new LocationEntry(playerId, worldType, location))
		));
		return entries;
	}

	public static WorldShiftSavedData get(MinecraftServer server) {
		ServerLevel overworld = server.overworld();
		return overworld.getDataStorage().computeIfAbsent(TYPE);
	}

	public WorldType getCurrentWorld() {
		return currentWorld;
	}

	public void setCurrentWorld(WorldType worldType) {
		this.currentWorld = Objects.requireNonNull(worldType, "worldType");
		setDirty();
	}

	public void saveLocation(UUID playerId, WorldType worldType, PlayerLocation location) {
		Objects.requireNonNull(playerId, "playerId");
		Objects.requireNonNull(worldType, "worldType");
		Objects.requireNonNull(location, "location");

		locations.computeIfAbsent(playerId, ignored -> new HashMap<>()).put(worldType, location);
		setDirty();
	}

	public Optional<PlayerLocation> getLocation(UUID playerId, WorldType worldType) {
		Map<WorldType, PlayerLocation> playerLocations = locations.get(playerId);
		if (playerLocations == null) {
			return Optional.empty();
		}

		return Optional.ofNullable(playerLocations.get(worldType));
	}

	public void clearLocation(UUID playerId, WorldType worldType) {
		Map<WorldType, PlayerLocation> playerLocations = locations.get(playerId);
		if (playerLocations == null) {
			return;
		}

		if (playerLocations.remove(worldType) != null) {
			setDirty();
		}
	}

	public void clearLocationsForWorld(WorldType worldType) {
		boolean changed = false;
		for (Map<WorldType, PlayerLocation> playerLocations : locations.values()) {
			if (playerLocations.remove(worldType) != null) {
				changed = true;
			}
		}
		if (changed) {
			setDirty();
		}
	}

	public boolean isBootstrapped(WorldType worldType) {
		return bootstrappedWorlds.contains(worldType);
	}

	public void markBootstrapped(WorldType worldType) {
		if (bootstrappedWorlds.add(worldType)) {
			setDirty();
		}
	}

	private record LocationEntry(UUID playerId, WorldType worldType, PlayerLocation location) {
	}
}
