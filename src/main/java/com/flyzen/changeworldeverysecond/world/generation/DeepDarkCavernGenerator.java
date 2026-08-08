package com.flyzen.changeworldeverysecond.world.generation;

import com.flyzen.changeworldeverysecond.WorldShift;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class DeepDarkCavernGenerator {
	public static final int FLOOR_Y = -20;
	public static final int SPAWN_Y = FLOOR_Y + 1;
	public static final int RADIUS_XZ = 28;
	public static final int RADIUS_Y = 14;

	public void generate(ServerLevel level) {
		forceChunks(level);

		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
		BlockState bricks = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
		BlockState sculk = Blocks.SCULK.defaultBlockState();
		BlockState air = Blocks.AIR.defaultBlockState();

		for (int dx = -RADIUS_XZ - 2; dx <= RADIUS_XZ + 2; dx++) {
			for (int dz = -RADIUS_XZ - 2; dz <= RADIUS_XZ + 2; dz++) {
				for (int dy = -RADIUS_Y - 2; dy <= RADIUS_Y + 2; dy++) {
					double nx = dx / (double) RADIUS_XZ;
					double ny = dy / (double) RADIUS_Y;
					double nz = dz / (double) RADIUS_XZ;
					double dist = nx * nx + ny * ny + nz * nz;

					int x = dx;
					int y = FLOOR_Y + 6 + dy;
					int z = dz;
					cursor.set(x, y, z);

					if (dist <= 1.0) {
						level.setBlock(cursor, air, 2);
					} else if (dist <= 1.18) {
						level.setBlock(cursor, deepslate, 2);
					}
				}
			}
		}

		for (int dx = -RADIUS_XZ + 2; dx <= RADIUS_XZ - 2; dx++) {
			for (int dz = -RADIUS_XZ + 2; dz <= RADIUS_XZ - 2; dz++) {
				if ((dx * dx) + (dz * dz) > (RADIUS_XZ - 3) * (RADIUS_XZ - 3)) {
					continue;
				}

				cursor.set(dx, FLOOR_Y - 1, dz);
				level.setBlock(cursor, deepslate, 2);
				cursor.set(dx, FLOOR_Y, dz);
				level.setBlock(cursor, sculk, 2);
				cursor.set(dx, FLOOR_Y + 1, dz);
				level.setBlock(cursor, air, 2);
				cursor.set(dx, FLOOR_Y + 2, dz);
				level.setBlock(cursor, air, 2);
			}
		}

		placeRuinPillar(level, new BlockPos(-10, FLOOR_Y + 1, -8));
		placeRuinPillar(level, new BlockPos(12, FLOOR_Y + 1, 6));
		placeRuinPillar(level, new BlockPos(-6, FLOOR_Y + 1, 14));
		placeRuinWall(level, new BlockPos(0, FLOOR_Y + 1, -16));

		placeShrieker(level, new BlockPos(4, FLOOR_Y + 1, 3));
		placeShrieker(level, new BlockPos(-7, FLOOR_Y + 1, -2));
		placeShrieker(level, new BlockPos(9, FLOOR_Y + 1, -11));
		placeShrieker(level, new BlockPos(-12, FLOOR_Y + 1, 8));

		level.setBlock(new BlockPos(2, FLOOR_Y + 1, -4), Blocks.SCULK_SENSOR.defaultBlockState(), 3);
		level.setBlock(new BlockPos(-3, FLOOR_Y + 1, 5), Blocks.SCULK_SENSOR.defaultBlockState(), 3);
		level.setBlock(new BlockPos(0, FLOOR_Y + 1, 10), Blocks.SCULK_CATALYST.defaultBlockState(), 3);
		level.setBlock(new BlockPos(6, FLOOR_Y + 1, 1), Blocks.SCULK_CATALYST.defaultBlockState(), 3);

		for (int i = 0; i < 40; i++) {
			int x = level.getRandom().nextIntBetweenInclusive(-RADIUS_XZ + 4, RADIUS_XZ - 4);
			int z = level.getRandom().nextIntBetweenInclusive(-RADIUS_XZ + 4, RADIUS_XZ - 4);
			cursor.set(x, FLOOR_Y + 1, z);
			if (level.getBlockState(cursor).isAir()) {
				level.setBlock(cursor, Blocks.SCULK_VEIN.defaultBlockState(), 2);
			}
		}

		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				cursor.set(dx, FLOOR_Y, dz);
				level.setBlock(cursor, bricks, 3);
				cursor.set(dx, FLOOR_Y + 1, dz);
				level.setBlock(cursor, air, 3);
				cursor.set(dx, FLOOR_Y + 2, dz);
				level.setBlock(cursor, air, 3);
			}
		}

		placeAmbientLights(level);

		DeepDarkWardenDirector.spawnAggressiveWarden(level, new BlockPos(18, SPAWN_Y, 16));
		DeepDarkWardenDirector.spawnAggressiveWarden(level, new BlockPos(-16, SPAWN_Y, 12));
		DeepDarkWardenDirector.spawnAggressiveWarden(level, new BlockPos(10, SPAWN_Y, -18));
		WorldShift.LOGGER.info("Generated deep dark cavern around origin at y={}", FLOOR_Y);
	}

	public static void placeAmbientLights(ServerLevel level) {
		BlockPos[] lights = {
				new BlockPos(0, FLOOR_Y + 4, 0),
				new BlockPos(8, FLOOR_Y + 3, -6),
				new BlockPos(-9, FLOOR_Y + 3, 5),
				new BlockPos(14, FLOOR_Y + 3, 10),
				new BlockPos(-12, FLOOR_Y + 3, -10),
				new BlockPos(5, FLOOR_Y + 5, 12),
				new BlockPos(-4, FLOOR_Y + 5, -14)
		};
		for (BlockPos pos : lights) {
			if (level.getBlockState(pos).isAir()) {
				level.setBlock(pos, Blocks.SHROOMLIGHT.defaultBlockState(), 3);
			}
		}
	}

	private void placeRuinPillar(ServerLevel level, BlockPos base) {
		BlockState reinforced = Blocks.REINFORCED_DEEPSLATE.defaultBlockState();
		BlockState bricks = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
		for (int y = 0; y < 8; y++) {
			level.setBlock(base.above(y), y % 3 == 0 ? reinforced : bricks, 3);
		}
		level.setBlock(base.above(8), Blocks.CHISELED_DEEPSLATE.defaultBlockState(), 3);
	}

	private void placeRuinWall(ServerLevel level, BlockPos center) {
		BlockState bricks = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
		BlockState cracked = Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
		for (int dx = -4; dx <= 4; dx++) {
			for (int y = 0; y < 5; y++) {
				BlockState state = ((dx + y) % 2 == 0) ? bricks : cracked;
				level.setBlock(center.offset(dx, y, 0), state, 3);
			}
		}
	}

	private void placeShrieker(ServerLevel level, BlockPos pos) {
		BlockState shrieker = Blocks.SCULK_SHRIEKER.defaultBlockState()
				.setValue(SculkShriekerBlock.CAN_SUMMON, true);
		level.setBlock(pos, shrieker, 3);
	}

	private void forceChunks(ServerLevel level) {
		int chunkRadius = Mth.ceil(RADIUS_XZ / 16.0) + 1;
		for (int chunkX = -chunkRadius; chunkX <= chunkRadius; chunkX++) {
			for (int chunkZ = -chunkRadius; chunkZ <= chunkRadius; chunkZ++) {
				level.getChunk(chunkX, chunkZ);
			}
		}
	}
}
