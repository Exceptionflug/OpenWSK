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
import de.pro_crafting.common.scoreboard.ScoreboardManager;
import de.pro_crafting.generator.BlockGenerator;
import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaManager;
import net.thecobix.openwsk.commands.CommandArena;
import net.thecobix.openwsk.commands.CommandTeam;
import net.thecobix.openwsk.commands.CommandWSK;
import net.thecobix.openwsk.configuration.ConfigHelper;
import net.thecobix.openwsk.invitation.InvitationSystem;
import net.thecobix.openwsk.listener.ArenaListener;
import net.thecobix.openwsk.listener.ChatListener;
import net.thecobix.openwsk.listener.ConnectionStateChangedListener;
import net.thecobix.openwsk.listener.FightListener;
import net.thecobix.openwsk.listener.InventoryListener;
import net.thecobix.openwsk.listener.PlayerMoveListener;
import net.thecobix.openwsk.listener.TeleportListener;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.util.Repository;

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
	private Repository theRepo = new Repository();
	private ArenaManager arenaManager;
	private ConfigHelper configHelper;
	private BlockGenerator generator;
	private InvitationSystem invitationSystem;
	
	private static OpenWSK pluginInstance;
	
	public static final String S_PREFIX = "§8[§bWSK§8] §7";
	public static final String S_NONCOLOR_PREFIX = "[WSK] ";
	public static String S_VERSION;
	public static final String S_CODENAME = "Beta 1";
	
	
	@Override
	public void onEnable() {
		pluginInstance = this;
		System.out.println("------------------------------------------");
		Logger.log("Startup", "OpenWSK v"+this.getDescription().getVersion()+" by St0n3gr1d / MrCreeperkopf", 0);
		Logger.log("Startup", "This plugin is using the command framework by Postremus. Visit https://github.com/Postremus/CommandFramework for further information.", 0);
		Logger.log("Startup", "This plugin is using the commons and the block generator by pro_crafting. Visit https://github.com/Postremus/ for further information.", 0);
		String codename = S_CODENAME.isEmpty() ? "" : "§8(§c"+S_CODENAME+"§8)";
		generator = new BlockGenerator(this, 30000);
		invitationSystem = new InvitationSystem();
		S_VERSION = "§6"+this.getDescription().getVersion()+" "+codename;
		
		/* >>> The Framework */
		this.cmdFramework = new CommandFramework(this);
		this.cmdFramework.registerCommands(new CommandWSK());
		this.cmdFramework.registerCommands(new CommandArena());
		this.cmdFramework.registerCommands(new CommandTeam());
		
		Method[] methods = this.getClass().getMethods();
		for(Method m : methods) {
			if(m.getName().equalsIgnoreCase("completeCommands")) {
				this.cmdFramework.registerCompleter("wsk", m, this);
			}
		}
		this.cmdFramework.registerHelp();
		this.cmdFramework.setInGameOnlyMessage(S_PREFIX+"§cNur ein Spieler kann diesen Befehl ausführen!");
		/* The Framework <<< */
		
		
		configHelper = new ConfigHelper();
		configHelper.loadPluginConfig();
		this.arenaManager = new ArenaManager();
		configHelper.loadArenaConfig();
		for(Arena a : configHelper.buildArenas()) {
			arenaManager.loadArena(a);
		}
		
		Bukkit.getPluginManager().registerEvents(new ArenaListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new ConnectionStateChangedListener(), this);
		Bukkit.getPluginManager().registerEvents(new TeleportListener(), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new FightListener(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
		System.out.println("------------------------------------------");
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
		List<Arena> otherArenaList = new ArrayList<>();
		for(Arena a : arenaManager.loadedArenas) {
			otherArenaList.add(a);
		}
		for(Arena a : otherArenaList) {
			arenaManager.unloadArena(a);
		}
		otherArenaList.clear();
		invitationSystem.stopTimer();
		invitationSystem.invitations.clear();
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
	 * @return OpenWSK
	 */
	public static OpenWSK getPluginInstance() {
		return pluginInstance;
	}
	
	/**
	 * Returns the instance of the repository class
	 * @return Repository
	 */
	public Repository getRepo() {
		return theRepo;
	}
	
	/**
	 * The ArenaManager
	 * @return ArenaManager
	 */
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	/**
	 * ConfigHelper's instance
	 * @return ConfigHelper
	 */
	public ConfigHelper getConfigHelper() {
		return configHelper;
	}
	
	public BlockGenerator getGenerator() {
		return generator;
	}
	
	public InvitationSystem getInvitationSystem() {
		return invitationSystem;

	}
	
}
