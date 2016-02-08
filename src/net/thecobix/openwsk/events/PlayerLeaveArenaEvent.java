package net.thecobix.openwsk.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.thecobix.openwsk.arena.Arena;

public class PlayerLeaveArenaEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Arena arena;
	private Player player;
	
	public PlayerLeaveArenaEvent(Arena arena, Player player) {
		this.arena = arena;
		this.player = player;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public Arena getArena() {
		return arena;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
