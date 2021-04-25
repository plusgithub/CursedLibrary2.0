package com.cursedplanet.cursedlibrary.lib.constants;

import com.cursedplanet.cursedlibrary.lib.command.annotation.Permission;
import com.cursedplanet.cursedplugins.plugin.SimplePlugin;

/**
 * Used to store basic library permissions
 */
public class FoPermissions {

	@Permission(value = "Receive plugin update notifications on join.")
	public static final String NOTIFY_UPDATE;

	static {
		NOTIFY_UPDATE = SimplePlugin.getNamed().toLowerCase() + ".notify.update";
	}
}