package net.thecobix.openwsk.arena;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;

import de.pro_crafting.common.Point;
import de.pro_crafting.common.Size;
import de.pro_crafting.generator.JobState;
import de.pro_crafting.generator.JobStateChangedCallback;
import de.pro_crafting.generator.criteria.CuboidCriteria;
import de.pro_crafting.generator.job.Job;
import de.pro_crafting.generator.job.SimpleJob;
import de.pro_crafting.generator.provider.SingleBlockProvider;
import net.thecobix.openwsk.events.ArenaStateChangedEvent;
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
public class ArenaReseter implements Listener, JobStateChangedCallback {

	private Arena arena;
	private int waterLevel;
	private boolean shouldResetUnderwater = true;
	
	public ArenaReseter(Arena arena) {
		this.arena = arena;
		waterLevel = arena.getRepo().getWaterLevel();
		OpenWSK.getPluginInstance().getServer().getPluginManager().registerEvents(this, OpenWSK.getPluginInstance());
	}
	
	public void reset() {
		CuboidRegion rg = this.arena.getPlayGroundRegion();
		World world = this.arena.getRepo().getWorld();
		Point origin = new Point(BukkitUtil.toLocation(world, rg.getMinimumPoint()));
		origin.setY(waterLevel);
		Size size = new Size(rg.getWidth(), rg.getMaximumY(), rg.getLength());
		OpenWSK.getPluginInstance().getGenerator().addJob(new SimpleJob(origin, size, world, this, new SingleBlockProvider(new CuboidCriteria(), Material.AIR, (byte) 0)));
	}
	
	public void resetUnderWater() {
		shouldResetUnderwater = false;
		CuboidRegion rg = this.arena.getPlayGroundRegion();
		World world = this.arena.getRepo().getWorld();
		Point origin = new Point(BukkitUtil.toLocation(world, rg.getMinimumPoint()));
		origin.setY(rg.getMinimumY());
		Size size = new Size(rg.getWidth(), waterLevel, rg.getLength());
		OpenWSK.getPluginInstance().getGenerator().addJob(new SimpleJob(origin, size, world, this, new SingleBlockProvider(new CuboidCriteria(), Material.STATIONARY_WATER, (byte) 0)));
	}
	
	private void removeDrops(World arenaWorld) {
		CuboidRegion rg = this.arena.getPlayGroundRegion();
		for(Entity e : arenaWorld.getEntitiesByClasses(Item.class, Arrow.class)) {
			if(rg.contains(BukkitUtil.toVector(e.getLocation()))) {
				e.remove();
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onStateChanged(ArenaStateChangedEvent e) {
		if(!e.getArena().equals(this.arena)) {
			return;
		}
		if(e.getNewState() == ArenaState.PRERUNNING) {
			this.removeDrops(this.arena.getRepo().getWorld());
		} else if(e.getNewState() == ArenaState.RESET) {
			this.reset();
		}
	}

	@Override
	public void jobStateChanged(Job job, JobState fromState) {
		if(job.getState() != JobState.Finished) {
			return;
		}
		if(shouldResetUnderwater) {
			resetUnderWater();
			return;
		}
		shouldResetUnderwater = true;
		removeDrops(this.arena.getRepo().getWorld());
		if(this.arena.getState() == ArenaState.RESET) {
			this.arena.setState(ArenaState.IDLE);
		}
	}
	
}
