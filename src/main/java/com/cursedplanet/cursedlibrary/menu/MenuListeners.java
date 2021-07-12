package com.cursedplanet.cursedlibrary.menu;

import com.cursedplanet.cursedlibrary.menu.slot.CursedSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;

import java.util.UUID;

public class MenuListeners implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() != null) {

			if (event.getInventory().getHolder() instanceof CursedHolder) {
				UUID id = ((CursedHolder) event.getInventory().getHolder()).getId();

				CursedGUI gui = MenuHandler.getInventory(id);

				if (gui.updateTask != null)
					gui.updateTask.accept(event);

				if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {

					CursedSlot slot = gui.contents.get(event.getSlot());
					slot.pass(event);

					event.setCancelled(true);

					event.setCancelled(!slot.isClickAllowed(event.getClick()));

					Common.log(String.valueOf(event.getAction()));
					event.setCancelled(!slot.isActionAllowed(event.getAction()));

				} else if (event.isShiftClick() && event.getCurrentItem() != null) { // This means the click IS in the player inventory
					// TODO: Setup shifting into non player inventories
					event.setCancelled(true);

					for (Integer slot : gui.contents.keySet()) {

						ItemStack invItem = gui.inv.getItem(slot);
						ItemStack clickedItem = event.getCurrentItem();

						if (slot >= gui.inv.firstEmpty()) {
							gui.contents.get(gui.inv.firstEmpty()).setItem(clickedItem);
							break;
						}

						if (invItem.getAmount() >= 64) {
							break;
						}

						int invItemAmount = invItem.getAmount();
						int clickedItemAmount = clickedItem.getAmount();

						invItem.setAmount(1);
						clickedItem.setAmount(1);
						if (invItem.isSimilar(clickedItem)) {
							gui.contents.get(slot).setItem(ItemCreator.of(invItem).amount(invItemAmount + clickedItemAmount).build().makeMenuTool());
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {

		Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getHolder() instanceof CursedHolder) {
			UUID id = ((CursedHolder) event.getInventory().getHolder()).getId();

			CursedGUI gui = MenuHandler.getInventory(id);

			for (int slot : event.getRawSlots()) {
				if (slot >= player.getOpenInventory().getTopInventory().getSize())
					continue;


				if (!gui.contents.get(slot).isActionAllowed(InventoryAction.PLACE_SOME)) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() instanceof CursedHolder) {
			UUID id = ((CursedHolder) event.getInventory().getHolder()).getId();

			CursedGUI gui = MenuHandler.getInventory(id);

			if (gui.consumer != null)
				gui.consumer.accept(event);

			if (gui.closeSound != null)
				((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), gui.closeSound, 1, 1);

			((Player) event.getPlayer()).updateInventory();
		}
	}
}
