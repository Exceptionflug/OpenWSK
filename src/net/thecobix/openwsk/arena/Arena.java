package net.thecobix.openwsk.arena;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import net.thecobix.openwsk.events.PlayerJoinArenaEvent;
import net.thecobix.openwsk.events.PlayerLeaveArenaEvent;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;

public class Arena {

	private String arenaName;
	private Team[] teams = {new Team("team1"), new Team("team2")};
	private boolean isOpen = false;
	private ArrayList<String> in = new ArrayList<>();
	
	public Arena(String arenaName) {
		this.arenaName = arenaName;
	}
	
	public void join(String name) {
		in.add(name);
		OpenWSK.getPluginInstance().getServer().getPluginManager().callEvent(new PlayerJoinArenaEvent(this, Bukkit.getPlayerExact(name)));
	}
	
	public boolean leave(String name) {
		try{
			in.remove(name);
			OpenWSK.getPluginInstance().getServer().getPluginManager().callEvent(new PlayerLeaveArenaEvent(this, Bukkit.getPlayerExact(name)));
			return true;
		}catch(Exception e) {
			return false;
		}
	}
}
