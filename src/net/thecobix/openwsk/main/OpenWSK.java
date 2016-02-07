package net.thecobix.openwsk.main;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

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
