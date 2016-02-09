package net.thecobix.openwsk.configuration;

import java.util.ArrayList;
import java.util.List;

import net.thecobix.openwsk.arena.Arena;
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
public class ConfigHelper {
	
	public static List<String> registeredArenaStringList = new ArrayList<>();
	public static List<ArenaConfig> arenaConfigs = new ArrayList<>();
	public PluginConfig pluginConfig;

	public void loadPluginConfig() {
		this.pluginConfig = new PluginConfig();
		this.pluginConfig.initConfig();
		String i = this.pluginConfig.cfg.getString("Arenas");
		String[] stringArray = i.split(",");
		for(String p : stringArray) {
			if(p.startsWith(" ")) {
				p = p.substring(1);
			}
			registeredArenaStringList.add(p);
		}
	}
	
	public void loadArenaConfig() {
		if(registeredArenaStringList.isEmpty()) {
			OpenWSK.Logger.log("Config", "No registered arenas!", 1);
			return;
		}
		for(String i : registeredArenaStringList) {
			ArenaConfig ac = new ArenaConfig(i);
			ac.initConfig();
			arenaConfigs.add(ac);
		}
	}

	public List<Arena> buildArenas() {
		List<Arena> out = new ArrayList<>();
		for(ArenaConfig ac : arenaConfigs) {
			Arena a = new Arena(ac.name);
			out.add(a);
		}
		return out;
	}
	
	
	
}
