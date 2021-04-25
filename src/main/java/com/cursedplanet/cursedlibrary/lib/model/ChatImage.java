package com.cursedplanet.cursedlibrary.lib.model;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import com.cursedplanet.cursedlibrary.lib.MinecraftVersion;
import com.cursedplanet.cursedlibrary.lib.MinecraftVersion.V;
import com.cursedplanet.cursedlibrary.lib.Valid;
import com.cursedplanet.cursedlibrary.lib.remain.CompChatColor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Represents a way to show an image in chat
 *
 * @author bobacadodl and kangarko
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatImage {

	/**
	 * Represents empty char
	 */
	private final static char TRANSPARENT_CHAR = ' ';

	/**
	 * Represents colors we can use for MC before 1.16
	 */
	private final static Color[] LEGACY_COLORS = {
			new Color(0, 0, 0),
			new Color(0, 0, 170),
			new Color(0, 170, 0),
			new Color(0, 170, 170),
			new Color(170, 0, 0),
			new Color(170, 0, 170),
			new Color(255, 170, 0),
			new Color(170, 170, 170),
			new Color(85, 85, 85),
			new Color(85, 85, 255),
			new Color(85, 255, 85),
			new Color(85, 255, 255),
			new Color(255, 85, 85),
			new Color(255, 85, 255),
			new Color(255, 255, 85),
			new Color(255, 255, 255),
	};

	/**
	 * Represents the currently loaded lines
	 */
	@Getter
	private String[] lines;

	/**
	 * Appends the given text next to the image
	 *
	 * @param text
	 * @return
	 */
	public ChatImage appendText(String... text) {
		for (int y = 0; y < lines.length; y++)
			if (text.length > y) {
				final String line = text[y];

				lines[y] += " " + line;
			}

		return this;
	}

	/**
	 * Appends the given text as centered, next to the image
	 *
	 * @param text
	 * @return
	 */
	public ChatImage appendCenteredText(String... text) {
		for (int y = 0; y < lines.length; y++)
			if (text.length > y) {
				final int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines[y].length();

				lines[y] = lines[y] + center(text[y], len);

			} else
				return this;

		return this;
	}

	/*
	 * Centers the given message according to the given length
	 */
	private String center(String message, int length) {
		if (message.length() > length)
			return message.substring(0, length);

		else if (message.length() == length)
			return message;

		else {
			final int leftPadding = (length - message.length()) / 2;
			final StringBuilder leftBuilder = new StringBuilder();

			for (int i = 0; i < leftPadding; i++)
				leftBuilder.append(" ");

			return leftBuilder.toString() + message;
		}
	}

	/**
	 * Sends this image to the given player
	 *
	 * @param sender
	 */
	public void sendToPlayer(CommandSender sender) {
		for (final String line : lines)
			sender.sendMessage(Variables.replace(line, sender));
	}

	/* ------------------------------------------------------------------------------- */
	/* Static */
	/* ------------------------------------------------------------------------------- */

	/**
	 * Create an image to show in a chat message from the given path
	 * in your plugin's JAR, with the given height and the given character type.
	 *
	 * @param file
	 * @param height
	 * @param characterType
	 * @return
	 */
	public static ChatImage fromFile(@NonNull File file, int height, Type characterType) throws IOException {
		Valid.checkBoolean(file.exists(), "Cannot load image from non existing file " + file.toPath());

		final BufferedImage image = ImageIO.read(file);

		if (image == null)
			throw new NullPointerException("Unable to load image size " + file.length() + " bytes from " + file.toPath());

		final CompChatColor[][] chatColors = parseImage(image, height);
		final ChatImage chatImage = new ChatImage();

		chatImage.lines = parseColors(chatColors, characterType);

		return chatImage;
	}

	/**
	 * Return a chat image from finished lines that were already created from {@link #fromFile(File, int, Type)}
	 *
	 * @param lines
	 * @return
	 */
	public static ChatImage fromLines(String[] lines) {
		final ChatImage chatImage = new ChatImage();
		chatImage.lines = lines;

		return chatImage;
	}

	/*
	 * Parse the given image into chat colors
	 */
	private static CompChatColor[][] parseImage(BufferedImage image, int height) {
		final double ratio = (double) image.getHeight() / image.getWidth();
		int width = (int) (height / ratio);

		if (width > 10)
			width = 10;

		final BufferedImage resized = resizeImage(image, (int) (height / ratio), height);
		final CompChatColor[][] chatImg = new CompChatColor[resized.getWidth()][resized.getHeight()];

		for (int x = 0; x < resized.getWidth(); x++)
			for (int y = 0; y < resized.getHeight(); y++) {
				final int rgb = resized.getRGB(x, y);
				final CompChatColor closest = getClosestChatColor(new Color(rgb, true));

				chatImg[x][y] = closest;
			}

		return chatImg;
	}

	/*
	 * Resize the given image
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
		final AffineTransform af = new AffineTransform();

		af.scale(
				width / (double) originalImage.getWidth(),
				height / (double) originalImage.getHeight());

		final AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		return operation.filter(originalImage, null);
	}

	/*
	 * Returns the closes chat color from the given color
	 */
	private static CompChatColor getClosestChatColor(Color color) {
		if (MinecraftVersion.olderThan(V.v1_16)) {
			if (color.getAlpha() < 128)
				return null;

			int index = 0;
			double best = -1;

			for (int i = 0; i < LEGACY_COLORS.length; i++)
				if (areSimilar(LEGACY_COLORS[i], color))
					return CompChatColor.getColors().get(i);

			for (int i = 0; i < LEGACY_COLORS.length; i++) {
				final double distance = getDistance(color, LEGACY_COLORS[i]);

				if (distance < best || best == -1) {
					best = distance;
					index = i;
				}
			}

			return CompChatColor.getColors().get(index);
		}

		return CompChatColor.of(color);
	}

	/*
	 * Return if colors are nearly identical
	 */
	private static boolean areSimilar(Color first, Color second) {
		return Math.abs(first.getRed() - second.getRed()) <= 5 &&
				Math.abs(first.getGreen() - second.getGreen()) <= 5 &&
				Math.abs(first.getBlue() - second.getBlue()) <= 5;

	}

	/*
	 * Returns how different two colors are
	 */
	private static double getDistance(Color first, Color second) {
		final double rmean = (first.getRed() + second.getRed()) / 2.0;
		final double r = first.getRed() - second.getRed();
		final double g = first.getGreen() - second.getGreen();
		final int b = first.getBlue() - second.getBlue();

		final double weightR = 2 + rmean / 256.0;
		final double weightG = 4.0;
		final double weightB = 2 + (255 - rmean) / 256.0;

		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	/*
	 * Parse the given 2D colors to fit lines
	 */
	private static String[] parseColors(CompChatColor[][] colors, Type imgchar) {
		final String[] lines = new String[colors[0].length];

		for (int y = 0; y < colors[0].length; y++) {
			String line = "";

			for (int x = 0; x < colors.length; x++) {
				final CompChatColor color = colors[x][y];

				line += (color != null) ? colors[x][y].toString() + imgchar : TRANSPARENT_CHAR;
			}

			lines[y] = line + ChatColor.RESET;
		}

		return lines;
	}

	/* ------------------------------------------------------------------------------- */
	/* Classes */
	/* ------------------------------------------------------------------------------- */

	/**
	 * Represents common image characters
	 *
	 * @author bobacadodl
	 */
	public enum Type {
		BLOCK('\u2588'),
		DARK_SHADE('\u2593'),
		MEDIUM_SHADE('\u2592'),
		LIGHT_SHADE('\u2591');

		/**
		 * The character used to build the image
		 */
		@Getter
		private char character;

		private Type(char c) {
			this.character = c;
		}

		/**
		 * Return the character
		 *
		 * @return
		 */
		@Override
		public String toString() {
			return String.valueOf(character);
		}
	}
}
