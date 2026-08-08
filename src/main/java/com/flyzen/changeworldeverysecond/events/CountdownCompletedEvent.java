package com.flyzen.changeworldeverysecond.events;

import net.minecraft.server.MinecraftServer;

public record CountdownCompletedEvent(MinecraftServer server) {
}
