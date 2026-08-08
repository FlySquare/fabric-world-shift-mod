package com.flyzen.changeworldeverysecond.events;

import com.flyzen.changeworldeverysecond.world.WorldSelection;

import net.minecraft.server.MinecraftServer;

public record TransitionStartedEvent(MinecraftServer server, WorldSelection selection, int delaySeconds) {
}
