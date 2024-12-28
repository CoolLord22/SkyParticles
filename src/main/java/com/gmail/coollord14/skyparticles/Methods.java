package com.gmail.coollord14.skyparticles;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Methods {
	public static String prefix = "&f[&bSkyParticles&f] ";

	public static void removeLocation(Player player, String name, Main plugin) {
		if(plugin.getConfig().getConfigurationSection("particles." + name) != null) {
			plugin.getConfig().set("particles." + name, null);
			plugin.saveConfig();
			Main.registeredParticles.remove(name);
			sendMessage(true, true, player, "&aYou have deleted " + ChatColor.YELLOW + name);
		}
		else sendMessage(true, true, player, "&cThe location you suggested couldn't be found.");
	}

	public static void registerParticle(Player p, String name, Particle particle, Double distance, Integer count, Double speed, Main plugin) {
		if(Main.selectedLocations.containsKey(p) && Main.selectedLocations.get(p).size() == 2) {
			if(!Main.selectedLocations.get(p).get(0).getWorld().equals(Main.selectedLocations.get(p).get(1).getWorld())) {
				sendMessage(true, true, p, ChatColor.RED + "Your locations must be in the same world!");
				return;
			}
			plugin.getConfig().set("particles." + name + ".particle", particle.toString());
			plugin.getConfig().set("particles." + name + ".distance", distance);
			plugin.getConfig().set("particles." + name + ".count", count);
			plugin.getConfig().set("particles." + name + ".speed", speed);
			plugin.getConfig().set("particles." + name + ".world", Main.selectedLocations.get(p).get(0).getWorld().getName());
			plugin.getConfig().set("particles." + name + ".pos1.x", Main.selectedLocations.get(p).get(0).getX());
			plugin.getConfig().set("particles." + name + ".pos1.y", Main.selectedLocations.get(p).get(0).getY());
			plugin.getConfig().set("particles." + name + ".pos1.z", Main.selectedLocations.get(p).get(0).getZ());
			plugin.getConfig().set("particles." + name + ".pos2.x", Main.selectedLocations.get(p).get(1).getX());
			plugin.getConfig().set("particles." + name + ".pos2.y", Main.selectedLocations.get(p).get(1).getY());
			plugin.getConfig().set("particles." + name + ".pos2.z", Main.selectedLocations.get(p).get(1).getZ());
			plugin.getConfig().set("particles." + name + ".enabled", true);
			plugin.saveConfig();
			if(!checkCorrectness(plugin, p)) {
				sendMessage(true, true, p, "&aSuccessfully created a new particle location: " + ChatColor.YELLOW + name);
				Main.selectedLocations.get(p).clear();
			}
			return;
		}
		sendMessage(true, true, p, "&cYou must select an area first!");
		return;
	}

	public static void registerWorldGuard(Player p, String name, Particle particle, Double distance, Integer count, Double speed, Main plugin) {
		if(Main.useWorldguard) {
			RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
			if(regions.getRegion(name) != null && regions.getRegion(name) instanceof ProtectedCuboidRegion) {
				String configName = p.getWorld().getName() + "@" + name;
				ProtectedRegion region = regions.getRegion(name);
				BlockVector3 min = region.getMinimumPoint();
				BlockVector3 max = region.getMaximumPoint();

				plugin.getConfig().set("particles." + configName + ".particle", particle.toString());
				plugin.getConfig().set("particles." + configName + ".distance", distance);
				plugin.getConfig().set("particles." + configName + ".count", count);
				plugin.getConfig().set("particles." + configName + ".speed", speed);
				plugin.getConfig().set("particles." + configName + ".world", p.getWorld().getName());
				plugin.getConfig().set("particles." + configName + ".pos1.x", min.getX());
				plugin.getConfig().set("particles." + configName + ".pos1.y", min.getY());
				plugin.getConfig().set("particles." + configName + ".pos1.z", min.getZ());
				plugin.getConfig().set("particles." + configName + ".pos2.x", max.getX());
				plugin.getConfig().set("particles." + configName + ".pos2.y", max.getY());
				plugin.getConfig().set("particles." + configName + ".pos2.z", max.getZ());
				plugin.getConfig().set("particles." + configName + ".enabled", true);
				plugin.saveConfig();
				if(!checkCorrectness(plugin, p)) {
					sendMessage(true, true, p, "&aSuccessfully created a new particle location: " + ChatColor.YELLOW + configName);
				}
				return;
			}
			sendMessage(true, true, p, "&cRegion was not found / is not cuboid!");
			return;
		}
		sendMessage(true, true, p, "&cWorldGuard wasn't found!");
		return;
	}

	public static Location getLocation(String sp, String corner, Main plugin) {
		final int x = plugin.getConfig().getInt("particles." + sp + "." + corner + ".x");
		final int y = plugin.getConfig().getInt("particles." + sp + "." + corner + ".y");
		final int z = plugin.getConfig().getInt("particles." + sp + "." + corner + ".z");
		final World world = Bukkit.getWorld(plugin.getConfig().getString("particles." + sp + ".world"));
		return new Location(world, x, y, z);
	}

	public static Location getPos(String val, Location loc1, Location loc2) {
		int x1,x2,y1,y2,z1,z2;
		x1 = loc1.getBlockX();
		y1 = loc1.getBlockY();
		z1 = loc1.getBlockZ();
		x2 = loc2.getBlockX();
		y2 = loc2.getBlockY();
		z2 = loc2.getBlockZ();
		if(val.equals("min"))
			return new Location(loc1.getWorld(), Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
		if(val.equals("max"))
			return new Location(loc1.getWorld(), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
		return null;
	}

	public static boolean isContained(Location loc, Location min, Location max) {
		return min.getWorld().equals(loc.getWorld()) && loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY() && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ();
	}

	public static boolean checkCorrectness(Main plugin, CommandSender sender) {
		Main.registeredParticles.clear();
		Main.sendParticleTo.clear();
		boolean errors = false;
		for(String name : plugin.getConfig().getConfigurationSection("particles").getKeys(false)) {
			ArrayList<String> requirements = new ArrayList<>(Arrays.asList("particle", "distance", "count", "speed", "world", "pos1.x", "pos1.y", "pos1.z", "pos2.x", "pos2.y", "pos2.z"));
			ArrayList<String> missing = new ArrayList<>();
			for(String req : requirements) {
				if(plugin.getConfig().get("particles." + name + "." + req) == null) {
					plugin.getConfig().set("particles." + name + ".enabled", false);
					plugin.saveConfig();
					missing.add(req);
				}
			}
			
			if(!Particle.valueOf(plugin.getConfig().getString("particles." + name + ".particle")).getDataType().getSimpleName().contains("Void")) {
				errors = true;
				plugin.getConfig().set("particles." + name + ".enabled", false);
				plugin.saveConfig();
				Methods.sendMessage(true, true, sender, "&aThe particle location &e" + name + " &acontains a particle type which is not supported and was automatically disabled.");
			} else if(!missing.isEmpty()) {
				errors = true;
				Methods.sendMessage(true, true, sender, "&aThe particle location &e" + name + " &ais missing &e" + String.join("&a, &e", missing) + " &aand was automatically disabled.");
			} else {
				try {
					Particle particle = Particle.valueOf(plugin.getConfig().getString("particles." + name + ".particle"));
					double distance = plugin.getConfig().getDouble("particles." + name + ".distance");
					int count = plugin.getConfig().getInt("particles." + name + ".count");
					double speed = plugin.getConfig().getDouble("particles." + name + ".speed");
					boolean enabled = plugin.getConfig().getBoolean("particles." + name + ".enabled", false);
					Location min = Methods.getPos("min", Methods.getLocation(name, "pos1", plugin), Methods.getLocation(name, "pos2", plugin));
					Location max = Methods.getPos("max", Methods.getLocation(name, "pos1", plugin), Methods.getLocation(name, "pos2", plugin));

					SkyParticle sp = new SkyParticle(name, particle, distance, count, speed, enabled, min, max);
					Main.registeredParticles.put(name, sp);
				} catch (IllegalArgumentException e) {
					errors = true;
					plugin.getConfig().set("particles." + name + ".enabled", false);
					plugin.saveConfig();
					Methods.sendMessage(true, true, sender, "&aThe particle location &e" + name + " &acontains an invalid particle type and was automatically disabled.");
				}
			}
		}
		return errors;
	}
	
	public static void sendMessage(boolean usePrefix, boolean actionBar, CommandSender sender, String msg) {
		if(usePrefix)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + msg));
		if(!usePrefix)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));

		if(sender instanceof Player && actionBar)
			((Player) sender).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', msg)));
	}
}