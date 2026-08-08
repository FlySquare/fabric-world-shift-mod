package com.flyzen.changeworldeverysecond.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class CommandManager {
	public void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
				WorldShiftCommand.register(dispatcher, buildContext)
		);
	}
}
