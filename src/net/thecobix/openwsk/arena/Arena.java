package net.thecobix.openwsk.arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.pro_crafting.common.Point;
import de.pro_crafting.common.Size;
import de.pro_crafting.generator.criteria.SingleBlockCriteria;
import de.pro_crafting.generator.job.SimpleJob;
import de.pro_crafting.generator.provider.SingleBlockProvider;
import net.thecobix.openwsk.events.ArenaStateChangedEvent;
import net.thecobix.openwsk.events.PlayerJoinArenaEvent;
import net.thecobix.openwsk.events.PlayerLeaveArenaEvent;
import net.thecobix.openwsk.fight.FightScoreboard;
import net.thecobix.openwsk.kitsystem.KitAPI;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.PlayerRole;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamManager;
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
	private List<Team> teams;
	private boolean isOpen = false;
	private ArrayList<String> in = new ArrayList<>();
	private WaterRemoveSystem waterRemover;
	private ArenaRepo repo;
	private ArenaState state = ArenaState.IDLE;
	private ArenaReseter reseter;
	private TeamManager teamManager;
	private FightScoreboard scoreboard;
	
	public Arena(String arenaName) {
		this.arenaName = arenaName;
		this.waterRemover = new WaterRemoveSystem(this);
		
		this.teams = new ArrayList<>();
		this.teams.add(new Team("team1", this));
		this.teams.add(new Team("team2", this));
		this.repo = new ArenaRepo(this);
		this.setReseter(new ArenaReseter(this));
		this.teamManager = new TeamManager(this);
		this.scoreboard = new FightScoreboard(this);
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
		return teams.get(0);
	}
	
	public Team getTeam2() {
		return teams.get(1);
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
	
	public List<Team> getTeams() {
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
						giveRoleKit(tp);
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
		for(Team t : teams) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				Player p = Bukkit.getPlayerExact(tp.getPlayerName());
				p.getInventory().setArmorContents(null);
				p.getInventory().setItem(0, new ItemStack(Material.STONE_PICKAXE, 1));
				ItemStack clay = null;
				if(t.getTeamName().equals("team1")) {
					clay = new ItemStack(Material.STAINED_CLAY, 64, (byte) 14);
				} else {
					clay = new ItemStack(Material.STAINED_CLAY, 64, (byte) 11);
				}
				p.getInventory().setItem(1, clay);
				p.getInventory().setItem(2, clay);
				p.getInventory().setItem(3, new ItemStack(Material.BOW, 1));
				p.getInventory().setItem(4, new ItemStack(Material.ARROW, 16));
				p.getInventory().setItem(9, clay);
				p.getInventory().setItem(18, clay);
				p.getInventory().setItem(27, clay);
				p.getInventory().setItem(10, clay);
				p.getInventory().setItem(19, clay);
				p.getInventory().setItem(28, clay);
				giveRoleKit(tp);
			}
		}
	}
	
	private void giveRoleKit(TeamPlayer tp) {
		if(tp.getRole() == PlayerRole.CAPTAIN) {
			Player p = Bukkit.getPlayerExact(tp.getPlayerName());
			p.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
			p.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
			p.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
			p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
			p.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE, 64));
			p.getInventory().addItem(new ItemStack(Material.DIODE, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_COMPARATOR, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 64));
			p.getInventory().addItem(new ItemStack(Material.PISTON_BASE, 64));
			p.getInventory().addItem(new ItemStack(Material.PISTON_STICKY_BASE, 64));
			p.getInventory().addItem(new ItemStack(Material.SLIME_BLOCK, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_BLOCK, 64));
		} else if(tp.getRole() == PlayerRole.SPECIAL_FORCES) {
			Player p = Bukkit.getPlayerExact(tp.getPlayerName());
			p.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
			p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
		} if(tp.getRole() == PlayerRole.INGENIEUR) {
			Player p = Bukkit.getPlayerExact(tp.getPlayerName());
			p.getInventory().addItem(new ItemStack(Material.REDSTONE, 64));
			p.getInventory().addItem(new ItemStack(Material.DIODE, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_COMPARATOR, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_TORCH_ON, 64));
			p.getInventory().addItem(new ItemStack(Material.PISTON_BASE, 64));
			p.getInventory().addItem(new ItemStack(Material.PISTON_STICKY_BASE, 64));
			p.getInventory().addItem(new ItemStack(Material.SLIME_BLOCK, 64));
			p.getInventory().addItem(new ItemStack(Material.REDSTONE_BLOCK, 64));
		} if(tp.getRole() == PlayerRole.SCHUETZE) {
			Player p = Bukkit.getPlayerExact(tp.getPlayerName());
			p.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
			p.getInventory().addItem(new ItemStack(Material.TNT, 64));
		} if(tp.getRole() == PlayerRole.SOLDAT) {
			Player p = Bukkit.getPlayerExact(tp.getPlayerName());
			p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
			p.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
			p.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
		}
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
		if(state != this.state) {
			Bukkit.getPluginManager().callEvent(new ArenaStateChangedEvent(this, state, this.state));
			this.state = state;
		}
	}
	
	public ArenaState getState() {
		return state;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public Team getTeamWithoutLeader() {
		for(Team t : teams) {
			if(t.getTeamLeader() == null) {
				return t;
			}
		}
		return null;
	}

	public ArenaReseter getReseter() {
		return reseter;
	}

	public void setReseter(ArenaReseter reseter) {
		this.reseter = reseter;
	}
	
	public boolean areBothTeamsReady() {
		return this.getTeam1().isReady() && this.getTeam2().isReady();
	}

	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	public FightScoreboard getScoreboard() {
		return scoreboard;
	}
	
	public void tpToTeamWarp() {
		for(Team t : getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				Player z = Bukkit.getPlayerExact(tp.getPlayerName());
				if(t.getTeamName().equals("team1")) {
					z.teleport(repo.getTeam1Warp());
				} else {
					z.teleport(repo.getTeam2Warp());
				}
			}
		}
	}
	
	public void prepareForNextFight() {
		scoreboard.clearScoreboard();
		waterRemover.stop();
		state = ArenaState.IDLE;
		teamManager = new TeamManager(this);
		teams.clear();
		teams.add(new Team("team1", this));
		teams.add(new Team("team2", this));
		repo.setEnteringAllowed(false);
		this.setOpen(false);
	}
	
	public void preparePlayerZ() {
		for(Team t : getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				Player z = Bukkit.getPlayerExact(tp.getPlayerName());
				z.setGameMode(GameMode.SURVIVAL);
				z.setHealth(20.0);
				z.setFoodLevel(20);
				z.getInventory().clear();
			}
		}
	}
	
	public void replace() {
		World world = this.repo.getWorld();
		CuboidRegion inner = getPlayGroundRegion();
		Point origin = new Point(BukkitUtil.toLocation(world, inner.getMinimumPoint()));
		Size size = new Size(inner.getWidth(), inner.getHeight(), inner.getLength());
		OpenWSK.getPluginInstance().getGenerator().addJob(new SimpleJob(origin, size, world, null, new SingleBlockProvider(new SingleBlockCriteria(Material.OBSIDIAN), Material.TNT, (byte)0), false));
		OpenWSK.getPluginInstance().getGenerator().addJob(new SimpleJob(origin, size, world, null, new SingleBlockProvider(new SingleBlockCriteria(Material.BEDROCK), Material.SLIME_BLOCK, (byte)0), false));
	}
}
