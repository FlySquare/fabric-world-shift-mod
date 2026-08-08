package com.flyzen.changeworldeverysecond.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class WorldRegistry {
	private final EnumSet<WorldType> worlds = EnumSet.noneOf(WorldType.class);

	public void registerDefaults() {
		Collections.addAll(worlds, WorldType.values());
	}

	public void clear() {
		worlds.clear();
	}

	public void register(WorldType worldType) {
		Objects.requireNonNull(worldType, "worldType");
		worlds.add(worldType);
	}

	public void unregister(WorldType worldType) {
		Objects.requireNonNull(worldType, "worldType");
		worlds.remove(worldType);
	}

	public boolean isRegistered(WorldType worldType) {
		return worlds.contains(worldType);
	}

	public List<WorldType> getAvailableWorlds() {
		return List.copyOf(worlds);
	}

	public Set<WorldType> getAvailableWorldSet() {
		return Collections.unmodifiableSet(worlds);
	}

	public int size() {
		return worlds.size();
	}

	public boolean isEmpty() {
		return worlds.isEmpty();
	}

	public List<WorldType> getCandidatesExcluding(WorldType excluded) {
		List<WorldType> candidates = new ArrayList<>(worlds.size());

		for (WorldType worldType : worlds) {
			if (worldType != excluded) {
				candidates.add(worldType);
			}
		}

		return List.copyOf(candidates);
	}
}
