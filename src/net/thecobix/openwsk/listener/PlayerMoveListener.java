package net.thecobix.openwsk.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;

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
public class PlayerMoveListener implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		for(Arena a : OpenWSK.getPluginInstance().getArenaManager().loadedArenas) {
			if(OpenWSK.getPluginInstance().getArenaManager().isInArena(e.getPlayer(), a)) {
				if(!a.getIn().contains(e.getPlayer().getName())) {
					a.join(e.getPlayer().getName());
				}
			} else {
				if(a.getIn().contains(e.getPlayer().getName())) {
					a.leave(e.getPlayer().getName());
				}
			}
		}
		Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(e.getPlayer());
		if(t == null) {
			return;
		}
		if(t.getTeamName().equals("team1")) {
			if(t.getArena().getRepo().getTeam1Region().contains((int)e.getFrom().getX(), (int)e.getFrom().getY(), (int)e.getFrom().getZ())) {
				if(t.getArena().getRepo().getTeam2Region().contains((int)e.getTo().getX(), (int)e.getTo().getY(), (int)e.getTo().getZ())) {
					if(t.getArena().getRepo().isEnteringAllowed() || e.getPlayer().hasPermission("wsk.bypass")) {
						
					} else {
						e.getPlayer().teleport(e.getFrom());
					}
				} else {
					if(t.getArena().getRepo().getTeam1Region().contains((int)e.getTo().getX(), (int)e.getTo().getY(), (int)e.getTo().getZ()) || e.getPlayer().hasPermission("wsk.bypass")) {
						
					} else {
						e.getPlayer().teleport(e.getFrom());
					}
				}
			}
		}
		if(t.getTeamName().equals("team2")) {
			if(t.getArena().getRepo().getTeam2Region().contains((int)e.getFrom().getX(), (int)e.getFrom().getY(), (int)e.getFrom().getZ())) {
				if(t.getArena().getRepo().getTeam1Region().contains((int)e.getTo().getX(), (int)e.getTo().getY(), (int)e.getTo().getZ())) {
					if(t.getArena().getRepo().isEnteringAllowed() || e.getPlayer().hasPermission("wsk.bypass")) {
						
					} else {
						e.getPlayer().teleport(e.getFrom());
					}
				} else {
					if(t.getArena().getRepo().getTeam2Region().contains((int)e.getTo().getX(), (int)e.getTo().getY(), (int)e.getTo().getZ()) || e.getPlayer().hasPermission("wsk.bypass")) {
						
					} else {
						e.getPlayer().teleport(e.getFrom());
					}
				}
			}
		}
		
	}
	
}
