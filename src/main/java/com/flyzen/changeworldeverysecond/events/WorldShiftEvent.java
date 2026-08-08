package com.flyzen.changeworldeverysecond.events;

import com.flyzen.changeworldeverysecond.world.WorldSelection;

import net.minecraft.server.MinecraftServer;

public record WorldShiftEvent(MinecraftServer server, WorldSelection selection) {
}
