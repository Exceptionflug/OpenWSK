package de.pro_crafting.generator.provider;

import org.bukkit.Material;

import de.pro_crafting.common.Point;
import de.pro_crafting.generator.BlockData;
import de.pro_crafting.generator.criteria.Criteria;

public class SingleBlockProvider implements Provider{
	private BlockData blockData;
	private Criteria criteria;
	
	public SingleBlockProvider(Criteria criteria, Material type, byte dataByte) {
		this.criteria = criteria;
		this.blockData = new BlockData(type, dataByte);
	}
	
	public BlockData getBlockData(Point point, BlockData block) {
		if (!criteria.matches(point, block)) {
			return block;
		}
		return this.blockData;
	}

	public Criteria getCriteria() {
		return this.criteria;
	}
}
