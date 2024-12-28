package com.gmail.coollord14.skyparticles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class TabCompleter implements TabExecutor {

	private final Main plugin;

	public TabCompleter(Main pluginInp) {
		plugin = pluginInp;
		plugin.getCommand("sp").setExecutor(this);
		plugin.getCommand("sp").setTabCompleter(this);
	}

	private enum MainCommands {
		HELP("help", "skyparticles.help"),
		TOGGLE("toggle", "skyparticles.player.toggle"),
		RELOAD("reload", "skyparticles.admin.reload"),
		ADD("add,create", "skyparticles.admin.add"),
		WORLDGUARD("worldguard,wg", "skyparticles.admin.add"),
		REMOVE("remove,delete,destroy", "skyparticles.admin.remove"),
		DISABLE("disable", "skyparticles.admin.enabledisable"),
		ENABLE("enable", "skyparticles.admin.enabledisable"),
		POS1("pos1", "skyparticles.admin.add"),
		POS2("pos2", "skyparticles.admin.add"),
		SET("set,modify,change", "skyparticles.admin.set");

		private String cmdName;
		private String perm;

		private MainCommands(String name, String perm) {
			cmdName = name;
			this.perm = perm;
		}

		public static MainCommands match(String label, String firstArg) {
			boolean arg = false;
			if (label.equalsIgnoreCase("sp") || label.equalsIgnoreCase("skyparticles"))
				arg = true;
			for (MainCommands cmd : values()) {
				if (arg) {
					for (String item : cmd.cmdName.split(",")) {
						if (firstArg.equalsIgnoreCase(item))
							return cmd;
					}
				}
			}
			return null;
		}

		public String[] trim(String[] args, StringBuffer name) {
			if (args.length == 0)
				return args;
			if (!args[0].equalsIgnoreCase(cmdName))
				return args;
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			if (name != null)
				name.append(" " + args[0]);
			return newArgs;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MainCommands cmd = MainCommands.match(label, args.length >= 1 ? args[0] : "");
		if (cmd == null) {
			cmdHelp(sender);
			return true;
		}
		StringBuffer cmdName = new StringBuffer(label);
		args = cmd.trim(args, cmdName);

		if (!checkCommandPermissions(sender, args, cmd))
			return true;

		switch (cmd) {
		case HELP:
			cmdHelp(sender);
			break;
		case TOGGLE:
			cmdToggle(sender);
			break;
		case RELOAD:
			cmdReload(sender);
			break;
		case POS1:
			cmdPos(sender, 0);
			break;
		case POS2:
			cmdPos(sender, 1);
			break;
		case ADD:
			cmdAdd(sender, args);
			break;
		case WORLDGUARD:
			cmdWorldGuard(sender, args);
			break;
		case REMOVE:
			cmdRemove(sender, args);
			break;
		case SET:
			cmdSet(sender, args);
			break;
		case DISABLE:
			cmdDisable(sender, args);
			break;
		case ENABLE:
			cmdEnable(sender, args);
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if(command.getName().equalsIgnoreCase("sp")) {
			ArrayList<String> list = new ArrayList<String>();
			String lastArg = "";
			if(args.length == 1) {
				return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "pos1", "pos2", "enable", "disable", "toggle", "add", "worldguard", "remove", "set", "reload"), new ArrayList<>());
			} 

			else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("worldguard") || args[0].equalsIgnoreCase("wg")) {
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("worldguard") || args[0].equalsIgnoreCase("wg")) {
						if(Main.useWorldguard && sender instanceof Player) {
							Location loc = ((Player) sender).getLocation();
							RegionManager regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(loc.getWorld()));
							list.addAll(regions.getApplicableRegionsIDs(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())));
							lastArg = args[1];
						}
					} else {
						list.add("<name>");
						lastArg = args[1];
					}
				}
				else if(args.length == 3) {
					for (Enum<?> stuff : Particle.class.getEnumConstants()) {
						if(Particle.valueOf(stuff.toString()).getDataType().getSimpleName().contains("Void")) {
							list.add(stuff.toString());
							lastArg = args[2];
						}
					}
				}
				else if(args.length == 4) {
					list.add("<distance>");
					lastArg = args[3];
				}
				else if(args.length == 5) {
					list.add("[count]");
					lastArg = args[4];
				}
				else if(args.length == 6) {
					list.add("[speed]");
					lastArg = args[5];
				}
				return StringUtil.copyPartialMatches(lastArg, list, new ArrayList<>());
			} 

			else if((args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("destroy") || args[0].equalsIgnoreCase("delete")) && args.length == 2) {
				for (String location : plugin.getConfig().getConfigurationSection("particles.").getKeys(false)) {
					list.add(location);
				}
				return StringUtil.copyPartialMatches(args[1], list, new ArrayList<>());
			} 

			else if(args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("modify") || args[0].equalsIgnoreCase("change")) {
				if(args.length == 2) {
					for (String location : plugin.getConfig().getConfigurationSection("particles.").getKeys(false)) {
						list.add(location);
					}
					lastArg = args[1];
				}
				if(args.length == 3) {
					list.addAll(Arrays.asList("name", "particle", "distance", "count", "speed", "bounds"));
					lastArg = args[2];
				}
				if(args.length == 4) {
					if(args[2].equalsIgnoreCase("name"))
						list.add("<new name>");
					if(args[2].equalsIgnoreCase("distance"))
						list.add("<new distance>");
					if(args[2].equalsIgnoreCase("count"))
						list.add("<new count>");
					if(args[2].equalsIgnoreCase("speed"))
						list.add("<new speed>");
					if(args[2].equalsIgnoreCase("particle"))
						for (Enum<?> stuff : Particle.class.getEnumConstants()) {
							if(Particle.valueOf(stuff.toString()).getDataType().getSimpleName().contains("Void")) {
								list.add(stuff.toString());
								lastArg = args[2];
							}
						}
					lastArg = args[3];
				}
				return StringUtil.copyPartialMatches(lastArg, list, new ArrayList<>());
			}
		}
		return null;
	}

	private void cmdHelp(CommandSender sender) {
		List<String> result = new ArrayList<String>();
		result.add("&b---== SkyParticles Help Page &7<required> | [optional] &b==---");
		result.add(" &b/sp help&7: lists the commands");
		result.add(" &b/sp pos1/pos2&7: declare the positions for a sky-particle");
		result.add(" &b/sp add <location name> <particle> <distance> [count] [speed]&7: create a new sky-particle");
		result.add(" &b/sp worldguard <region name> <particle> <distance> [count] [speed]&7: create a new sky-particle for a cuboid region");
		result.add(" &b/sp remove <location name>&7: remove a skyparticle");
		result.add(" &b/sp set <location name> <variable> <new value>&7: modify the specified location");
		result.add(" &b/sp toggle&7: disable particles for yourself");
		result.add(" &b/sp disable/enable <location name>&7: disable or enable a particle location");
		result.add(" &b/sp reload&7: reload the configuration");
		result.add("");
		for(String msg : result) {
			Methods.sendMessage(false, false, sender, ChatColor.translateAlternateColorCodes('&', msg));
		}
	}

	private void cmdPos(CommandSender sender, int pos) {
		if(!(sender instanceof Player)) {
			Methods.sendMessage(true, false, sender, "&cYou must be a player to use this command!");
			return;
		}
		Player p = ((Player) sender);
		if(!Main.selectedLocations.containsKey(p)) {
			Main.selectedLocations.put(p, new HashMap<>());
		}
		Location loc = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation();
		Main.selectedLocations.get(p).put(pos, loc);
		Methods.sendMessage(true, true, sender, "&aSet position &e" + (pos + 1) + " &aat (&e" + loc.getBlockX() + "&a, &e" + loc.getBlockY() + "&a, &e" + loc.getBlockZ() + "&a).");
	}

	private void cmdDisable(CommandSender sender, String[] args) {
		if(args.length < 1) {
			Methods.sendMessage(true, true, sender, "&cYou must specify the location name.");
			return;
		}
		if(plugin.getConfig().getConfigurationSection("particles.").getKeys(false).contains(args[0])) {
			boolean enabled = plugin.getConfig().getBoolean("particles." + args[0] + ".enabled");
			if(!enabled)
				Methods.sendMessage(true, true, sender, "&cThat location is already disabled.");
			else {
				plugin.getConfig().set("particles." + args[0] + ".enabled", false);
				Methods.sendMessage(true, true, sender, "&cDisabled location &e" + args[0] + "&c.");
			}
		}
		else Methods.sendMessage(true, true, sender, "&cThe location you suggested couldn't be found.");
	}

	private void cmdEnable(CommandSender sender, String[] args) {
		if(args.length < 1) {
			Methods.sendMessage(true, true, sender, "&cYou must specify the location name.");
			return;
		}
		if(plugin.getConfig().getConfigurationSection("particles.").getKeys(false).contains(args[0])) {
			boolean enabled = plugin.getConfig().getBoolean("particles." + args[0] + ".enabled");
			if(enabled)
				Methods.sendMessage(true, true, sender, "&aThat location is already enabled.");
			else {
				if(!Methods.checkCorrectness(plugin, sender)) {
					plugin.getConfig().set("particles." + args[0] + ".enabled", true);
					Methods.sendMessage(true, true, sender, "&aEnabled location &e" + args[0] + "&a.");
				}
			}
		}
		else Methods.sendMessage(true, true, sender, "&cThe location you suggested couldn't be found.");
	}

	private void cmdToggle(CommandSender sender) {
		if(!(sender instanceof Player)) {
			Methods.sendMessage(true, false, sender, "&cYou must be a player to use this command!");
			return;
		}
		String player = ((Player) sender).getUniqueId().toString();
		if(Main.toggled.contains(player)) {
			Main.toggled.remove(player);
			Methods.sendMessage(true, true, sender, "&aYou have enabled SkyParticles for yourself.");
		}
		else {
			Main.toggled.add(player);
			Methods.sendMessage(true, true, sender, "&cYou have disabled SkyParticles for yourself.");
		}
		Main.updateToggles();
	}

	private void cmdReload(CommandSender sender) {
		plugin.reloadConfig();
		Main.setToggles();
		if(Methods.checkCorrectness(plugin, sender)) 
			Bukkit.getLogger().warning("&cThere was an error with the config. Some particles may not work as expected!");
		else if(plugin.getConfig().getConfigurationSection("particles.").getKeys(false) != null)
			Methods.sendMessage(true, true, sender, "&aSuccessfully reloaded config with " + plugin.getConfig().getConfigurationSection("particles.").getKeys(false).size() + " particle locations.");
		else Methods.sendMessage(true, true, sender, "&aSuccessfully reloaded config.");
	}

	private void cmdAdd(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			Methods.sendMessage(true, false, sender, "&cYou must be a player to use this command!");
			return;
		}
		Particle particle;
		double distance = 1.0;
		int count = 1;
		double speed = 0.5;

		if(args.length < 4)  {
			Methods.sendMessage(true, true, sender, "&cYou must specify location name, particle type, and render distance.");
			return;
		}
		else if(args.length >= 4) {
			if(plugin.getConfig().getConfigurationSection("particles.").getKeys(false).contains(args[1]))  {
				Methods.sendMessage(true, true, sender, "&cThe location you specified already exists. To modify it, use /sp set.");
				return;
			}
			try {
				particle = (Particle.valueOf(args[2].toUpperCase()));
			} catch (Exception e) {
				Methods.sendMessage(true, true, sender, "&cThe particle you suggested couldn't be found.");
				return;
			}
			try {
				distance = Double.parseDouble(args[3]);
				count = (int) Math.round(10 * (distance / 5) * (distance / 5));
			} catch (Exception e) {
				Methods.sendMessage(true, true, sender, "&cThe distance must be a number.");
				return;
			}

			if(args.length >= 5)
				try {
					count = Integer.parseInt(args[4]);
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe count must be an integer.");
					return;
				}
			if(args.length == 6)
				try {
					speed = Double.parseDouble(args[5]);
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe speed must be a number.");
					return;
				}
			Methods.registerParticle((Player) sender, args[1], particle, distance, count, speed, plugin);
		}
	}

	private void cmdWorldGuard(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)) {
			Methods.sendMessage(true, false, sender, "&cYou must be a player to use this command!");
			return;
		}
		Particle particle;
		Double distance = 1.0;
		Integer count = 1;
		Double speed = 0.5;

		if(args.length < 4)  {
			Methods.sendMessage(true, true, sender, "&cYou must specify region name, particle type, and render distance.");
			return;
		}
		else if(args.length >= 4) {
			String name = ((Player) sender).getWorld().getName() + "@" + args[1]; 
			if(plugin.getConfig().getConfigurationSection("particles.").getKeys(false).contains(name)) {
				Methods.sendMessage(true, true, sender, "&cThe location you specified already exists. To modify it, use /sp set.");
				return;
			}
			try {
				particle = (Particle.valueOf(args[2].toUpperCase()));
			} catch (Exception e) {
				Methods.sendMessage(true, true, sender, "&cThe particle you suggested couldn't be found.");
				return;
			}
			try {
				distance = Double.parseDouble(args[3]);
				count = (int) Math.round(10 * (distance / 5) * (distance / 5));
			} catch (Exception e) {
				Methods.sendMessage(true, true, sender, "&cThe distance must be a number.");
				return;
			}

			if(args.length >= 5)
				try {
					count = Integer.parseInt(args[4]);
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe count must be an integer.");
					return;
				}
			if(args.length == 6)
				try {
					speed = Double.parseDouble(args[5]);
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe speed must be a number.");
					return;
				}
			Methods.registerWorldGuard((Player) sender, args[1], particle, distance, count, speed, plugin);
		}
	}

	private void cmdRemove(CommandSender sender, String[] args) {
		if(args.length < 2) {
			Methods.sendMessage(true, true, sender, "&cYou must specify the location name.");
			return;
		}
		Methods.removeLocation((Player) sender, args[1], plugin);
	}

	private void cmdSet(CommandSender sender, String[] args) {
		if(args.length == 3) {
			if(args[2].equalsIgnoreCase("bounds")) {
				Player p = (Player) sender;
				if(Main.selectedLocations.containsKey(p) && Main.selectedLocations.get(p).size() == 2) {
					if(!Main.selectedLocations.get(p).get(0).getWorld().equals(Main.selectedLocations.get(p).get(1).getWorld())) {
						Methods.sendMessage(true, true, p, "&cYour locations must be in the same world!");
						return;
					}
					plugin.getConfig().set("particles." + args[1] + ".world", Main.selectedLocations.get(p).get(0).getWorld().getName());
					plugin.getConfig().set("particles." + args[1] + ".pos1.x", Main.selectedLocations.get(p).get(0).getX());
					plugin.getConfig().set("particles." + args[1] + ".pos1.y", Main.selectedLocations.get(p).get(0).getY());
					plugin.getConfig().set("particles." + args[1] + ".pos1.z", Main.selectedLocations.get(p).get(0).getZ());
					plugin.getConfig().set("particles." + args[1] + ".pos2.x", Main.selectedLocations.get(p).get(1).getX());
					plugin.getConfig().set("particles." + args[1] + ".pos2.y", Main.selectedLocations.get(p).get(1).getY());
					plugin.getConfig().set("particles." + args[1] + ".pos2.z", Main.selectedLocations.get(p).get(1).getZ());
					plugin.saveConfig();

					Methods.sendMessage(true, true, sender, "&aYou updated the&e " + args[2] + " &aof&e " + args[1] + "&a.");
					return;
				}
				Methods.sendMessage(true, true, p, "&cYou must select an area first!");
				return;
			}
		} else if(args.length < 4) {
			Methods.sendMessage(true, true, sender, "&cYou must specify the location name, variable, and new value.");
			return;
		} else if(plugin.getConfig().getConfigurationSection("particles." + args[1]) != null) {
			if(args[2].equalsIgnoreCase("name")) {
				for (String s : plugin.getConfig().getConfigurationSection("particles." + args[1]).getKeys(false)) {
					plugin.getConfig().set("particles." + args[3] + "." + s,  plugin.getConfig().get("particles." + args[1] + "." + s));
				}
				plugin.getConfig().set("particles." + args[1], null);
			}
			else if(args[2].equalsIgnoreCase("particle")) {
				Particle particle;
				try {
					particle = (Particle.valueOf(args[3].toUpperCase()));
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe particle you suggested couldn't be found.");
					return;
				}
				plugin.getConfig().set("particles." + args[1] + ".particle", particle.toString());
			}
			else if(args[2].equalsIgnoreCase("distance")) {
				try {
					plugin.getConfig().set("particles." + args[1] + ".distance", Double.parseDouble(args[3]));
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe distance must be a number.");
					return;
				}
			}
			else if(args[2].equalsIgnoreCase("count")) {
				try {
					plugin.getConfig().set("particles." + args[1] + ".count", Integer.parseInt(args[3]));
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe count must be an integer.");
					return;
				}

			}
			else if(args[2].equalsIgnoreCase("speed")) {
				try {
					plugin.getConfig().set("particles." + args[1] + ".speed", Double.parseDouble(args[3]));
				} catch (Exception e) {
					Methods.sendMessage(true, true, sender, "&cThe speed must be a number.");
					return;
				}
			}
			plugin.saveConfig();
			if(!Methods.checkCorrectness(plugin, sender)) 
				Methods.sendMessage(true, true, sender, "&aYou updated the&e " + args[2] + " &aof&e " + args[1] + "&a:&e " + args[3] + "&a.");
		}
		else Methods.sendMessage(true, true, sender, "&cThe location you suggested couldn't be found.");
	}

	private boolean checkCommandPermissions(CommandSender sender, String[] args, MainCommands cmd) {
		boolean pass = false;
		if (cmd.perm.isEmpty())
			pass = true;
		else if (sender.hasPermission(cmd.perm))
			pass = true;

		if (!pass)
			Methods.sendMessage(true, true, sender, "&cYou don't have permission to use this command!");
		return pass;
	}
}
