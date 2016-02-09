package net.thecobix.openwsk.arena;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.thecobix.openwsk.events.ArenaStateChangedEvent;
import net.thecobix.openwsk.events.PlayerJoinArenaEvent;
import net.thecobix.openwsk.events.PlayerLeaveArenaEvent;
import net.thecobix.openwsk.kitsystem.KitAPI;
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
public class Arena {

	private String arenaName;
	private Team[] teams = {new Team("team1"), new Team("team2")};
	private boolean isOpen = false;
	private ArrayList<String> in = new ArrayList<>();
	private WaterRemoveSystem waterRemover;
	private ArenaRepo repo;
	private ArenaState state = ArenaState.IDLE;
	private ArenaReseter reseter;
	
	public Arena(String arenaName) {
		this.arenaName = arenaName;
		this.waterRemover = new WaterRemoveSystem(this);
		
		this.repo = new ArenaRepo(this);
		this.reseter = new ArenaReseter(this);
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
	
	public Team getTeam1() {
		return teams[0];
	}
	
	public Team getTeam2() {
		return teams[1];
	}
	
	public WaterRemoveSystem getWaterRemover() {
		return waterRemover;
	}

	public String getName() {
		return arenaName;
	}
	
	public String getArenaName() {
		return arenaName;
	}
	
	public ArrayList<String> getIn() {
		return in;
	}
	
	public ArenaRepo getRepo() {
		return repo;
	}
	
	public Team[] getTeams() {
		return teams;
	}
	
	public void giveAllPlayersDefaultKit() {
		if(repo.isEssentialsKitEnabled()) {
			KitAPI api = new KitAPI();
			if(api.existsKit(repo.getEssentialsKitName())) {
				for(Team t : teams) {
					for(TeamPlayer tp : t.getTeamMembers()) {
						Player p = Bukkit.getPlayerExact(tp.getPlayerName());
						api.giveKit(repo.getEssentialsKitName(), p);
					}
				}
			} else {
				giveDefaultKitWithoutEssentials();
			}
		} else {
			giveDefaultKitWithoutEssentials();
		}
	}
	
	private void giveDefaultKitWithoutEssentials() {
		//TODO
	}
	
	public void broadcastInside(String msg) {
		for(String i : in) {
			Player p = Bukkit.getPlayerExact(i);
			if(p == null) {
				in.remove(i);
				continue;
			}
			p.sendMessage(msg);
		}
	}
	
	public void broacastOutside(String msg) {
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(in.contains(p.getName())) {
				continue;
			}
			p.sendMessage(msg);
			
		}
	}
	
	public boolean load() {
		if(this.repo.load()) {
			this.waterRemover = new WaterRemoveSystem(this);
			this.setOpen(false);
			return true;
		}
		return false;
	}
	
	public void unload() {
		HandlerList.unregisterAll(this.waterRemover);
		this.waterRemover.stop();
		this.setOpen(false);
		
	}
	
	public void open() {
		this.setOpen(true);
		this.waterRemover.start();
		this.broadcastInside(OpenWSK.S_PREFIX+"§aDie Arena wurde freigegeben!");
	}

	public void close() {
		this.setOpen(false);
		this.waterRemover.stop();
		this.broadcastInside(OpenWSK.S_PREFIX+"§cDie Arena wurde gesperrt!");
	}
	
	public void setOpen(boolean open) {
		this.isOpen = open;
		com.sk89q.worldguard.protection.flags.StateFlag.State value = isOpen ? com.sk89q.worldguard.protection.flags.StateFlag.State.ALLOW : com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
		setOpeningFlags(this.repo.getTeam1Region(), value);
		setOpeningFlags(this.repo.getTeam2Region(), value);
		setInnerRegionFlags(this.repo.getInnerRegion(), value);
	}
	
	private void setInnerRegionFlags(ProtectedRegion region, com.sk89q.worldguard.protection.flags.StateFlag.State value) {
	    com.sk89q.worldguard.protection.flags.StateFlag.State forcedValue = com.sk89q.worldguard.protection.flags.StateFlag.State.DENY;
	    region.setFlag(DefaultFlag.TNT, value);
	    region.setFlag(DefaultFlag.PVP, value);
	    region.setFlag(DefaultFlag.FIRE_SPREAD, forcedValue);
	    region.setFlag(DefaultFlag.GHAST_FIREBALL, forcedValue);
	    region.setFlag(DefaultFlag.BUILD, forcedValue);
	}
	
	private void setOpeningFlags(ProtectedRegion region, com.sk89q.worldguard.protection.flags.StateFlag.State value) {
		region.setFlag(DefaultFlag.TNT, value);
		region.setFlag(DefaultFlag.BUILD, value);
		region.setFlag(DefaultFlag.PVP, value);
		region.setFlag(DefaultFlag.FIRE_SPREAD, value);
		region.setFlag(DefaultFlag.GHAST_FIREBALL, value);
	}

	public CuboidRegion getPlayGroundRegion() {
		ProtectedRegion innerRegion = this.repo.getInnerRegion();
		return new CuboidRegion(innerRegion.getMinimumPoint(), innerRegion.getMaximumPoint());
	}
	
	public void setState(ArenaState state) {
		Bukkit.getPluginManager().callEvent(new ArenaStateChangedEvent(this, state, this.state));
		this.state = state;
	}
	
	public ArenaState getState() {
		return state;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
}
