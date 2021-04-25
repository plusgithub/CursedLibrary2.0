package com.cursedplanet.cursedlibrary;

import com.cursedplanet.cursedlibrary.glowing.GlowAPI;
import com.cursedplanet.cursedlibrary.lib.remain.Remain;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Temp implements Listener {

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		GlowAPI.setGlowing(event.getEntity(), GlowAPI.Color.LIGHT_PURPLE, Remain.getOnlinePlayers());
	}
}
