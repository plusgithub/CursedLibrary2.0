package com.cursedplanet.cursedlibrary.lib.bungee;

import com.cursedplanet.cursedlibrary.lib.bungee.message.IncomingMessage;
import com.cursedplanet.cursedlibrary.lib.debug.Debugger;
import com.cursedplanet.cursedplugins.plugin.SimplePlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * Represents a BungeeCord listener using a {@link BungeeChannel} channel
 * on which you can listen to receiving messages
 * <p>
 * This class is also a Listener for Bukkit events for your convenience
 */
public abstract class BungeeListener implements Listener, PluginMessageListener {

	/**
	 * Create a new bungee listener
	 *
	 * @param channel
	 */
	protected BungeeListener() {
	}

	/**
	 * Handle the received message automatically if it matches our tag
	 */
	@Override
	public final void onPluginMessageReceived(String tag, Player player, byte[] data) {

		// Cauldron/Thermos is unsupported for bungee
		if (Bukkit.getName().contains("Cauldron"))
			return;

		if (tag.equals(SimplePlugin.getInstance().getBungeeCord().getChannel())) {
			final IncomingMessage message = new IncomingMessage(data);

			Debugger.debug("bungee", "Channel " + message.getChannel() + " received " + message.getAction() + " message from " + message.getServerName() + " server.");
			onMessageReceived(player, message);
		}
	}

	/**
	 * Called automatically when you receive a plugin message from Bungeecord,
	 * see https://spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel
	 *
	 * @param player
	 * @param message
	 */
	public abstract void onMessageReceived(Player player, IncomingMessage message);
}
