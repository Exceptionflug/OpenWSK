package net.thecobix.openwsk.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.thecobix.openwsk.main.OpenWSK;

public class ArenaConfig {

	private File file;
	public FileConfiguration cfg;
	private String name;
	
	public ArenaConfig(String arenaName) {
		this.name = arenaName;
		this.file = new File("plugins/OpenWSK/Arenas/"+arenaName+".yml");
		this.cfg = YamlConfiguration.loadConfiguration(file);
	}
	
	public void initConfig() {
		this.cfg.options().copyDefaults(true);
		this.cfg.options().header(name+".yml by OpenWSK v"+OpenWSK.getPluginInstance().getDescription().getVersion());
		this.cfg.addDefault("Arena.name", name);
		this.cfg.addDefault("Arena.location.world", "worldNameHere");
		this.cfg.addDefault("Arena.location.spectatorspawn.x", 0);
		this.cfg.addDefault("Arena.location.spectatorspawn.y", 70);
		this.cfg.addDefault("Arena.location.spectatorspawn.x", 0);
		this.cfg.addDefault("Arena.location.fightstart.team1.x", 0);
		this.cfg.addDefault("Arena.location.fightstart.team1.y", 70);
		this.cfg.addDefault("Arena.location.fightstart.team1.z", 0);
		this.cfg.addDefault("Arena.location.fightstart.team2.x", 0);
		this.cfg.addDefault("Arena.location.fightstart.team2.y", 70);
		this.cfg.addDefault("Arena.location.fightstart.team2.z", 0);
		this.cfg.addDefault("Arena.region.wholearena", "regionNameHere");
		this.cfg.addDefault("Arena.region.inner", "regionNameHere");
		this.cfg.addDefault("Arena.region.team1", "regionNameHere");
		this.cfg.addDefault("Arena.region.team2", "regionNameHere");
		this.cfg.addDefault("Arena.kit.essentials", true);
		this.cfg.addDefault("Arena.kit.name", "kitNameHere");
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
