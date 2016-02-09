package de.pro_crafting.common.scoreboard;

public enum Criteria {
	Dummy ("dummy"),
	Trigger ("trigger"),
	DeathCount ("deathCount"),
	PlayerKillCount ("playerKillCount"),
	TotalKillCount ("totalKillCount"),
	Health ("health");
	
	private String criteriaName;
	private Criteria(String criteria) {
		this.criteriaName = criteria;
	}
	
	public String getCriteriaName() {
		return this.criteriaName;
	}
}