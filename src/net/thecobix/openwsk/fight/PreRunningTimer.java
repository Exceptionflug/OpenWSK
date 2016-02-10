package net.thecobix.openwsk.fight;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

public class PreRunningTimer {

	private Arena arena;
	private int time = 61;
	
	public PreRunningTimer(Arena arena) {
		this.arena = arena;
	}
	
	public void preRunningTimer() {
		List<String> sucken = new ArrayList<>();
		for(TeamPlayer tb : arena.getTeam1().getTeamMembers()) {
			sucken.add(tb.getPlayerName());
		}
		List<String> sucken2 = new ArrayList<>();
		for(TeamPlayer tb : arena.getTeam2().getTeamMembers()) {
			sucken2.add(tb.getPlayerName());
		}
		arena.broacastOutside(OpenWSK.S_PREFIX+"Jeden Moment beginnt eine epische Schlacht in Arena �b"+arena.getArenaName()+"�7.");
		arena.broadcastInside("�c[Team1] "+sucken.toString().replace("[", "").replace("]", ""));
		arena.broadcastInside("�eVersus");
		arena.broadcastInside("�9[Team2] "+sucken2.toString().replace("[", "").replace("]", ""));
		arena.tpToTeamWarp();
		int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(OpenWSK.getPluginInstance(), new Runnable() {

			@Override
			public void run() {
				
				time --;
				switch (time) {
				case 60:
					arena.broadcastInside("�660 Sekunden bis die Schlacht beginnt.");
					break;

				case 30:
					arena.broadcastInside("�630 Sekunden");
					break;
					
				case 10:
					arena.broadcastInside("�610 Sekunden");
					break;
					
				case 5:
					arena.broadcastInside("�65 Sekunden");
					break;
					
				case 4:
					arena.broadcastInside("�64 Sekunden");
					break;
					
				case 3:
					arena.broadcastInside("�63 Sekunden");
					break;
					
				case 2:
					arena.broadcastInside("�62 Sekunden");
					break;
					
				case 1:
					arena.broadcastInside("�61 Sekunde");
					break;
					
				case 0:
					arena.setState(ArenaState.RUNNING);
					arena.broadcastInside("�6Das Entern wird in 60 Sekunden erlaubt.");
					break;
					
				case -30:
					arena.broadcastInside("�6Das Entern wird in 30 Sekunden erlaubt.");
					break;
					
				case -50:
					arena.broadcastInside("�6Das Entern wird in 10 Sekunden erlaubt.");
					break;
					
				case -51:
					arena.broadcastInside("�69 Sekunden");
					break;
					
				case -52:
					arena.broadcastInside("�68 Sekunden");
					break;
					
				case -53:
					arena.broadcastInside("�67 Sekunden");
					break;
					
				case -54:
					arena.broadcastInside("�66 Sekunden");
					break;
					
				case -55:
					arena.broadcastInside("�65 Sekunden");
					break;
					
				case -56:
					arena.broadcastInside("�64 Sekunden");
					break;
					
				case -57:
					arena.broadcastInside("�63 Sekunden");
					break;
					
				case -58:
					arena.broadcastInside("�62 Sekunden");
					break;
					
				case -59:
					arena.broadcastInside("�61 Sekunde");
					break;
					
				case -60:
					arena.broadcastInside("�aDas Entern ist nun erlaubt");
					arena.getRepo().setEnteringAllowed(true);
					break;
					
				default:
					break;
				}
				
			}
			
		}, 20, 20);
	}
	
}