package net.thecobix.openwsk.fight;

import org.bukkit.Bukkit;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.main.OpenWSK;

public class Fight {

	private Arena arena;
	private FightTimer timer;
	private int task;
	
	public Fight(Arena arena) {
		this.arena = arena;
		this.timer = new FightTimer(this);
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public FightTimer getTimer() {
		return timer;
	}
	
	public void startFight() {
		arena.open();
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(OpenWSK.getPluginInstance(), timer, 0L, 1200L);
		
	}
	
	public boolean isTimerRunning() {
		return Bukkit.getScheduler().isCurrentlyRunning(task);
	}
	
	public void stopTimer() {
		Bukkit.getScheduler().cancelTask(task);
	}
	
}
