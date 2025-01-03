package com.gmail.coollord14.skyparticles;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	public static Main plugin;

	static HashMap<Player, HashMap<Integer, Location>> selectedLocations = new HashMap<>();
	static Set<String> toggled = new HashSet<>();
	static HashMap<UUID, HashSet<SkyParticle>> sendParticleTo = new HashMap<>();
	static HashMap<String, SkyParticle> registeredParticles = new HashMap<>();
	static boolean useWorldguard = false;

	@Override
	public void onEnable() {
		try {
			plugin = this;
			if(this.getServer().getPluginManager().getPlugin("WorldGuard") != null)
				useWorldguard = true;
			new TabCompleter(this);
			saveDefaultConfig();
			setToggles();
			Methods.checkCorrectness(plugin, Bukkit.getConsoleSender());
			new ParticleListener(this);
			loadParticleSender();
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSkyParticles has been enabled!"));
		}
		catch (Exception e) {
			getServer().getPluginManager().disablePlugin(this);
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cAn error disabled SkyParticles!"));
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		plugin = null;
	}

	public void loadParticleSender() {
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			for(Player p : Bukkit.getOnlinePlayers()) {
				for(SkyParticle sp : sendParticleTo.getOrDefault(p.getUniqueId(), new HashSet<>())) {
					if(sp.isEnabled()) {
						p.spawnParticle(sp.getParticle(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), sp.getCount(), sp.getDistance(), sp.getDistance(), sp.getDistance(), sp.getSpeed(), null);
					}
				}
			}
		}, 0L, 20L);
	}

	public static void setToggles() {
		Set<String> toggles = new HashSet<>();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
			String filename = "toggles.yml";
			File global = new File(plugin.getDataFolder(), filename);
			YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);
			if (!global.exists()) {
				try {
					global.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
					globalConfig.createSection("UUIDs");
				}
			}
			else if(globalConfig.getList("UUIDs") != null) {
				for(Object uuid : globalConfig.getList("UUIDs"))
					toggles.add(uuid.toString());
			}
		});
		toggled = toggles;
	}

	public static void updateToggles() {
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, (Runnable) () -> {
			String filename = "toggles.yml";
			File global = new File(plugin.getDataFolder(), filename);
			YamlConfiguration globalConfig = YamlConfiguration.loadConfiguration(global);
			globalConfig.set("UUIDs", toggled);
			try {
				globalConfig.save(global);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}