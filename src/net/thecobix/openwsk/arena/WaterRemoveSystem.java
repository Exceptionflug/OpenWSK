package net.thecobix.openwsk.arena;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitTask;

import net.thecobix.openwsk.main.OpenWSK;

/**
 * This class is inspired by the WGK WaterRemove System
 *
 */
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
public class WaterRemoveSystem implements Listener {

	private Arena arena;
	private List<SimpleEntry<Location, Integer>> explBlocks;
	private List<Block> waterList;
	private BukkitTask task;
	
	public WaterRemoveSystem(Arena a) {
		this.arena = a;
		explBlocks = new ArrayList<>();
		waterList = new ArrayList<>();
	}
	
	public void start() {
		stop();
		Bukkit.getPluginManager().registerEvents(this, OpenWSK.getPluginInstance());
		explBlocks = new ArrayList<>();
		waterList = new ArrayList<>();
		task = Bukkit.getScheduler().runTaskTimer(OpenWSK.getPluginInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				waterCheck();
				removeWater();
				
			}
		}, 0, 20);
	}
	
	public void stop() {
		if(task != null) {
			task.cancel();
		}
		HandlerList.unregisterAll(this);
	}
	
	public void add(Location loc) {
		this.explBlocks.add(new SimpleEntry<Location, Integer>(loc, 15));
	}
	
	public void waterCheck() {
		for(int i = this.explBlocks.size()-1;i>-1;i--) {
			if(this.explBlocks.get(i).getValue() >= 15) {
				Block b = this.explBlocks.get(i).getKey().getBlock();
				if(b.getType() == Material.WATER || b.getType() == Material.STATIONARY_WATER) {
					this.waterList.add(b);
					this.explBlocks.remove(i);
				} else {
					this.explBlocks.get(i).setValue(this.explBlocks.get(i).getValue()+1);
				}
			}
		}
	}
	
	private void removeWater() {
		for(int i = this.waterList.size()-1;i>-1;i--) {
			Block current = this.waterList.get(i);
			for(Block removeBlock : getSourceOfWater(current)) {
				removeBlock.setType(Material.AIR);
			}
			if(current.getType() == Material.AIR) {
				this.waterList.remove(i);
			}
		}
	}
	
	private List<Block> getSourceOfWater(Block b) {
		List<Block> water = new ArrayList<Block>();
		collectBlocks(b, water, new ArrayList<Block>());
		return water;
	}
	
	/* code by: andf54
	 * https://forums.bukkit.org/threads/get-the-whole-stream-of-water-or-lava.110156/
	 * Einige kleinere Änderungen vorgenommen
	 */
	public void collectBlocks(Block anchor, List<Block> collected, List<Block> visitedBlocks)
	{
		if(!(anchor.getType() == Material.WATER || anchor.getType() == Material.STATIONARY_WATER)) return;
		
		if (visitedBlocks.contains(anchor))return;
		visitedBlocks.add(anchor);
		if(anchor.getType() == Material.STATIONARY_WATER)
		{
		   collected.add(anchor);
		}
		if(visitedBlocks.size() > 50) {
			collected.clear();
			return;
		}
		collectBlocks(anchor.getRelative(BlockFace.UP), collected, visitedBlocks);
		collectBlocks(anchor.getRelative(BlockFace.NORTH), collected, visitedBlocks);
		collectBlocks(anchor.getRelative(BlockFace.EAST), collected, visitedBlocks);
		collectBlocks(anchor.getRelative(BlockFace.SOUTH), collected, visitedBlocks);
		collectBlocks(anchor.getRelative(BlockFace.WEST), collected, visitedBlocks);
	}
}
