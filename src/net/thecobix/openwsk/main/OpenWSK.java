package net.thecobix.openwsk.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.pro_crafting.commandframework.CommandArgs;
import de.pro_crafting.commandframework.CommandFramework;
import de.pro_crafting.commandframework.Completer;
import net.thecobix.openwsk.commands.CommandWSK;

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

	private CommandFramework cmdFramework;
	private static OpenWSK pluginInstance;
	
	public static final String S_PREFIX = "§8[§bWSK§8] §7";
	public static final String S_NONCOLOR_PREFIX = "[WSK] ";
	public static String S_VERSION;
	public static final String S_CODENAME = "";
	
	
	@Override
	public void onEnable() {
		pluginInstance = this;
		System.out.println("----------------------------------------");
		Logger.log("Startup", "OpenWSK v"+this.getDescription().getVersion()+" by St0n3gr1d / MrCreeperkopf", 0);
		Logger.log("Startup", "This plugin is using the command framework by Postremus. Visit https://github.com/Postremus/CommandFramework for further information.", 0);
		String codename = S_CODENAME.isEmpty() ? "" : "§8(§c"+S_CODENAME+"§8)";
		S_VERSION = "§6"+this.getDescription().getVersion()+" "+codename;
		
		/* >>> The Framework */
		this.cmdFramework = new CommandFramework(this);
		this.cmdFramework.registerCommands(new CommandWSK());
		
		
		Method[] methods = this.getClass().getMethods();
		for(Method m : methods) {
			if(m.getName().equalsIgnoreCase("completeCommands")) {
				this.cmdFramework.registerCompleter("wsk", m, this);
			}
		}
		this.cmdFramework.registerHelp();
		this.cmdFramework.setInGameOnlyMessage(S_PREFIX+"§cNur ein Spieler kann diesen Befehl ausführen!");
		/* The Framework <<< */
		
		
		//TODO Local Configs and MySQL integration
	}
	
	@Completer(name="wsk")
	public List<String> completeCommands(CommandArgs args) {
		List<String> ret = new ArrayList<String>();
		String label = args.getCommand().getLabel();
		for (String arg : args.getArgs()) {
			label += " " + arg;
		}
		for(String currentLabel : this.cmdFramework.getCommandLabels()) {
			if(currentLabel.contains("nWSK")) {
				continue;
			}
			String current = currentLabel.replace('.', ' ');
			if (current.contains(label)) {
				current = current.substring(label.lastIndexOf(' ')).trim();
				current = current.substring(0, current.indexOf(' ') != -1 ? current.indexOf(' ') : current.length()).trim();
				if (!ret.contains(current)) {
					ret.add(current);
				}
			}
		}
		return ret;
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
	
	/**
	 * Returns the instance of the main class
	 * @return
	 */
	public static OpenWSK getPluginInstance() {
		return pluginInstance;
	}
	
}
