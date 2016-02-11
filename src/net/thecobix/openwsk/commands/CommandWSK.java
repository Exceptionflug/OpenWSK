package net.thecobix.openwsk.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.pro_crafting.commandframework.Command;
import de.pro_crafting.commandframework.CommandArgs;
import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.events.FightQuitEvent;
import net.thecobix.openwsk.fight.Fight;
import net.thecobix.openwsk.fight.FightManager;
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
public class CommandWSK {

	@Command(name="wsk", description="Zeigt Informationen über das Plugin an.", usage="/wsk")
	public void wskCommand(CommandArgs args) {
		if(!args.isPlayer()) {
			return;
		}
		Player p = args.getPlayer();
		p.sendMessage(OpenWSK.S_PREFIX+"OpenWSK WarShip Fightsystem by St0n3gr1d / MrCreeperkopf");
		p.sendMessage(OpenWSK.S_PREFIX+"Version: "+OpenWSK.S_VERSION);
		p.sendMessage(OpenWSK.S_PREFIX+"Um eine Liste mit Befehlen zu erhalten, gebe §6/wsk help §7ein.");
	}
	
	@Command(name="wsk.help", description="Zeigt eine Liste mit Befehlen.", usage="/wsk help")
	public void wskHelpCommand(CommandArgs args) {
		if(!args.isPlayer()) {
			return;
		}
		Player p = args.getPlayer();
		int permlevel = getHelpPermissionLevel(p);
		if(permlevel == 0) {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
			p.sendMessage("§8/wsk credits - §6Mitwirkende und Danksagungen.");
		} else if(permlevel == 1) {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
			p.sendMessage("§8/wsk arena - §6Zeigt eine Liste mit Arenabefehlen.");
			p.sendMessage("§8/wsk quit [§cteam1§8, §9team2§8] - §6Beendet einen Kampf");
			p.sendMessage("§8/wsk credits - §6Mitwirkende und Danksagungen.");
		} else {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
			p.sendMessage("§8/wsk arena - §6Zeigt eine Liste mit Arenabefehlen.");
			p.sendMessage("§8/wsk quit [§cteam1§8, §9team2§8] - §6Beendet einen Kampf");
			p.sendMessage("§8/wsk reload - §6Reloaded das Plugin.");
			p.sendMessage("§8/wsk credits - §6Mitwirkende und Danksagungen.");
		}
	}
	
	@Command(name="wsk.reload", description="Reloaded das Plugin.", usage="/wsk reload", permission="wsk.reload")
	public void onReload(CommandArgs args) {
		if(!args.isPlayer()) {
			return;
		}
		OpenWSK.getPluginInstance().getConfigHelper().registeredArenaStringList.clear();
		OpenWSK.getPluginInstance().getConfigHelper().arenaConfigs.clear();
		Bukkit.getPluginManager().disablePlugin(OpenWSK.getPluginInstance());
		OpenWSK.getPluginInstance().getArenaManager().loadedArenas.clear();
		Bukkit.getPluginManager().enablePlugin(OpenWSK.getPluginInstance());
		args.getPlayer().sendMessage(OpenWSK.S_PREFIX+"§aDas Plugin wurde neu geladen!");
	}
	
	@Command(name="wsk.credits", description="Mitwirkende und Danksagungen.", usage="/wsk credits")
	public void credits(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			p.sendMessage("§b--- §aMitwirkende §b---");
			p.sendMessage("§7Programmierung - §6St0n3gr1d / MrCreeperkopf");
			p.sendMessage("§7Test - §6Sidaonyx, St0n3gr1d / MrCreeperkopf");
			p.sendMessage("");
			p.sendMessage("§b--- §aDanksagungen §b---");
			p.sendMessage("§7Danke an Postremus für das CommandFramework sowie die Commons und den BlockGenerator.");
			p.sendMessage("§7Weiterer Dank gebührt dem MyPlayPlanet Admin-Team für das WGK System.");
		}
	}
	
	@Command(name="wsk.quit", description="Beendet einen Kampf.", usage="/wsk quit [team1,team2]", permission="wsk.quit")
	public void quit(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena.");
				return;
			}
			Fight f = null;
			for(Fight fi : FightManager.fights) {
				if(fi.getArena().getArenaName().equals(a.getArenaName())) {
					f = fi;
				}
			}
			if(f == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cHier läuft kein Kampf.");
				return;
			}
			if(a.getState() != ArenaState.RUNNING) {
				p.sendMessage(OpenWSK.S_PREFIX+"Hier läuft kein Kampf.");
				return;
			}
			if(args.getArgs().length == 0) {
				Bukkit.getPluginManager().callEvent(new FightQuitEvent(f, "§6Kampf beendet - Unentschieden"));
			} else {
				if(args.getArgs(0).equalsIgnoreCase("team1")) {
					Bukkit.getPluginManager().callEvent(new FightQuitEvent(f, "§cKampf beendet - Team1 gewinnt (Rot)"));
				} else if(args.getArgs(0).equalsIgnoreCase("team2")) {
					Bukkit.getPluginManager().callEvent(new FightQuitEvent(f, "§9Kampf beendet - Team2 gewinnt (Blau)"));
				} else {
					p.sendMessage(OpenWSK.S_PREFIX+"§bBenutzung: /wsk quit [team1,team2]");
				}
			}
		}
	}
	
	private int getHelpPermissionLevel(Player p) {
		if(p.hasPermission("wsk.mod.help")) {
			return 1;
		} else if(p.hasPermission("wsk.admin.help")) {
			return 2;
		} else {
			return 0;
		}
	}
	
}
