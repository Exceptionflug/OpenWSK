package net.thecobix.openwsk.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.thecobix.openwsk.configuration.ArenaConfig;
import net.thecobix.openwsk.configuration.ConfigHelper;
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
public class ArenaRepo {

	private ArenaConfig arenaConfig;
	private Arena arena;
	
	private String world;
	private ProtectedRegion arenaRegion;
	private int waterLevel;
	private boolean autoReset;
	private ProtectedRegion team1Region;
	private ProtectedRegion team2Region;
	private Location team1Warp;
	private Location team2Warp;
	private Location spectatorWarp;
	private boolean enteringAllowed = false;
	private int waterLevelDamage = 1;
	private ProtectedRegion innerRegion;
	private boolean essentialsKit;
	private String essentialsKitName;
	public int timeLeftMinutes = 60;
	
	public ArenaRepo(Arena a) {
		this.arena = a;
		for(ArenaConfig ac : ConfigHelper.arenaConfigs) {
			if(ac.name.equalsIgnoreCase(a.getName())) {
				this.arenaConfig = ac;
			}
		}
		if(arenaConfig == null) {
			OpenWSK.Logger.log("Arena", "Problem: Arena "+arena.getName()+" coud not find ArenaConfig", 2);
			
		}
	}
	
	public boolean load() {
		if(!loadWorld()) return false;
		if(!loadArenaRegion()) return false;
		if(!loadInnerRegion()) return false;
		if(!loadWaterLevel()) return false;
		if(!loadAutoReset()) return false;
		if(!loadTeam1Region()) return false;
		if(!loadTeam2Region()) return false;
		if(!loadTeam1Warp()) return false;
		if(!loadTeam2Warp()) return false;
		if(!loadSpectatorWarp()) return false;
		if(!loadEssentialsKit()) return false;
		return true;
	}
	
	private boolean loadWorld() {
		try{
			String worldName = this.arenaConfig.cfg.getString("Arena.location.world");
			if(!existsWorld(worldName)) {
				return false;
			}
			this.world = worldName;
			return true;
		}catch(Exception e) {
			return false;
		}
	}
	
	private boolean existsWorld(String name) {
		if(name == null) return false;
		return OpenWSK.getPluginInstance().getServer().getWorld(name) != null;
	}
	
	private boolean loadArenaRegion() {
		String name = this.arenaConfig.cfg.getString("Arena.region.wholearena");
		this.arenaRegion = getWorldGuardRegion(name);
		return this.arenaRegion != null;
	}
	
	private ProtectedRegion getWorldGuardRegion(String name) {
		RegionManager rm = OpenWSK.getPluginInstance().getRepo().getWorldGuard().getRegionManager(this.getWorld());
		return rm.getRegion(name);
	}
	
	World getWorld() {
		return Bukkit.getWorld(world);
	}
	
	private boolean loadInnerRegion() {
		String name = this.arenaConfig.cfg.getString("Arena.region.inner");
		this.innerRegion = this.getWorldGuardRegion(name);
		return this.innerRegion != null;
	}
	
	private boolean loadWaterLevel() {
		int waterLevel = this.arenaConfig.cfg.getInt("Arena.location.waterlevel");
		this.waterLevel = waterLevel;
		return true;
	}
	
	private boolean loadAutoReset() {
		this.autoReset = this.arenaConfig.cfg.getBoolean("Arena.autoreset", true);
		return true;
	}
	
	private boolean loadTeam1Region() {
		String name = this.arenaConfig.cfg.getString("Arena.region.team1");
		this.team1Region = this.getWorldGuardRegion(name);
		return this.team1Region != null;
	}

	private boolean loadTeam2Region() {
		String name = this.arenaConfig.cfg.getString("Arena.region.team2");
		this.team2Region = this.getWorldGuardRegion(name);
		return this.team2Region != null;
	}
	
	private Location loadLocation(double x, double y, double z) {
		return new Location(getWorld(), x, y, z);
	}
	
	private boolean loadTeam1Warp() {
		this.team1Warp = this.loadLocation(this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team1.x"),
				this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team1.y"),
				this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team1.z"));
		return this.team1Warp != null;
	}
	
	private boolean loadTeam2Warp() {
		this.team2Warp = this.loadLocation(this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team2.x"),
				this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team2.y"),
				this.arenaConfig.cfg.getDouble("Arena.location.fightstart.team2.z"));
		return this.team2Warp != null;
	}
	
	private boolean loadSpectatorWarp() {
		this.spectatorWarp = this.loadLocation(this.arenaConfig.cfg.getDouble("Arena.location.spectatorspawn.x"),
				this.arenaConfig.cfg.getDouble("Arena.location.spectatorspawn.y"),
				this.arenaConfig.cfg.getDouble("Arena.location.spectatorspawn.z"));
		return this.spectatorWarp != null;
	}
	
	private boolean loadEssentialsKit() {
		this.essentialsKit = this.arenaConfig.cfg.getBoolean("Arena.kit.essentials");
		this.essentialsKitName = this.arenaConfig.cfg.getString("Arena.kit.name");
		return true;
	}
	
	public void setWorld(String world) {
		if(this.existsWorld(world)) {
			this.world = world;
		}
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public ArenaConfig getArenaConfig() {
		return arenaConfig;
	}
	
	public ProtectedRegion getArenaRegion() {
		return arenaRegion;
	}
	
	public ProtectedRegion getInnerRegion() {
		return innerRegion;
	}
	
	public Location getSpectatorWarp() {
		return spectatorWarp;
	}
	
	public ProtectedRegion getTeam1Region() {
		return team1Region;
	}
	
	public Location getTeam1Warp() {
		return team1Warp;
	}
	
	public ProtectedRegion getTeam2Region() {
		return team2Region;
	}
	
	public Location getTeam2Warp() {
		return team2Warp;
	}
	
	public int getWaterLevel() {
		return waterLevel;
	}
	
	public int getWaterLevelDamage() {
		return waterLevelDamage;
	}
	
	public void setTeam1Region(ProtectedRegion team1Region) {
		this.team1Region = team1Region;
	}
	
	public void setEnteringAllowed(boolean enteringAllowed) {
		this.enteringAllowed = enteringAllowed;
	}
	
	public void setArenaRegion(ProtectedRegion arenaRegion) {
		this.arenaRegion = arenaRegion;
	}
	
	public void setAutoReset(boolean autoReset) {
		this.autoReset = autoReset;
	}
	
	public void setInnerRegion(ProtectedRegion innerRegion) {
		this.innerRegion = innerRegion;
	}
	
	public void setSpectatorWarp(Location spectatorWarp) {
		this.spectatorWarp = spectatorWarp;
	}
	
	public void setTeam1Warp(Location team1Warp) {
		this.team1Warp = team1Warp;
	}
	
	public void setTeam2Region(ProtectedRegion team2Region) {
		this.team2Region = team2Region;
	}
	
	public void setTeam2Warp(Location team2Warp) {
		this.team2Warp = team2Warp;
	}
	
	public void setWaterLevel(int waterLevel) {
		this.waterLevel = waterLevel;
	}
	
	public void setWaterLevelDamage(int waterLevelDamage) {
		this.waterLevelDamage = waterLevelDamage;
	}
	
	public boolean shouldAutoReset() {
		return autoReset;
	}
	
	public boolean isEnteringAllowed() {
		return enteringAllowed;
	}
	
	public boolean isEssentialsKitEnabled() {
		return essentialsKit;
	}
	
	public String getEssentialsKitName() {
		return essentialsKitName;
	}
	
}
