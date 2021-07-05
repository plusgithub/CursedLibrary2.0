package com.cursedplanet.cursedlibrary.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.mineacademy.fo.Common;

import java.util.function.Consumer;

public class MenuPrompt implements Listener {

	//private CursedMenu menu = null;
	Integer cancelTicks;
	Player player;
	CursedMenu menu;
	Consumer<String> action;
	String[] lines;

	public MenuPrompt(Player player, CursedMenu menu, Integer cancelTicks, Consumer<String> action, String... lines) {
		this.cancelTicks = cancelTicks;
		this.player = player;
		this.menu = menu;
		this.action = action;
		this.lines = lines;

		engage();
	}

	private void engage() {
		menu.close(player);
		PromptHandler.prompts.put(player, this);

		Common.tellNoPrefix(player, lines);

		Common.runLater(cancelTicks, this::disengage);
	}

	public void pass(String message) {
		action.accept(message);
		disengage();
	}

	private void disengage() {
		if (PromptHandler.prompts.get(player) == this) {
			PromptHandler.prompts.remove(player);
			menu.display(player);
		}
	}
}
