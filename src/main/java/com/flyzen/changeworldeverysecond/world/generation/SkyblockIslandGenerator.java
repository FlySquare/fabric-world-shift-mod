package com.flyzen.changeworldeverysecond.world.generation;

import com.flyzen.changeworldeverysecond.WorldShift;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SkyblockIslandGenerator {
	public static final int CENTER_X = 0;
	public static final int CENTER_Z = 0;
	public static final int SURFACE_Y = 64;
	public static final int SPAWN_Y = SURFACE_Y + 1;
	public static final int RADIUS = 3;

	public void generate(ServerLevel level) {
		forceChunks(level);

		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int dx = -RADIUS; dx <= RADIUS; dx++) {
			for (int dz = -RADIUS; dz <= RADIUS; dz++) {
				if ((dx * dx) + (dz * dz) > (RADIUS * RADIUS) + 1) {
					continue;
				}

				set(level, cursor.set(CENTER_X + dx, SURFACE_Y - 2, CENTER_Z + dz), Blocks.BEDROCK.defaultBlockState());
				set(level, cursor.set(CENTER_X + dx, SURFACE_Y - 1, CENTER_Z + dz), Blocks.DIRT.defaultBlockState());
				set(level, cursor.set(CENTER_X + dx, SURFACE_Y, CENTER_Z + dz), Blocks.GRASS_BLOCK.defaultBlockState());
			}
		}

		placeOakTree(level, new BlockPos(CENTER_X - 2, SURFACE_Y + 1, CENTER_Z - 1));
		placeStarterChest(level, new BlockPos(CENTER_X + 1, SURFACE_Y + 1, CENTER_Z));

		WorldShift.LOGGER.info("Generated skyblock island at {}, {}, {}", CENTER_X, SURFACE_Y, CENTER_Z);
	}

	private void placeOakTree(ServerLevel level, BlockPos base) {
		BlockState log = Blocks.OAK_LOG.defaultBlockState();
		BlockState leaves = Blocks.OAK_LEAVES.defaultBlockState()
				.setValue(LeavesBlock.PERSISTENT, true)
				.setValue(LeavesBlock.DISTANCE, 1);

		for (int y = 0; y < 5; y++) {
			set(level, base.above(y), log);
		}

		for (int dy = 2; dy <= 5; dy++) {
			int radius = dy >= 5 ? 1 : 2;
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					if (Math.abs(dx) == radius && Math.abs(dz) == radius && dy < 5) {
						continue;
					}

					BlockPos leafPos = base.offset(dx, dy, dz);
					if (level.getBlockState(leafPos).isAir()) {
						set(level, leafPos, leaves);
					}
				}
			}
		}
	}

	private void placeStarterChest(ServerLevel level, BlockPos pos) {
		set(level, pos, Blocks.CHEST.defaultBlockState());
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof ChestBlockEntity chest)) {
			return;
		}

		chest.setItem(0, new ItemStack(Items.OAK_SAPLING, 4));
		chest.setItem(1, new ItemStack(Items.DIRT, 16));
		chest.setItem(2, new ItemStack(Items.BREAD, 8));
		chest.setItem(3, new ItemStack(Items.ICE, 2));
		chest.setItem(4, new ItemStack(Items.LAVA_BUCKET));
		chest.setItem(5, new ItemStack(Items.WATER_BUCKET));
		chest.setItem(6, new ItemStack(Items.BONE_MEAL, 16));
		chest.setItem(7, new ItemStack(Items.WHEAT_SEEDS, 8));
		chest.setChanged();
	}

	private void forceChunks(ServerLevel level) {
		for (int chunkX = -1; chunkX <= 1; chunkX++) {
			for (int chunkZ = -1; chunkZ <= 1; chunkZ++) {
				level.getChunk(chunkX, chunkZ);
			}
		}
	}

	private static void set(ServerLevel level, BlockPos pos, BlockState state) {
		level.setBlock(pos, state, 3);
	}
}
