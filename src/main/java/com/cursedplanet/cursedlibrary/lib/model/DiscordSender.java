package com.cursedplanet.cursedlibrary.lib.model;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import com.cursedplanet.cursedlibrary.lib.Common;
import com.cursedplanet.cursedlibrary.lib.exception.FoException;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a Discord command sender for Discord integration
 */
@Getter
@RequiredArgsConstructor
public final class DiscordSender implements CommandSender {

	private final String name;
	private final User user;
	private final MessageChannel channel;
	private final Message message;

	@Override
	public boolean isPermissionSet(String permission) {
		throw unsupported("isPermissionSet");
	}

	@Override
	public boolean isPermissionSet(Permission permission) {
		throw unsupported("isPermissionSet");
	}

	@Override
	public boolean hasPermission(String perm) {
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		return false;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
		throw unsupported("addAttachment");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin) {
		throw unsupported("addAttachment");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
		throw unsupported("addAttachment");
	}

	@Override
	public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
		throw unsupported("addAttachment");
	}

	@Override
	public void removeAttachment(PermissionAttachment attachment) {
		throw unsupported("removeAttachment");
	}

	@Override
	public void recalculatePermissions() {
		throw unsupported("recalculatePermissions");
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		throw unsupported("getEffectivePermissions");
	}

	@Override
	public boolean isOp() {
		throw unsupported("isOp");
	}

	@Override
	public void setOp(boolean op) {
		throw unsupported("setOp");
	}

	@Override
	public void sendMessage(String[] messages) {
		for (final String message : messages)
			sendMessage(message);
	}

	@Override
	public void sendMessage(String message) {
		final String finalMessage = Common.stripColors(message);

		Common.runAsync(() -> {
			final Message sentMessage = channel.sendMessage(finalMessage).complete();

			// Automatically remove after a short while
			channel.deleteMessageById(sentMessage.getIdLong()).completeAfter(4, TimeUnit.SECONDS);
		});
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Server getServer() {
		return Bukkit.getServer();
	}

	@Override
	public Spigot spigot() {
		throw unsupported("spigot");
	}

	private FoException unsupported(String method) {
		return new FoException("DiscordSender cannot invoke " + method + "()");
	}

	/**
	 * @see org.bukkit.command.CommandSender#sendMessage(java.util.UUID, java.lang.String)
	 */
	//@Override - Disable to prevent errors in older MC
	@Override
	public void sendMessage(UUID uuid, String message) {
		this.sendMessage(message);
	}

	/**
	 * @see org.bukkit.command.CommandSender#sendMessage(java.util.UUID, java.lang.String[])
	 */
	//@Override - Disable to prevent errors in older MC
	@Override
	public void sendMessage(UUID uuid, String[] messages) {
		this.sendMessage(messages);
	}
}
