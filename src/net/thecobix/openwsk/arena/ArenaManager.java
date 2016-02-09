package net.thecobix.openwsk.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

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
public class ArenaManager {

	public List<Arena> loadedArenas = new ArrayList<>();
	
	public Arena getArenaFromPlayer(Player p) {
		for(Arena a : loadedArenas) {
			if(a.getRepo().getArenaRegion().contains((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ())) {
				if(!a.getIn().contains(p.getName())) {
					a.join(p.getName());
				}
				return a;
			} else {
				if(a.getIn().contains(p.getName())) {
					a.leave(p.getName());
				}
			}
		}
		return null;
	}
	
	public boolean isInArena(Player p, Arena a) {
		return a.getRepo().getArenaRegion().contains((int)p.getLocation().getX(), (int)p.getLocation().getY(), (int)p.getLocation().getZ());
	}
	
	public boolean isLocIn(Location loc, Arena a) {
		return a.getRepo().getArenaRegion().contains((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
	}
	
	public Team getTeamFromPlayer(Player p) {
		Arena a = getArenaFromPlayer(p);
		if(a == null) {
			return null;
		}
		for(Team t : a.getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				if(tp.getPlayerName().equals(p.getName())) {
					return t;
				}
			}
		}
		return null;
	}
	
	public void unloadArena(Arena a) {
		loadedArenas.remove(a);
		a.unload();
	}
	
	public void loadArena(Arena a) {
		if(!a.load()) {
			OpenWSK.Logger.log("Arena", "Failed to load "+a.getArenaName()+". Make sure it is configurated rightly.", 2);
		} else {
			loadedArenas.add(a);
		}
	}
	
}
