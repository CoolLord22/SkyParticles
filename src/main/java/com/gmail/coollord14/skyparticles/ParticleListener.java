package com.gmail.coollord14.skyparticles;

import java.util.HashSet;

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

		HashSet<SkyParticle> particlesToSend = new HashSet<>();
		if(!Main.toggled.contains(player.getUniqueId().toString())) {
			for(SkyParticle registeredParticle : Main.registeredParticles.values()) {
				if(registeredParticle.isEnabled()) {
					Location min = registeredParticle.getMin();
					Location max = registeredParticle.getMax();
					if(Methods.isContained(player.getLocation(), min, max))
						particlesToSend.add(registeredParticle);
				}
			}
		}
		Main.sendParticleTo.put(e.getPlayer().getUniqueId(), particlesToSend);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Main.sendParticleTo.put(e.getPlayer().getUniqueId(), new HashSet<>());
	}
}
