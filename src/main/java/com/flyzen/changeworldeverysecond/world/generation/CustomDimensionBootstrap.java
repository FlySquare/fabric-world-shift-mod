package com.flyzen.changeworldeverysecond.world.generation;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.persistence.WorldShiftSavedData;
import com.flyzen.changeworldeverysecond.world.DimensionKeys;
import com.flyzen.changeworldeverysecond.world.WorldType;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public final class CustomDimensionBootstrap {
	private final SkyblockIslandGenerator skyblockIslandGenerator = new SkyblockIslandGenerator();
	private final DeepDarkCavernGenerator deepDarkCavernGenerator = new DeepDarkCavernGenerator();
	private final MushroomIslandGenerator mushroomIslandGenerator = new MushroomIslandGenerator();

	public void init() {
		ServerLevelEvents.LOAD.register(this::onLevelLoad);
		WorldShift.LOGGER.info("CustomDimensionBootstrap ready");
	}

	private void onLevelLoad(MinecraftServer server, ServerLevel level) {
		WorldShiftSavedData data = WorldShiftSavedData.get(server);

		if (level.dimension().equals(DimensionKeys.SKYBLOCK) && !data.isBootstrapped(WorldType.SKYBLOCK)) {
			skyblockIslandGenerator.generate(level);
			data.markBootstrapped(WorldType.SKYBLOCK);
		}

		if (level.dimension().equals(DimensionKeys.MUSHROOM_ISLAND)) {
			data.migrateMushroomBootstrap();
			if (!data.isBootstrapped(WorldType.MUSHROOM_ISLAND)) {
				mushroomIslandGenerator.generate(level);
				data.clearLocationsForWorld(WorldType.MUSHROOM_ISLAND);
				data.markBootstrapped(WorldType.MUSHROOM_ISLAND);
			}
		}

		if (level.dimension().equals(DimensionKeys.DEEP_DARK) && !data.isBootstrapped(WorldType.DEEP_DARK)) {
			deepDarkCavernGenerator.generate(level);
			data.clearLocationsForWorld(WorldType.DEEP_DARK);
			data.markBootstrapped(WorldType.DEEP_DARK);
		}
	}
}
