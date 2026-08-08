package com.flyzen.changeworldeverysecond.world;

import com.flyzen.changeworldeverysecond.util.IdentifierUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class DimensionKeys {
	public static final ResourceKey<Level> SKYBLOCK = ResourceKey.create(Registries.DIMENSION, IdentifierUtil.of("skyblock"));
	public static final ResourceKey<Level> DEEP_DARK = ResourceKey.create(Registries.DIMENSION, IdentifierUtil.of("deep_dark"));
	public static final ResourceKey<Level> MUSHROOM_ISLAND = ResourceKey.create(Registries.DIMENSION, IdentifierUtil.of("mushroom_island"));

	private DimensionKeys() {
	}
}
