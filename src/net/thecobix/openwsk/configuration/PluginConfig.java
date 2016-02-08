package net.thecobix.openwsk.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.thecobix.openwsk.main.OpenWSK;

public class PluginConfig {

	private File file;
	public FileConfiguration cfg;
	
	public PluginConfig() {
		this.file = new File("plugins/OpenWSK/config.yml");
		this.cfg = YamlConfiguration.loadConfiguration(file);
	}
	
	public void initConfig() {
		this.cfg.options().copyDefaults(true);
		this.cfg.options().header("config.yml by OpenWSK v"+OpenWSK.getPluginInstance().getDescription().getVersion());
		this.cfg.addDefault("Arenas", "Example1,Example2");
		try {
			this.cfg.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() throws IOException {
		this.cfg.save(file);
	}
	
}
