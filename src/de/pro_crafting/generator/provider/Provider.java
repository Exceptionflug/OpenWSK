package de.pro_crafting.generator.provider;

import de.pro_crafting.common.Point;
import de.pro_crafting.generator.BlockData;
import de.pro_crafting.generator.criteria.Criteria;

public interface Provider {
	BlockData getBlockData(Point point, BlockData block);
	Criteria getCriteria();
}
