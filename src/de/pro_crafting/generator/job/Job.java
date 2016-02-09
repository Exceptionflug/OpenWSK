package de.pro_crafting.generator.job;

import org.bukkit.World;

import de.pro_crafting.common.Point;
import de.pro_crafting.common.Size;
import de.pro_crafting.generator.JobState;

public interface Job 
{
	public boolean next();
	public JobState getState();
	public void setState(JobState state);
	public World getWorld();
	public int getAffectedBlocks();
	public Point getOrigin();
	public Size getSize();
}
