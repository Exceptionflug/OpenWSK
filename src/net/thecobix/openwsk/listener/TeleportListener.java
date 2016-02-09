package net.thecobix.openwsk.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

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
public class TeleportListener implements Listener {

	@EventHandler
	public void onTp(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		Arena old = null;
		for(Arena a : OpenWSK.getPluginInstance().getArenaManager().loadedArenas) {
			if(OpenWSK.getPluginInstance().getArenaManager().isLocIn(e.getFrom(), a)) {
				old = a;
			}
		}
		Arena newArena = null;
		for(Arena a : OpenWSK.getPluginInstance().getArenaManager().loadedArenas) {
			if(OpenWSK.getPluginInstance().getArenaManager().isInArena(p, a)) {
				newArena = a;
			}
		}
		if(old != null) {
			if(newArena != null) {
				if(!old.getArenaName().equals(newArena.getArenaName())) {
					old.leave(p.getName());
					newArena.join(p.getName());
				} else {
					return;
				}
			} else {
				old.leave(p.getName());
			}
		} else {
			if(newArena != null) {
				newArena.join(p.getName());
			} else {
				return;
			}
		}
	}
	
}
