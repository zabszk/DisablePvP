package net.zabszk.disablepvp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

public class Main extends JavaPlugin {

	static List<String> DisabledPvP;
	static FileConfiguration config;
	static Event event;
	
	@Override
	public void onEnable() {
		config = getConfig();
		event = new Event();
		
		config.addDefault("enabled", "&6PVP has been &cenabled");
		config.addDefault("disabled", "&6PVP has been &adisabled");
		config.addDefault("blocked", "&cSorry, pvp is disabled for this player.");
		config.addDefault("blocked-you", "&cSorry, you don't have pvp enabled. Type: /pvp");
		config.addDefault("AllowMetrics", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		if (config.getBoolean("AllowMetrics"))
		{
			try {
	            Metrics metrics = new Metrics(this);
	            metrics.start();
	        } catch (Exception e) {
	            System.out.println("Metrics error!");
	            e.printStackTrace();
	        }
		}
		
		File file = new File("plugins/DisablePvP/players.yml");
		
		if (!file.exists())
		{
			try {
				 file.createNewFile();
			} catch (Exception e) {}
			
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			DisabledPvP = new ArrayList<String>();
			cfg.set("disabled", DisabledPvP);
			
			try {
				cfg.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			DisabledPvP = cfg.getStringList("disabled");
		}
		
		getServer().getPluginManager().registerEvents(event, this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pvp")) {
			String search = "";
			
			if (args.length > 0)
			{
				if (sender.hasPermission("disablepvp.others")) search = args[0].toLowerCase();
				else sender.sendMessage(ChatColor.RED + "[DisablePvP] You don't have permissions!");
			}
			else if (sender instanceof Player)
			{
				if (sender.hasPermission("disablepvp.self")) search = sender.getName().toLowerCase();
				else sender.sendMessage(ChatColor.RED + "[DisablePvP] You don't have permissions!");
			}
			else sender.sendMessage(ChatColor.RED + "[DisablePvP] Please use: /pvp <nick> [new status]");
			
			if (search.length() > 0) {
				if (DisabledPvP.contains(search)) {
					if (args.length < 2 || args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("1") || args[1].equalsIgnoreCase("enable")) {
						DisabledPvP.remove(search);
						Save();
						
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("enabled")));
						if (args.length > 0 && Bukkit.getOfflinePlayer(search).isOnline()) Bukkit.getPlayer(search).sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("enabled")));
					}
				}
				else {
					if (args.length < 2 || args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("0") || args[1].equalsIgnoreCase("disable")) {
						DisabledPvP.add(search);
						Save();
						
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("disabled")));
						if (args.length > 0 && Bukkit.getOfflinePlayer(search).isOnline()) Bukkit.getPlayer(search).sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("disabled")));
					}
				}
			}
		}
		else if (cmd.getName().equalsIgnoreCase("pvpreload")) {
			if (sender.hasPermission("disablepvp.reload")){
				sender.sendMessage(ChatColor.GREEN + "[DisablePvP] Reloading (wait for success message or error on console)...");
				reloadConfig();
				config = getConfig();
				
				File file = new File("plugins/DisablePvP/players.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				
				DisabledPvP = cfg.getStringList("disabled");
				
				sender.sendMessage(ChatColor.GREEN + "[DisablePvP] Reload completed!");
			}
			else sender.sendMessage(ChatColor.RED + "[DisablePvP] You don't have permissions!");
		}
		return true;
	}

	static void Save()
	{
		File file = new File("plugins/DisablePvP/players.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
		
		cfg.set("disabled", DisabledPvP);
		
		try {
			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
