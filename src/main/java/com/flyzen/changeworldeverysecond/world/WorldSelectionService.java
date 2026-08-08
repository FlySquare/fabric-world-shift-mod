package com.flyzen.changeworldeverysecond.world;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

public final class WorldSelectionService {
	private final WorldRegistry registry;
	private final RandomGenerator random;

	private WorldType currentWorld;
	private WorldType preparedNext;

	public WorldSelectionService(WorldRegistry registry) {
		this(registry, RandomGeneratorFactory.of("L64X128MixRandom").create());
	}

	public WorldSelectionService(WorldRegistry registry, RandomGenerator random) {
		this.registry = Objects.requireNonNull(registry, "registry");
		this.random = Objects.requireNonNull(random, "random");
	}

	public void reset(WorldType startingWorld) {
		Objects.requireNonNull(startingWorld, "startingWorld");

		if (!registry.isRegistered(startingWorld)) {
			throw new IllegalArgumentException("Starting world is not registered: " + startingWorld);
		}

		this.currentWorld = startingWorld;
		this.preparedNext = null;
		prepareNext();
	}

	public WorldType getCurrentWorld() {
		return currentWorld;
	}

	public void setCurrentWorld(WorldType worldType) {
		Objects.requireNonNull(worldType, "worldType");

		if (!registry.isRegistered(worldType)) {
			throw new IllegalArgumentException("World is not registered: " + worldType);
		}

		this.currentWorld = worldType;
		this.preparedNext = null;
		prepareNext();
	}

	public WorldType prepareNext() {
		ensureInitialized();
		preparedNext = chooseNext(currentWorld);
		return preparedNext;
	}

	public WorldType getPreparedNext() {
		ensureInitialized();
		if (preparedNext == null) {
			prepareNext();
		}
		return preparedNext;
	}

	public WorldSelection advance() {
		ensureInitialized();

		WorldType from = currentWorld;
		WorldType to = preparedNext != null ? preparedNext : chooseNext(from);
		currentWorld = to;
		preparedNext = null;
		return new WorldSelection(from, to);
	}

	public WorldType peekNext() {
		return getPreparedNext();
	}

	private WorldType chooseNext(WorldType from) {
		List<WorldType> candidates = registry.getCandidatesExcluding(from);

		if (candidates.isEmpty()) {
			return from;
		}

		int index = random.nextInt(candidates.size());
		return candidates.get(index);
	}

	private void ensureInitialized() {
		if (currentWorld == null) {
			throw new IllegalStateException("WorldSelectionService has not been reset with a starting world");
		}

		if (registry.isEmpty()) {
			throw new IllegalStateException("WorldRegistry has no available worlds");
		}
	}
}
