package com.cursedplanet.cursedlibrary;

import com.comphenix.protocol.ProtocolLibrary;
import com.cursedplanet.cursedlibrary.collection.CollectionAPI;
import com.cursedplanet.cursedlibrary.collection.CollectionCommand;
import com.cursedplanet.cursedlibrary.collection.CollectionLoader;
import com.cursedplanet.cursedlibrary.collection.command.CollectionsParent;
import com.cursedplanet.cursedlibrary.glowing.GlowAPI;
import com.cursedplanet.cursedlibrary.glowing.listeners.PlayerJoinListener;
import com.cursedplanet.cursedlibrary.glowing.listeners.PlayerQuitListener;
import com.cursedplanet.cursedlibrary.menu.MenuListeners;
import com.cursedplanet.cursedlibrary.menu.MenuPrompt;
import com.cursedplanet.cursedlibrary.pluginchecker.JoinChecker;
import fr.minuskube.inv.InventoryManager;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.plugin.SimplePlugin;

public class LibraryPlugin extends SimplePlugin {

	public static InventoryManager manager;

	@Override
	protected void onPluginStart() {

		//Common.ADD_TELL_PREFIX = true;
		Common.setTellPrefix(CollectionLoader.getTellPrefix());

		this.saveResource("items.yml", false);
		this.saveResource("config.yml", false);

		registerCommand(new CollectionCommand());
		registerCommands("collections|collection", new CollectionsParent());
		registerEvents(new JoinChecker());
		//registerEvents(new MenuListeners());

		//Collections
		CollectionLoader.loadCollectionItems();
		CollectionLoader.loadCollectionConfig();

		HookManager.addPlaceholder("uncollected_items", (player) -> {
			return String.valueOf(CollectionAPI.getPlayerItems(player).size());
		});

		manager = new InventoryManager(LibraryPlugin.getInstance());
		manager.init();

		//registerCommand(new TempMenuTest());
		registerEvents(new MenuListeners());
		registerEvents(new MenuPrompt());

		//GlowingAPI

		GlowAPI.protocolManager = ProtocolLibrary.getProtocolManager();
		GlowAPI.asynchronousManager = GlowAPI.protocolManager.getAsynchronousManager();

		GlowAPI.entityMetadataListenerHandler = GlowAPI.asynchronousManager.registerAsyncHandler(GlowAPI.entityMetadataListener);
		GlowAPI.entityMetadataListenerHandler.syncStart();

		registerEvents(new PlayerJoinListener());
		registerEvents(new PlayerQuitListener());
		registerCommand(new Temp());

	}

	@Override
	protected void onPluginStop() {

		//Collections
		CollectionLoader.saveCollectionItems();

		GlowAPI.asynchronousManager.unregisterAsyncHandler(GlowAPI.entityMetadataListenerHandler);
		GlowAPI.entityMetadataListenerHandler.stop();
	}
}
