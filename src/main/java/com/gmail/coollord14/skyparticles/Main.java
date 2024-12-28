package com.gmail.coollord14.skyparticles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {
	public static Main plugin;
	
	static HashMap<Player, ArrayList<String>> sendParticleTo = new HashMap<>();
	static HashMap<Player, HashMap<Integer, Location>> selectedLocations = new HashMap<>();
	static List<String> toggled = new ArrayList<String>();
	static boolean useWorldguard = false;

	public Particle getParticle(String particleName) {
		try {
			return Particle.valueOf(particleName);
		} catch (final IllegalArgumentException e) { return null; }
	}

	@Override
	public void onEnable() {
		try {
			plugin = this;
			if(this.getServer().getPluginManager().getPlugin("WorldGuard") != null)
				useWorldguard = true;
			new TabCompleter(this);
			new ParticleListener(this);
			saveDefaultConfig();
			setToggles();
			Methods.checkCorrectness(plugin, Bukkit.getConsoleSender());
			loadParticles();
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

	public void loadParticles() {
		plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, (Runnable) () -> {
			if(plugin.getConfig().getConfigurationSection("particles") != null && plugin.getConfig().getConfigurationSection("particles").getKeys(false).size() > 0)
					for(final String sp : plugin.getConfig().getConfigurationSection("particles").getKeys(false)) {
						if(plugin.getConfig().getBoolean("particles." + sp + ".enabled"))
							for(Player p : Main.sendParticleTo.keySet()) {
								for(String particle : Main.sendParticleTo.get(p)) {
									if(particle.equals(sp)) {
										Particle actualParticle = Particle.valueOf(getConfig().getString("particles." + sp + ".particle"));
										if(!actualParticle.getDataType().getSimpleName().contains("Void")) {
											plugin.getConfig().set("particles." + sp + ".enabled", false);
											plugin.saveConfig();
											Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&aThe particle location &e" + sp + " &acontains a particle type which is not supported and was automatically disabled."));
										}
										double speed = plugin.getConfig().getDouble("particles." + sp + ".speed");
										Double distance = plugin.getConfig().getDouble("particles." + sp + ".distance");
										int count = plugin.getConfig().getInt("particles." + sp + ".count");
										Material data = null;

										if(plugin.getConfig().getBoolean("particles." + sp + ".enabled") && !toggled.contains(p.getUniqueId().toString()))
											p.spawnParticle(actualParticle, p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(), count, distance, distance, distance, speed, data);
									}
								}
							} 
					}
		}, 0L, 20L);
	}

	public static void setToggles() {
		List<String> toggles = new ArrayList<String>();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, (Runnable) () -> {
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