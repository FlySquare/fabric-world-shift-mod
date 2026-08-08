package com.flyzen.changeworldeverysecond.world;

import java.util.Locale;
import java.util.Optional;

import com.flyzen.changeworldeverysecond.config.ModLanguage;

import net.minecraft.util.StringRepresentable;

public enum WorldType implements StringRepresentable {
	OVERWORLD("world.overworld"),
	NETHER("world.nether"),
	END("world.end"),
	SKYBLOCK("world.skyblock"),
	DEEP_DARK("world.deep_dark"),
	MUSHROOM_ISLAND("world.mushroom_island");

	public static final StringRepresentable.EnumCodec<WorldType> CODEC = StringRepresentable.fromEnum(WorldType::values);

	private final String translationKey;

	WorldType(String translationKey) {
		this.translationKey = translationKey;
	}

	public String displayName() {
		return ModLanguage.current().translate(translationKey);
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}

	public static Optional<WorldType> byName(String name) {
		if (name == null || name.isBlank()) {
			return Optional.empty();
		}

		String normalized = name.trim();
		try {
			return Optional.of(WorldType.valueOf(normalized.toUpperCase(Locale.ROOT)));
		} catch (IllegalArgumentException ignored) {
			for (WorldType worldType : values()) {
				String en = ModLanguage.EN.translate(worldType.translationKey);
				String tr = ModLanguage.TR.translate(worldType.translationKey);
				if (en.equalsIgnoreCase(normalized)
						|| tr.equalsIgnoreCase(normalized)
						|| worldType.getSerializedName().equalsIgnoreCase(normalized)) {
					return Optional.of(worldType);
				}
			}
			return Optional.empty();
		}
	}
}
