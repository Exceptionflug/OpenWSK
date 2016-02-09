package de.pro_crafting.generator.criteria;

import de.pro_crafting.common.Point;
import de.pro_crafting.generator.BlockData;

public class CuboidCriteria implements Criteria {

	private Criteria wraped;

	public boolean matches(Point point, BlockData block) {
		if (wraped != null) {
			return wraped.matches(point, block);
		}
		return true;
	}

	public void wrap(Criteria criteria) {
		if (wraped == null) {
			wraped = criteria;
		} else {
			wraped.wrap(criteria);
		}
	}
}
