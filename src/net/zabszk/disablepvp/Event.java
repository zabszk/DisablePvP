package net.zabszk.disablepvp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Event implements Listener {

	@EventHandler (priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent e)
	{
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			if (Main.DisabledPvP.contains(e.getEntity().getName().toLowerCase())) {
				e.getDamager().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.getString("blocked")));
				e.setCancelled(true);
			}
			else if (Main.DisabledPvP.contains(e.getDamager().getName().toLowerCase())) {
				e.getEntity().sendMessage(ChatColor.translateAlternateColorCodes('&', Main.config.getString("blocked-you")));
				e.setCancelled(true);
			}
		}
	}
}
