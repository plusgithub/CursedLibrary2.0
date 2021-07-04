package com.cursedplanet.cursedlibrary.collection.command;

import com.Zrips.CMI.CMI;
import com.cursedplanet.cursedlibrary.collection.CollectionLoader;
import com.cursedplanet.cursedlibrary.collection.CollectionStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.command.SimpleSubCommand;

public class CollectionsAdd extends SimpleSubCommand {

	protected CollectionsAdd(SimpleCommandGroup parent) {
		super(parent, "add|give");

		setUsage("[player] [cmi item id]");
		setPermission("cursedlibrary.collections.add");
		setDescription("Adds the item in your hand to selected players collection");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		Player player = getPlayer();

		if (args.length >= 1) {
			checkNotNull(player, args[0] + "&c could not be found");
			player = Bukkit.getServer().getPlayerExact(args[0]);
		}

		ItemStack item = getPlayer().getInventory().getItemInMainHand();
		if (args.length >= 2) {
			try {
				item = CMI.getInstance().getItemManager().getItem(args[1]).getItemStack();
			} catch (Exception e) {
				Common.tell(player, "&cItem could not be found");
			}
		}

		CollectionStorage.addPlayerItem(player, item);
		tell(((String) CollectionLoader.getConfigPart("add_command")).replaceAll("\\{player}", player.getName()));
	}
}
