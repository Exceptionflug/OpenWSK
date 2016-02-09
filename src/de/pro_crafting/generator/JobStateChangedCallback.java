package de.pro_crafting.generator;

import de.pro_crafting.generator.job.Job;


public interface JobStateChangedCallback 
{
	public void jobStateChanged(Job job, JobState fromState);
}
