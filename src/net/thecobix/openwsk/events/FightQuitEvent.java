package net.thecobix.openwsk.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.thecobix.openwsk.fight.Fight;

public class FightQuitEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	private String reason;
	private Fight fight;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public FightQuitEvent(Fight fight, String reason) {
		this.fight = fight;
		this.reason = reason;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public Fight getFight() {
		return fight;
	}
	
	public String getReason() {
		return reason;
	}
	
}
