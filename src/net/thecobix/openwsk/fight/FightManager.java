package net.thecobix.openwsk.fight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

public class FightManager {

	public static List<Fight> fights = new ArrayList<>();
	
	public static void spectate(Fight f) {
		for(Team t : f.getArena().getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				Player z = Bukkit.getPlayerExact(tp.getPlayerName());
				if(z == null) {
					continue;
				}
				z.setGameMode(GameMode.SPECTATOR);
				Location warp = t.getTeamName().equals("team1") ? f.getArena().getRepo().getTeam1Warp() : f.getArena().getRepo().getTeam2Warp();
				z.teleport(warp);
				z.getInventory().clear();
			}
		}
		f.getArena().broadcastInside(OpenWSK.S_PREFIX+"§6Die Zuschauerphase ist in 60 Sekunden vorrüber.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(OpenWSK.getPluginInstance(), new Runnable() {
			public void run() {
				
				for(Team t : f.getArena().getTeams()) {
					for(TeamPlayer tp : t.getTeamMembers()) {
						Player z = Bukkit.getPlayerExact(tp.getPlayerName());
						if(z == null) {
							continue;
						}
						z.setGameMode(GameMode.SURVIVAL);
						z.teleport(f.getArena().getRepo().getSpectatorWarp());
						z.getInventory().clear();
						f.getArena().setState(ArenaState.RESET);
						FightManager.fights.remove(f);
					}
				}
				
			}
		}, 1200L);
	}
	
	public static void respawnAsSpectator(Player p, Arena a) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(OpenWSK.getPluginInstance(), new Runnable() {
			public void run() {
				
				for(Team t : a.getTeams()) {
					for(TeamPlayer tp : t.getTeamMembers()) {
						if(tp.getPlayerName().equals(p.getName())) {
							if(t.getTeamName().equals("team1")) {
								p.teleport(a.getRepo().getTeam1Warp());
								p.getInventory().clear();
								p.setGameMode(GameMode.SPECTATOR);
							} else {
								p.teleport(a.getRepo().getTeam2Warp());
								p.getInventory().clear();
								p.setGameMode(GameMode.SPECTATOR);
							}
						}
					}
				}
				
			}
		}, 35L);
	}
	
}
