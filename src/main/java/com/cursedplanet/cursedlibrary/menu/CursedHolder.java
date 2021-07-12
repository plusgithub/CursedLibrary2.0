package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CursedHolder implements InventoryHolder {

	private final UUID id;

	public CursedHolder() {
		this.id = UUID.randomUUID();
	}

	@NotNull
	public UUID getId() {
		return this.id;
	}

	@NotNull
	@Override
	public Inventory getInventory() {
		return null;
	}
}
