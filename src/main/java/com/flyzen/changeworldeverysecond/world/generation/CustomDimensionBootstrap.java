package com.flyzen.changeworldeverysecond.world.generation;

import com.flyzen.changeworldeverysecond.WorldShift;
import com.flyzen.changeworldeverysecond.persistence.WorldShiftSavedData;
import com.flyzen.changeworldeverysecond.world.DimensionKeys;
import com.flyzen.changeworldeverysecond.world.WorldType;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class CustomDimensionBootstrap {
	private static final int MUSHROOM_ISLAND_RADIUS = 12;
	private static final int MUSHROOM_SURFACE_Y = 80;

	private final SkyblockIslandGenerator skyblockIslandGenerator = new SkyblockIslandGenerator();
	private final DeepDarkCavernGenerator deepDarkCavernGenerator = new DeepDarkCavernGenerator();

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

		if (level.dimension().equals(DimensionKeys.MUSHROOM_ISLAND) && !data.isBootstrapped(WorldType.MUSHROOM_ISLAND)) {
			ensureMushroomIsland(level);
			data.markBootstrapped(WorldType.MUSHROOM_ISLAND);
		}

		if (level.dimension().equals(DimensionKeys.DEEP_DARK) && !data.isBootstrapped(WorldType.DEEP_DARK)) {
			deepDarkCavernGenerator.generate(level);
			data.clearLocationsForWorld(WorldType.DEEP_DARK);
			data.markBootstrapped(WorldType.DEEP_DARK);
		}
	}

	private void ensureMushroomIsland(ServerLevel level) {
		level.getChunk(0, 0);
		int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 0, 0);
		if (surfaceY <= level.getMinY() + 1) {
			surfaceY = MUSHROOM_SURFACE_Y;
			placeMyceliumIsland(level, surfaceY);
		}

		placeGiantMushroom(level, new BlockPos(-6, surfaceY + 1, -4), true);
		placeGiantMushroom(level, new BlockPos(5, surfaceY + 1, 3), false);
		WorldShift.LOGGER.info("Bootstrapped mushroom island landmarks at y={}", surfaceY);
	}

	private void placeMyceliumIsland(ServerLevel level, int surfaceY) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		int radius = MUSHROOM_ISLAND_RADIUS;

		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if ((dx * dx) + (dz * dz) > (radius * radius)) {
					continue;
				}

				cursor.set(dx, surfaceY - 3, dz);
				level.setBlock(cursor, Blocks.STONE.defaultBlockState(), 3);
				cursor.set(dx, surfaceY - 2, dz);
				level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), 3);
				cursor.set(dx, surfaceY - 1, dz);
				level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), 3);
				cursor.set(dx, surfaceY, dz);
				level.setBlock(cursor, Blocks.MYCELIUM.defaultBlockState(), 3);
			}
		}

		level.setBlock(new BlockPos(2, surfaceY + 1, 1), Blocks.RED_MUSHROOM.defaultBlockState(), 3);
		level.setBlock(new BlockPos(-3, surfaceY + 1, 2), Blocks.BROWN_MUSHROOM.defaultBlockState(), 3);
	}

	private void placeGiantMushroom(ServerLevel level, BlockPos base, boolean red) {
		BlockState stem = Blocks.MUSHROOM_STEM.defaultBlockState();
		BlockState cap = (red ? Blocks.RED_MUSHROOM_BLOCK : Blocks.BROWN_MUSHROOM_BLOCK).defaultBlockState()
				.setValue(HugeMushroomBlock.DOWN, false);

		for (int y = 0; y < 5; y++) {
			level.setBlock(base.above(y), stem, 3);
		}

		int capY = 5;
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				if (Math.abs(dx) == 2 && Math.abs(dz) == 2) {
					continue;
				}

				level.setBlock(base.offset(dx, capY, dz), cap, 3);
			}
		}

		if (red) {
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					level.setBlock(base.offset(dx, capY + 1, dz), cap, 3);
				}
			}
		}
	}
}
