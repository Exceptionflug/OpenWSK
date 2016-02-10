package net.thecobix.openwsk.team;

import org.bukkit.entity.Player;

import net.thecobix.openwsk.arena.Arena;

public class TeamManager {

	private Arena arena;
	
	public TeamManager(Arena a) {
		this.arena = a;
	}
	
	public boolean isPlayerAlive(Player p) {
		for(Team t : arena.getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				if(tp.getPlayerName().equals(p.getName())) {
					if(tp.getRole() == PlayerRole.DIED) {
						return false;
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}
	
}
