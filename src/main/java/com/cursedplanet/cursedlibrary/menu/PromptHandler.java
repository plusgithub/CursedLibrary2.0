package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.Common;

import java.util.HashMap;
import java.util.Map;

public class PromptHandler implements Listener {

	public static Map<Player, MenuPrompt> prompts = new HashMap<>();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		boolean inPrompt = prompts.containsKey(player);

		if (inPrompt) {
			event.setCancelled(true);
			Common.runLater(0, () -> { // Use this to run Sync in async method
				prompts.get(player).pass(event.getMessage());
			});
		}

		//event.getRecipients().removeAll(engagedPlayers.keySet());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		boolean inPrompt = prompts.containsKey(player);

		if (inPrompt) {
			prompts.remove(player);
		}
	}
}
