package net.thecobix.openwsk.main;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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
public class OpenWSK extends JavaPlugin {

	
	@Override
	public void onEnable() {
		System.out.println("----------------------------------------");
		Logger.log("Startup", "OpenWSK v"+this.getDescription().getVersion()+" by St0n3gr1d / MrCreeperkopf", 0);
		//TODO Local Configs and MySQL integration
	}
	
	
	@Override
	public void onDisable() {
		Logger.log("Shutdown", "Disabling OpenWSK", 0);
		//TODO Close MySQL if necessary
	}
	
	public static class Logger {
		
		public static void log(String prefix, String msg, int type) {
			switch (type) {
			case 0:
				Bukkit.getLogger().log(Level.INFO, "[WSK | "+prefix+"] "+msg);
				break;

			case 1:
				Bukkit.getLogger().log(Level.WARNING, "[WSK | "+prefix+"] "+msg);
				break;
				
			case 2:
				Bukkit.getLogger().log(Level.SEVERE, "[WSK | "+prefix+"] "+msg);
				break;
				
			default:
				log("Logger", "Unknown type "+type, 2);
				break;
			}
		}
		
	}
}
