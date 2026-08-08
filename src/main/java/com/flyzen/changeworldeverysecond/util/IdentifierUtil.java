package com.flyzen.changeworldeverysecond.util;

import net.minecraft.resources.Identifier;

public final class IdentifierUtil {
	private IdentifierUtil() {
	}

	public static Identifier of(String path) {
		return Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, path);
	}
}
