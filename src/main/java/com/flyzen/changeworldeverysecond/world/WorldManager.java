package com.flyzen.changeworldeverysecond.world;

import java.util.Set;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.config.ConfigManager;
import com.flyzen.changeworldeverysecond.persistence.WorldShiftSavedData;

import net.minecraft.server.MinecraftServer;

public final class WorldManager {
	private final DimensionResolver dimensionResolver = new DimensionResolver();

	private WorldRegistry registry;
	private WorldSelectionService selectionService;

	public void init() {
		registry = new WorldRegistry();
		applyEnabledWorlds(config().getEnabledWorlds());

		selectionService = new WorldSelectionService(registry);
		WorldType starting = resolveStartingWorld(config().getStartingWorld());
		selectionService.reset(starting);

		WorldShift.LOGGER.info(
				"WorldManager ready ({} worlds, starting at {})",
				registry.size(),
				starting.displayName()
		);
	}

	public void applyEnabledWorlds(Set<WorldType> enabledWorlds) {
		registry.clear();
		if (enabledWorlds == null || enabledWorlds.isEmpty()) {
			registry.registerDefaults();
			return;
		}

		for (WorldType worldType : enabledWorlds) {
			registry.register(worldType);
		}
	}

	public void onSessionStart(MinecraftServer server) {
		WorldShiftSavedData data = WorldShiftSavedData.get(server);
		WorldType persisted = data.getCurrentWorld();
		WorldType starting = registry.isRegistered(persisted) ? persisted : resolveStartingWorld(config().getStartingWorld());
		selectionService.reset(starting);
		data.setCurrentWorld(starting);
	}

	public void onSessionStop() {
		selectionService.prepareNext();
	}

	public WorldSelection performShift() {
		WorldSelection selection = selectionService.advance();

		WorldShift.LOGGER.info("Current World: {}", selection.currentWorld().displayName());
		WorldShift.LOGGER.info("Next World: {}", selection.nextWorld().displayName());

		return selection;
	}

	public void prepareNextWorld() {
		selectionService.prepareNext();
	}

	public WorldType forceWorld(WorldType worldType) {
		selectionService.setCurrentWorld(worldType);
		return worldType;
	}

	public WorldRegistry getRegistry() {
		return registry;
	}

	public WorldSelectionService getSelectionService() {
		return selectionService;
	}

	public DimensionResolver getDimensionResolver() {
		return dimensionResolver;
	}

	public WorldType getCurrentWorld() {
		return selectionService.getCurrentWorld();
	}

	public WorldType getNextWorld() {
		return selectionService.getPreparedNext();
	}

	private WorldType resolveStartingWorld(WorldType preferred) {
		if (registry.isRegistered(preferred)) {
			return preferred;
		}

		if (registry.isEmpty()) {
			registry.registerDefaults();
		}

		return registry.getAvailableWorlds().getFirst();
	}

	private static ConfigManager config() {
		return WorldShift.getInstance().getConfigManager();
	}
}
