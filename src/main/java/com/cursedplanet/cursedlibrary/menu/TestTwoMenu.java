package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class TestTwoMenu extends CursedGUI {


	public TestTwoMenu() {
		super(54, "Cool menu");

		reset();

		/**addStatic(5, new ItemStack(Material.DIAMOND)).lock();

		 addClickable(6, new ItemStack(Material.IRON_BARS), () -> {
		 addStatic(6, new ItemStack(Material.DIAMOND)).lock();
		 }).lock();


		 lockFilled(fillRectangle(new ItemStack(Material.DIAMOND, 5), 10, 33));

		 repeatingTask(20, () -> {
		 Common.tell(getViewers().get(0), "hello");
		 });

		 addCloseCallback(e -> {
		 Common.tell(e.getPlayer(), "You closed the menu");
		 });

		 setOpenSound(Sound.BLOCK_IRON_DOOR_OPEN);
		 setCloseSound(Sound.BLOCK_IRON_TRAPDOOR_CLOSE);

		 setPattern("#&#&#&#&#", "&#&#&#&#&");
		 assignPatternItem('#', new ItemStack(Material.IRON_BLOCK));
		 assignPatternItem('&', new ItemStack(Material.GOLD_BLOCK));
		 //compilePattern();

		 lockFilled(fillPattern());

		 MenuItem[] items = compilePattern();
		 for (int i = 0; i < items.length; i++) {

		 int slot = i;

		 items[i] = addClickable(slot, items[i].getItem(), () -> {
		 Common.tell(getViewers().get(0), "this is slot " + slot);
		 });

		 items[i].lock();
		 }*/

		//fillStatic(new ItemStack(Material.AIR));

		setPattern("?&?&?&?&?", "&0000000&", "?0000000?", "&0000000&", "?0000000?", "&?&?&?&?&");
		assignPatternItem('?', new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
		assignPatternItem('&', new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
		fillPattern();

		setPagePattern("&#######&", "&&#####&&", "&&&###&&&", "&&&&#&&&&", "&&&&&&&&&");

		ItemStack[] items = new ItemStack[Material.values().length];


		for (int i = 0; i < Material.values().length; i++) {
			int finalI = i;
			if ((Material.values()[finalI].isBlock() || Material.values()[finalI].isItem()) && Material.values()[finalI] != Material.AIR) {
				addPageItem(event -> {
					Common.tell(event.getWhoClicked(), "&aYou clicked " + Material.values()[finalI].toString());
				}, new ItemStack(Material.values()[i]));
			}
		}

		addNextPageButton(ItemCreator.of(CompMaterial.ARROW, "&aNext page").build().makeSurvival(), ItemCreator.of(CompMaterial.GRASS, "&cNext page").build().makeSurvival(), 51);

		addPreviousPageButton(ItemCreator.of(CompMaterial.ARROW, "&aLast page").build().makeSurvival(), ItemCreator.of(CompMaterial.GRASS, "&cLast page").build().makeSurvival(), 47);

		fillPages(slot -> {
			slot.allowAllClicks();
			slot.allow(InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE);
		});

	}
}
