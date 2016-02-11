package net.thecobix.openwsk.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.sk89q.worldedit.Vector;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.events.ArenaStateChangedEvent;
import net.thecobix.openwsk.events.PlayerJoinArenaEvent;
import net.thecobix.openwsk.events.PlayerLeaveArenaEvent;
import net.thecobix.openwsk.fight.Fight;
import net.thecobix.openwsk.fight.FightManager;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

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
public class ArenaListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinArenaEvent e) {
		e.getPlayer().sendMessage(OpenWSK.S_PREFIX+"§aWillkommen in Arena §6"+e.getArena().getName());
		e.getArena().getScoreboard().enterArena(e.getPlayer());
	}
	
	@EventHandler
	public void onLeave(PlayerLeaveArenaEvent e) {
		e.getPlayer().sendMessage(OpenWSK.S_PREFIX+"§cAuf Wiedersehen in Arena §6"+e.getArena().getName());
		e.getArena().getScoreboard().leaveArena(e.getPlayer());
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onExplode(EntityExplodeEvent e) {
		Arena a = null;
		for(Arena ar : OpenWSK.getPluginInstance().getArenaManager().loadedArenas) {
			if(OpenWSK.getPluginInstance().getArenaManager().isLocIn(e.getLocation(), ar)) {
				a = ar;
			}
		}
		if(a == null) {
			return;
		}
		if(!a.getPlayGroundRegion().contains(new Vector(e.getLocation().getX(), e.getLocation().getY(), e.getLocation().getZ()))) {
			return;
		}
		for(Block b : e.blockList()) {
			if(b.getType() != Material.WATER || b.getType() != Material.STATIONARY_WATER) {
				a.getWaterRemover().add(b.getLocation());
			}
		}
	}
	
	@EventHandler
	public void stateChange(ArenaStateChangedEvent e) {
		Arena a = e.getArena();
		if(e.getOldState() == ArenaState.SETUP && e.getNewState() == ArenaState.PRERUNNING) {
			a.preparePlayerZ();
			a.giveAllPlayersDefaultKit();
			a.replace();
			for(Team t : a.getTeams()) {
				for(TeamPlayer tp : t.getTeamMembers()) {
					Player z = Bukkit.getPlayerExact(tp.getPlayerName());
					z.playSound(z.getLocation(), Sound.NOTE_PLING, 100, 1);
				}
			}
			
		} else if(e.getOldState() == ArenaState.RUNNING && e.getNewState() == ArenaState.SPECTATE) {
			for(Fight f : FightManager.fights) {
				if(f.getArena().getArenaName().equals(e.getArena().getArenaName())) {
					FightManager.spectate(f);
				}
			}
		}
	}
}
