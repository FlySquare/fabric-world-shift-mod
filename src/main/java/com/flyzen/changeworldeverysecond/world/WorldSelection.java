package com.flyzen.changeworldeverysecond.world;

import java.util.Objects;

public record WorldSelection(WorldType currentWorld, WorldType nextWorld) {
	public WorldSelection {
		Objects.requireNonNull(currentWorld, "currentWorld");
		Objects.requireNonNull(nextWorld, "nextWorld");
	}
}
