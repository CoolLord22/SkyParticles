package com.gmail.coollord14.skyparticles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
	private static Map<String, FileConfiguration> configs = new HashMap<>();
	private static Map<String, File> files = new HashMap<>();

	private File cfile;
	private FileConfiguration cconf;

	public ConfigManager() { }

	public void newConfig(String name, JavaPlugin plugin) {
		cfile = new File(plugin.getDataFolder(), name + ".yml");
		if (!cfile.exists()) {
			cfile.getParentFile().mkdirs();

			try {
				cfile.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		cconf = new YamlConfiguration();

		try {
			cconf.load(cfile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		configs.put(name, cconf);
		files.put(name, cfile);
	}

	public void saveConfig(String name) {
		try {
			configs.get(name).save( files.get(name) );
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile(String name) {
		return files.get(name);
	}

	public FileConfiguration getConfig(String name) {
		return configs.get(name);
	}

	public Map<String, FileConfiguration> getConfigList() {
		return configs;
	}

	public Map<String, File> getFilesList() {
		return files;
	}
}
