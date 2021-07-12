package com.cursedplanet.cursedlibrary.glowing.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractPacket {
	// The packet we will be modifying
	protected PacketContainer handle;

	/***
	 * Constructs a new strongly typed wrapper for the given packet.
	 *
	 * @param handle - handle to the raw packet data.
	 * @param type   - the packet type.
	 */
	protected AbstractPacket(@Nullable PacketContainer handle, @NotNull PacketType type) {
		// Make sure we're given a valid packet
		if (handle == null)
			throw new IllegalArgumentException("Packet handle cannot be NULL.");
		if (!Objects.equal(handle.getType(), type))
			throw new IllegalArgumentException(handle.getHandle() + " is not a packet of type " + type);

		this.handle = handle;
	}

	/***
	 * Retrieve a handle to the raw packet data.
	 *
	 * @return Raw packet data.
	 */
	@NotNull
	public PacketContainer getHandle() {
		return handle;
	}

	/***
	 * Send the current packet to the given receiver.
	 *
	 * @param player - the receiver.
	 * @throws RuntimeException If the packet cannot be sent.
	 */
	public void sendPacket(@NotNull Player player) {
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, getHandle());
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Cannot send packet.", e);
		}
	}

	/***
	 * Send the current packet to all online players.
	 */
	public void broadcastPacket() {
		ProtocolLibrary.getProtocolManager().broadcastServerPacket(getHandle());
	}

	/***
	 * Simulate receiving the current packet from the given sender.
	 *
	 * @param player - the sender.
	 * @throws RuntimeException if the packet cannot be received.
	 */
	public void receivePacket(@NotNull Player player) {
		try {
			ProtocolLibrary.getProtocolManager().recieveClientPacket(player, getHandle());
		} catch (Exception e) {
			throw new RuntimeException("Cannot receive packet.", e);
		}
	}
}
