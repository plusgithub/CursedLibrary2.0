package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

public class TempCraftingMenu extends CursedGUI {

	public TempCraftingMenu() {
		super(45, "&8Crafting Menu");

		fillStatic(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), 10, 11, 12, 19, 20, 21, 28, 29, 30, 24);
		//if (getItemAt(10).getType() == Material.DIAMOND && getItemAt(11).getType() == Material.DIAMOND && getItemAt(12).getType() == Material.DIAMOND)

		addClickable(new ItemStack(Material.DIAMOND_AXE), 24, (e1) -> {
			Common.tell(e1.getWhoClicked(), "&aYou took the item");
			addStatic(new ItemStack(Material.AIR), 24).allow(InventoryAction.PLACE_ALL);
		}).allow(InventoryAction.PICKUP_ONE);
	}
}
