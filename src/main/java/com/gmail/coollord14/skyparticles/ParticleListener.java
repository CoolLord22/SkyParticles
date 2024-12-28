package com.gmail.coollord14.skyparticles;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ParticleListener implements Listener {

	private final Main plugin;

	public ParticleListener(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(plugin.getConfig().getConfigurationSection("particles") != null) 
			if(plugin.getConfig().getConfigurationSection("particles").getKeys(false).size() > 0) {
				ArrayList<String> particleToSend = new ArrayList<String>();
				for(String particle : plugin.getConfig().getConfigurationSection("particles").getKeys(false)) {
					if(plugin.getConfig().getBoolean("particles." + particle + ".enabled")) {
						Location min = Methods.getPos("min", Methods.getLocation(particle, "pos1", plugin), Methods.getLocation(particle, "pos2", plugin));
						Location max = Methods.getPos("max", Methods.getLocation(particle, "pos1", plugin), Methods.getLocation(particle, "pos2", plugin));
						if(Methods.isContained(player.getLocation(), min, max))
							particleToSend.add(particle);
					}
				}
				Main.sendParticleTo.put(e.getPlayer(), particleToSend);
			}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if(!Main.sendParticleTo.containsKey(e.getPlayer())) {
			Main.sendParticleTo.put(e.getPlayer(), new ArrayList<String>());
		}
	}
}
