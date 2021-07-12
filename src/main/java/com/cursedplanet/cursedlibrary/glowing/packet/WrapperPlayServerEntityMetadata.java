package com.cursedplanet.cursedlibrary.glowing.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WrapperPlayServerEntityMetadata extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

	public WrapperPlayServerEntityMetadata() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerEntityMetadata(@NotNull PacketContainer packet) {
		super(packet, TYPE);
	}

	/***
	 * Retrieve Entity ID.
	 * <p>
	 * Notes: entity's ID
	 *
	 * @return The current Entity ID
	 */
	public int getEntityID() {
		return handle.getIntegers().read(0);
	}

	/***
	 * Set Entity ID.
	 *
	 * @param value - new value.
	 */
	public void setEntityID(int value) {
		handle.getIntegers().write(0, value);
	}

	/***
	 * Retrieve the entity of the painting that will be spawned.
	 *
	 * @param world - the current world of the entity.
	 * @return The spawned entity.
	 */
	@NotNull
	public Entity getEntity(@NotNull World world) {
		return handle.getEntityModifier(world).read(0);
	}

	/***
	 * Retrieve the entity of the painting that will be spawned.
	 *
	 * @param event - the packet event.
	 * @return The spawned entity.
	 */
	@NotNull
	public Entity getEntity(@NotNull PacketEvent event) {
		return getEntity(event.getPlayer().getWorld());
	}

	/***
	 * Retrieve Metadata.
	 *
	 * @return The current Metadata
	 */
	@Nullable
	public List<WrappedWatchableObject> getMetadata() {
		return handle.getWatchableCollectionModifier().read(0);
	}

	/***
	 * Set Metadata.
	 *
	 * @param value - new value.
	 */
	public void setMetadata(@NotNull List<WrappedWatchableObject> value) {
		handle.getWatchableCollectionModifier().write(0, value);
	}
}
