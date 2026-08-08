package com.flyzen.changeworldeverysecond.world.generation;

import com.flyzen.changeworldeverysecond.WorldShift;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class MushroomIslandGenerator {
	public static final int SPAWN_RADIUS = 24;
	private static final int MOOSHROOM_COUNT = 8;

	public void generate(ServerLevel level) {
		forceChunks(level);

		int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 0, 0);
		if (surfaceY <= level.getMinY() + 1) {
			surfaceY = 80;
			placeMyceliumPlateau(level, surfaceY, SPAWN_RADIUS + 8);
		} else {
			carpetSpawnAreaWithMycelium(level, SPAWN_RADIUS + 8);
			surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, 0, 0);
		}

		clearSpawnPad(level, surfaceY);
		scatterMushrooms(level, surfaceY);
		placeGiantMushroom(level, new BlockPos(-8, surfaceY + 1, -5), true);
		placeGiantMushroom(level, new BlockPos(7, surfaceY + 1, 4), false);
		placeGiantMushroom(level, new BlockPos(-3, surfaceY + 1, 10), true);
		spawnMooshrooms(level, surfaceY);

		WorldShift.LOGGER.info("Generated mushroom fields spawn at y={}", surfaceY);
	}

	private void carpetSpawnAreaWithMycelium(ServerLevel level, int radius) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if ((dx * dx) + (dz * dz) > radius * radius) {
					continue;
				}

				int x = dx;
				int z = dz;
				int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
				if (y <= level.getMinY() + 1) {
					continue;
				}

				cursor.set(x, y - 1, z);
				BlockState ground = level.getBlockState(cursor);
				if (ground.isAir() || ground.liquid()) {
					continue;
				}

				level.setBlock(cursor, Blocks.MYCELIUM.defaultBlockState(), 3);
				cursor.set(x, y, z);
				if (!level.getBlockState(cursor).isAir() && !level.getBlockState(cursor).canBeReplaced()) {
					continue;
				}
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
				cursor.set(x, y + 1, z);
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	private void placeMyceliumPlateau(ServerLevel level, int surfaceY, int radius) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if ((dx * dx) + (dz * dz) > radius * radius) {
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
				cursor.set(dx, surfaceY + 1, dz);
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
				cursor.set(dx, surfaceY + 2, dz);
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	private void clearSpawnPad(ServerLevel level, int surfaceY) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				cursor.set(dx, surfaceY, dz);
				level.setBlock(cursor, Blocks.MYCELIUM.defaultBlockState(), 3);
				cursor.set(dx, surfaceY + 1, dz);
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
				cursor.set(dx, surfaceY + 2, dz);
				level.setBlock(cursor, Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}

	private void scatterMushrooms(ServerLevel level, int surfaceY) {
		RandomSource random = level.getRandom();
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int i = 0; i < 48; i++) {
			int x = random.nextIntBetweenInclusive(-SPAWN_RADIUS, SPAWN_RADIUS);
			int z = random.nextIntBetweenInclusive(-SPAWN_RADIUS, SPAWN_RADIUS);
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			if (y <= level.getMinY() + 1) {
				y = surfaceY + 1;
			}

			cursor.set(x, y, z);
			if (!level.getBlockState(cursor).isAir()) {
				continue;
			}
			cursor.set(x, y - 1, z);
			if (!level.getBlockState(cursor).is(Blocks.MYCELIUM)) {
				continue;
			}

			cursor.set(x, y, z);
			level.setBlock(
					cursor,
					random.nextBoolean() ? Blocks.RED_MUSHROOM.defaultBlockState() : Blocks.BROWN_MUSHROOM.defaultBlockState(),
					3
			);
		}
	}

	private void placeGiantMushroom(ServerLevel level, BlockPos base, boolean red) {
		level.setBlock(base.below(), Blocks.MYCELIUM.defaultBlockState(), 3);
		for (int y = 0; y < 3; y++) {
			level.setBlock(base.above(y), Blocks.AIR.defaultBlockState(), 3);
		}

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

	private void spawnMooshrooms(ServerLevel level, int surfaceY) {
		RandomSource random = level.getRandom();
		for (int i = 0; i < MOOSHROOM_COUNT; i++) {
			int x = random.nextIntBetweenInclusive(-SPAWN_RADIUS + 4, SPAWN_RADIUS - 4);
			int z = random.nextIntBetweenInclusive(-SPAWN_RADIUS + 4, SPAWN_RADIUS - 4);
			int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			if (y <= level.getMinY() + 1) {
				y = surfaceY + 1;
			}

			BlockPos pos = new BlockPos(x, y, z);
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);

			MushroomCow mooshroom = EntityTypes.MOOSHROOM.spawn(level, pos, EntitySpawnReason.EVENT);
			if (mooshroom != null) {
				mooshroom.setPos(x + 0.5, y, z + 0.5);
			}
		}
	}

	private void forceChunks(ServerLevel level) {
		int chunkRadius = Mth.ceil(SPAWN_RADIUS / 16.0) + 1;
		for (int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
			for (int chunkZ = -chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
				level.getChunk(chunkX, chunkZ);
			}
		}
	}
}
