package com.cursedplanet.cursedlibrary;

import com.cursedplanet.cursedlibrary.entity.EntityCreator;
import org.bukkit.entity.EntityType;
import org.mineacademy.fo.command.SimpleCommand;

public class Temp extends SimpleCommand {
	protected Temp() {
		super("temp");
	}

	@Override
	protected void onCommand() {
		checkConsole();

		EntityCreator creator = new EntityCreator(EntityType.ZOMBIE);
		creator.setGlowing(true, com.cursedplanet.cursedlibrary.glowing.GlowAPI.Color.DARK_PURPLE);
		creator.summonEntity(getPlayer().getLocation());
	}
}
