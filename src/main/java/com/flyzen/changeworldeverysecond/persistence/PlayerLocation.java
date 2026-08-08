package com.flyzen.changeworldeverysecond.persistence;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public record PlayerLocation(double x, double y, double z, float yaw, float pitch) {
	public static final Codec<PlayerLocation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.fieldOf("x").forGetter(PlayerLocation::x),
			Codec.DOUBLE.fieldOf("y").forGetter(PlayerLocation::y),
			Codec.DOUBLE.fieldOf("z").forGetter(PlayerLocation::z),
			Codec.FLOAT.fieldOf("yaw").forGetter(PlayerLocation::yaw),
			Codec.FLOAT.fieldOf("pitch").forGetter(PlayerLocation::pitch)
	).apply(instance, PlayerLocation::new));

	public static PlayerLocation from(ServerPlayer player) {
		Vec3 pos = player.position();
		return new PlayerLocation(pos.x, pos.y, pos.z, player.getYRot(), player.getXRot());
	}

	public Vec3 asVec3() {
		return new Vec3(x, y, z);
	}
}
