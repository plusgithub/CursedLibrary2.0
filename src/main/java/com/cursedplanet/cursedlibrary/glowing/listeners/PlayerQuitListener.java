package com.cursedplanet.cursedlibrary.glowing.listeners;

import com.cursedplanet.cursedlibrary.glowing.GlowAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class PlayerQuitListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
		Player quittingPlayer = event.getPlayer();
		GlowAPI.setGlowing(quittingPlayer, null, Bukkit.getOnlinePlayers()
				.parallelStream()
				.filter(player -> GlowAPI.isGlowing(quittingPlayer, player))
				.collect(Collectors.<Player>toList()));
	}

}