package com.flyzen.changeworldeverysecond.teleport;

import com.flyzen.changeworldeverysecond.world.WorldType;
import com.flyzen.changeworldeverysecond.world.generation.DeepDarkCavernGenerator;
import com.flyzen.changeworldeverysecond.world.generation.SkyblockIslandGenerator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public final class SpawnLocator {
	public Vec3 findSpawn(ServerLevel level, WorldType worldType) {
		return switch (worldType) {
			case END -> Vec3.atBottomCenterOf(ServerLevel.END_SPAWN_POINT);
			case SKYBLOCK -> new Vec3(
					SkyblockIslandGenerator.CENTER_X + 0.5,
					SkyblockIslandGenerator.SPAWN_Y,
					SkyblockIslandGenerator.CENTER_Z + 0.5
			);
			case DEEP_DARK -> new Vec3(0.5, DeepDarkCavernGenerator.SPAWN_Y, 0.5);
			case NETHER -> findNetherSpawn(level);
			case OVERWORLD, MUSHROOM_ISLAND -> findSurfaceSpawn(level, 0, 0);
		};
	}

	public boolean isSafeLocation(ServerLevel level, Vec3 position) {
		if (position.y < level.getMinY() + 1 || position.y > level.getMaxY()) {
			return false;
		}

		BlockPos feet = BlockPos.containing(position.x, position.y, position.z);
		if (isSafeStand(level, feet)) {
			return true;
		}

		for (int dy = 1; dy <= 3; dy++) {
			BlockPos candidate = feet.below(dy);
			if (candidate.getY() < level.getMinY()) {
				break;
			}
			if (isSafeStand(level, candidate.above())) {
				return true;
			}
		}

		return false;
	}

	private Vec3 findSurfaceSpawn(ServerLevel level, int x, int z) {
		level.getChunk(x >> 4, z >> 4);
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		if (y <= level.getMinY()) {
			y = Math.max(level.getMinY() + 1, 64);
		}

		BlockPos feet = new BlockPos(x, y, z);
		if (!isSafeStand(level, feet)) {
			feet = findNearbySafe(level, feet, 16);
		}

		return Vec3.atBottomCenterOf(feet);
	}

	private Vec3 findNetherSpawn(ServerLevel level) {
		level.getChunk(0, 0);
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int y = 32; y < 120; y++) {
			cursor.set(0, y, 0);
			if (isSafeStand(level, cursor)) {
				return Vec3.atBottomCenterOf(cursor.immutable());
			}
		}

		BlockPos platform = new BlockPos(0, 64, 0);
		ensurePlatform(level, platform);
		return Vec3.atBottomCenterOf(platform.above());
	}

	private BlockPos findNearbySafe(ServerLevel level, BlockPos origin, int radius) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int r = 0; r <= radius; r++) {
			for (int dx = -r; dx <= r; dx++) {
				for (int dz = -r; dz <= r; dz++) {
					int x = origin.getX() + dx;
					int z = origin.getZ() + dz;
					int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
					cursor.set(x, y, z);
					if (isSafeStand(level, cursor)) {
						return cursor.immutable();
					}
				}
			}
		}

		return origin;
	}

	private boolean isSafeStand(ServerLevel level, BlockPos feet) {
		BlockState ground = level.getBlockState(feet.below());
		BlockState body = level.getBlockState(feet);
		BlockState head = level.getBlockState(feet.above());

		if (!ground.blocksMotion() || isLava(ground) || isLava(body) || isLava(head)) {
			return false;
		}

		return body.getCollisionShape(level, feet).isEmpty()
				&& head.getCollisionShape(level, feet.above()).isEmpty();
	}

	private static boolean isLava(BlockState state) {
		var fluid = state.getFluidState().getType();
		return fluid == Fluids.LAVA || fluid == Fluids.FLOWING_LAVA;
	}

	private void ensurePlatform(ServerLevel level, BlockPos center) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				cursor.set(center.getX() + dx, center.getY(), center.getZ() + dz);
				level.setBlock(cursor, net.minecraft.world.level.block.Blocks.NETHERRACK.defaultBlockState(), 3);
				level.setBlock(cursor.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
				level.setBlock(cursor.above(2), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}
}
