package com.cursedplanet.cursedlibrary.glowing.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {
	public static final PacketType TYPE = PacketType.Play.Server.SCOREBOARD_TEAM;

	public static final Class<?> EnumChatFormat = MinecraftReflection.getMinecraftClass("EnumChatFormat");

	private static final byte ALLOW_FRIENDLY_FIRE = 0x01;
	private static final byte CAN_SEE_FRIENDLY_INVISIBLES = 0x02;

	/**
	 * Enumeration of all the known packet modes.
	 *
	 * @author Kristian
	 */
	public enum Modes {
		TEAM_CREATED(0),
		TEAM_REMOVED(1),
		TEAM_UPDATED(2),
		PLAYERS_ADDED(3),
		PLAYERS_REMOVED(4);

		final int packetMode;

		@Nullable
		public static Modes valueOf(int packetMode) {
			return Arrays.stream(Modes.values())
					.filter(modes -> modes.packetMode == packetMode)
					.findAny()
					.orElse(null);
		}

		Modes(int packetMode) {
			this.packetMode = packetMode;
		}
	}

	public enum TeamPush {
		ALWAYS("always"),
		PUSH_OTHER_TEAMS("pushOtherTeams"),
		PUSH_OWN_TEAM("pushOwnTeam"),
		NEVER("never");

		final String collisionRule;

		TeamPush(@NotNull String collisionRule) {
			this.collisionRule = collisionRule;
		}
	}

	public enum NameTagVisibility {
		ALWAYS("always"),
		HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
		HIDE_FOR_OWN_TEAM("hideForOwnTeam"),
		NEVER("never");

		final String nameTagVisibility;

		NameTagVisibility(@NotNull String nameTagVisibility) {
			this.nameTagVisibility = nameTagVisibility;
		}
	}

	public WrapperPlayServerScoreboardTeam() {
		super(new PacketContainer(TYPE), TYPE);
		handle.getModifier().writeDefaults();
	}

	public WrapperPlayServerScoreboardTeam(@NotNull PacketContainer packet) {
		super(packet, TYPE);
	}

	/**
	 * Retrieve an unique name for the team. (Shared with scoreboard)..
	 *
	 * @return The current Team Name
	 */
	@NotNull
	public String getName() {
		return handle.getStrings().read(0);
	}

	/**
	 * Set an unique name for the team. (Shared with scoreboard)..
	 *
	 * @param value - new value.
	 */
	public void setName(@NotNull String value) {
		handle.getStrings().write(0, value);
	}

	/**
	 * Retrieve the current packet {@link Modes}.
	 * <p>
	 * This determines whether or not team information is added or removed.
	 *
	 * @return The current packet mode.
	 */
	@Nullable
	public Modes getPacketMode() {
		return Modes.valueOf(handle.getIntegers().read(0));
	}

	/**
	 * Set the current packet {@link Modes}.
	 * <p>
	 * This determines whether or not team information is added or removed.
	 *
	 * @param value - new value.
	 */
	public void setPacketMode(@NotNull Modes value) {
		handle.getIntegers().write(0, value.packetMode);
	}

	/**
	 * Retrieve the team display name.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current display name.
	 */
	@NotNull
	public String getTeamDisplayName() {
		return handle.getChatComponents().read(0).toString();
	}

	/**
	 * Set the team display name.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setTeamDisplayName(@NotNull String value) {
		handle.getChatComponents().write(0, WrappedChatComponent.fromText(value));
	}

	/**
	 * Retrieve the team prefix. This will be inserted before the name of each team member.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current Team Prefix
	 */
	@NotNull
	public String getTeamPrefix() {
		return handle.getChatComponents().read(1).toString();
	}

	/**
	 * Set the team prefix. This will be inserted before the name of each team member.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setTeamPrefix(@NotNull String value) {
		handle.getChatComponents().write(1, WrappedChatComponent.fromText(value));
	}

	/**
	 * Retrieve the team suffix. This will be inserted after the name of each team member.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current Team Suffix
	 */
	@NotNull
	public String getTeamSuffix() {
		return handle.getChatComponents().read(2).toString();
	}

	/**
	 * Set the team suffix. This will be inserted after the name of each team member.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setTeamSuffix(@NotNull String value) {
		final WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromText(value);
		handle.getChatComponents().write(2, wrappedChatComponent);
	}

	/**
	 * Retrieve whether or not friendly fire is enabled.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current Friendly fire
	 */
	public boolean getAllowFriendlyFire() {
		final byte packOptionData = handle.getIntegers().read(1).byteValue();
		final int allowFriendlyFire = packOptionData & ALLOW_FRIENDLY_FIRE;
		return allowFriendlyFire != 0;
	}

	/**
	 * Set whether or not friendly fire is enabled.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setAllowFriendlyFire(boolean value) {
		int packOptionData = handle.getIntegers().read(1);
		if (value) packOptionData = packOptionData | ALLOW_FRIENDLY_FIRE;
		else packOptionData = packOptionData & ~ALLOW_FRIENDLY_FIRE;
		handle.getIntegers().write(1, packOptionData);
	}

	/**
	 * Retrieve whether or not friendly invisibles can be seen.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current Friendly fire
	 */
	public boolean getCanSeeFriendlyInvisibles() {
		final byte packOptionData = handle.getIntegers().read(1).byteValue();
		final int canSeeFriendlyInvisibles = packOptionData & CAN_SEE_FRIENDLY_INVISIBLES;
		return canSeeFriendlyInvisibles != 0;
	}

	/**
	 * Set whether or not friendly invisibles can be seen.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setCanSeeFriendlyInvisibles(boolean value) {
		int packOptionData = handle.getIntegers().read(1);
		if (value) packOptionData = packOptionData | CAN_SEE_FRIENDLY_INVISIBLES;
		else packOptionData = packOptionData & ~CAN_SEE_FRIENDLY_INVISIBLES;
		handle.getIntegers().write(1, packOptionData);
	}

	/**
	 * Retrieve the list of entries.
	 * <p>
	 * Packet mode must be one of the following for this to be valid:
	 * <ul>
	 *  <li>{@link Modes#TEAM_CREATED}</li>
	 *  <li>{@link Modes#PLAYERS_ADDED}</li>
	 *  <li>{@link Modes#PLAYERS_REMOVED}</li>
	 * </ul>
	 *
	 * @return A list of entries.
	 */
	@NotNull
	public Collection<String> getEntries() {
		return handle.getSpecificModifier(Collection.class).read(0);
	}

	/**
	 * Set the list of entries.
	 * <p>
	 * Packet mode must be one of the following for this to be valid:
	 * <ul>
	 *  <li>{@link Modes#TEAM_CREATED}</li>
	 *  <li>{@link Modes#PLAYERS_ADDED}</li>
	 *  <li>{@link Modes#PLAYERS_REMOVED}</li>
	 * </ul>
	 *
	 * @param entries - A list of entries.
	 */
	public void setEntries(@NotNull Collection<String> entries) {
		handle.getSpecificModifier(Collection.class).write(0, entries);
	}

	/**
	 * Retrieve the color of a team
	 * <p>
	 * A team must be created or updated.
	 *
	 * @return The current color
	 */
	@NotNull
	public ChatColor getTeamColor() {
		return handle.getEnumModifier(ChatColor.class, EnumChatFormat).read(0);
	}

	/**
	 * Sets the color of a team.
	 * <p>
	 * A team must be created or updated.
	 *
	 * @param value - new value.
	 */
	public void setTeamColor(@NotNull ChatColor value) {
		handle.getEnumModifier(ChatColor.class, EnumChatFormat).write(0, value);
	}

	@NotNull
	public TeamPush getTeamPush() {
		return TeamPush.valueOf(handle.getStrings().read(1));
	}

	public void setTeamPush(@NotNull TeamPush value) {
		handle.getStrings().write(1, value.toString());
	}

	@NotNull
	public NameTagVisibility getNameTagVisibility() {
		return NameTagVisibility.valueOf(handle.getStrings().read(2));
	}

	public void setNameTagVisibility(@NotNull NameTagVisibility value) {
		handle.getStrings().write(2, value.toString());
	}
}