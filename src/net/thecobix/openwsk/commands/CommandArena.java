package net.thecobix.openwsk.commands;

import org.bukkit.entity.Player;

import de.pro_crafting.commandframework.Command;
import de.pro_crafting.commandframework.CommandArgs;
import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
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
public class CommandArena {

	@Command(name="wsk.arena", description="Zeigt die Arena Befehle an.", usage="/wsk arena", permission="wsk.arena")
	public void onArenaHelp(CommandArgs args) {
		if(!args.isPlayer()) {
			return;
		}
		args.getPlayer().sendMessage(OpenWSK.S_PREFIX+"Arena Befehle:");
		args.getPlayer().sendMessage("§8/wsk arena §6- Zeigt die Arena Befehle an.");
		args.getPlayer().sendMessage("§8/wsk arena open §6- Öffnet die Arena");
		args.getPlayer().sendMessage("§8/wsk arena close §6- Schließt die Arena");
		args.getPlayer().sendMessage("§8/wsk arena reset §6- Resettet die Arena");
		args.getPlayer().sendMessage("§8/wsk arena info §6- Zeigt dir Informationen über die aktuelle Arena an.");
	}
	
	@Command(name="wsk.arena.open", description="Öffnet die Arena", usage="/wsk arena open", permission="wsk.arena.open")
	public void openArena(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena!");
				return;
			}
			a.open();
		}
	}
	
	@Command(name="wsk.arena.close", description="Schliesst die Arena", usage="/wsk arena close", permission="wsk.arena.close")
	public void closeArena(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena!");
				return;
			}
			a.close();
		}
	}
	
	@Command(name="wsk.arena.info", description="Zeigt dir Informationen über die aktuelle Arena an.", usage="/wsk arena info", permission="wsk.arena.info")
	public void onInfo(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena.");
				return;
			}
			p.sendMessage("§b--- §6"+a.getArenaName()+" §b---");
			p.sendMessage("§bStatus: §a"+a.getState().toString());
			String state = a.isOpen() ? "§aFreigegeben" : "§cGesperrt";
			p.sendMessage("§bZustand: "+state);
			p.sendMessage("§bGröße Team1: §a"+a.getTeam1().getTeamMembers().size());
			p.sendMessage("§bGröße Team2: §a"+a.getTeam2().getTeamMembers().size());
			String ar = a.getRepo().shouldAutoReset() ? "§aAn" : "§cAus";
			p.sendMessage("§bAutoReset: §a"+ar);
			p.sendMessage("§bGesamte Arena (Region): §a"+a.getRepo().getArenaRegion().getId());
			p.sendMessage("§bKampffläche (Region): §a"+a.getRepo().getInnerRegion().getId());
			p.sendMessage("§bTeam1 (Region): §a"+a.getRepo().getTeam1Region().getId());
			p.sendMessage("§bTeam2 (Region): §a"+a.getRepo().getTeam2Region().getId());
			p.sendMessage("§bFightstart Team1 (Warp): §aX"+a.getRepo().getTeam1Warp().getX()+" Y"+a.getRepo().getTeam1Warp().getY()+" Z"+a.getRepo().getTeam1Warp().getZ());
			p.sendMessage("§bFightstart Team2 (Warp): §aX"+a.getRepo().getTeam2Warp().getX()+" Y"+a.getRepo().getTeam2Warp().getY()+" Z"+a.getRepo().getTeam2Warp().getZ());
			p.sendMessage("§bSpectator (Warp): §aX"+a.getRepo().getSpectatorWarp().getX()+" Y"+a.getRepo().getSpectatorWarp().getY()+" Z"+a.getRepo().getSpectatorWarp().getZ());
			String ent = a.getRepo().isEnteringAllowed() ? "§aErlaubt" : "§cVerboten";
			p.sendMessage("§bEntern: "+ent);
			String ek = a.getRepo().isEssentialsKitEnabled() ? "§aWird verwendet" : "§cWird nicht verwendet";
			p.sendMessage("§bEssentials Kit: "+ek);
			if(a.getRepo().isEssentialsKitEnabled()) {
				p.sendMessage("§bEssentials Kit (Name): §a"+a.getRepo().getEssentialsKitName());
			}
			p.sendMessage("§bIn der Arena: §a"+a.getIn().toString().replace("[", "").replace("]", ""));
		}
	}
	
	@Command(name="wsk.arena.reset", description="Resetet die Arena.", usage="/wsk arena reset", permission="wsk.arena.reset")
	public void onReset(CommandArgs args) {
		if(args.isPlayer()) {
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(args.getPlayer());
			if(a == null) {
				args.getPlayer().sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena.");
				return;
			}
			a.setState(ArenaState.RESET);
			args.getPlayer().sendMessage(OpenWSK.S_PREFIX+"§aDie Arena wird gleich resettet!");
		}
	}
	
}
