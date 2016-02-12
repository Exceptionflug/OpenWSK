package net.thecobix.openwsk.fight;

import org.bukkit.Bukkit;

import net.thecobix.openwsk.events.FightQuitEvent;

public class FightTimer implements Runnable {

	private Fight fight;
	public int time;
	
	public FightTimer(Fight fight) {
		this.fight = fight;
		this.time = fight.getArena().getRepo().timeLeftMinutes;
	}
	
	public void run() {
		
		if(this.time == 0) {
			Bukkit.getPluginManager().callEvent(new FightQuitEvent(fight, "Zeit abgelaufen - Unentschieden", null, null));
		}
		this.time -= 1;
		fight.getArena().getScoreboard().updateTime(this.time);
	}
	
}
