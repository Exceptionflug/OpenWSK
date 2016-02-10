package net.thecobix.openwsk.configuration;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.thecobix.openwsk.main.OpenWSK;

/*
 * OpenWSK WarShip Fight System by St0n3gr1d
   Copyright (C) 2016 St0n3gr1d
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
