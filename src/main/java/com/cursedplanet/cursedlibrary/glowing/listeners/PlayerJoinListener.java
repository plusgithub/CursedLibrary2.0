package com.cursedplanet.cursedlibrary.glowing.listeners;

import com.cursedplanet.cursedlibrary.glowing.GlowAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerJoinListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
		GlowAPI.initTeams(event.getPlayer());
	}

}
