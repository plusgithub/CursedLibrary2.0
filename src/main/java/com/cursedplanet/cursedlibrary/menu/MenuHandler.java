package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class MenuHandler {

	private static final HashMap<UUID, CursedGUI> inventories = new HashMap<>();

	public static void removeInventory(CursedGUI gui) {
		inventories.remove(gui.getId());
	}

	public static CursedGUI getInventory(UUID uuid) {
		return inventories.get(uuid);
	}

	public static boolean isViewing(Player player) {
		return player.getInventory().getHolder() instanceof CursedHolder;
	}

	static void addInventory(CursedGUI gui) {
		inventories.put(gui.getId(), gui);
	}
}
