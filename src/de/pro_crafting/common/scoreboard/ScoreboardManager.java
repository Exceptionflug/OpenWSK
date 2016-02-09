package de.pro_crafting.common.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager<T> {
	private Map<T, Scoreboard> scoreboards;
	
	public ScoreboardManager() {
		this.scoreboards = new HashMap<T, Scoreboard>();
	}
	
	public int getScore(T key, String name, String objectiveName) {
		Score score = getScoreObject(key, objectiveName, name);
		return score.getScore();
	}
	
	public void setScore(T key, String name, int value, String objectiveName) {
		Score score = getScoreObject(key, objectiveName, name);
		score.setScore(value);
	}
	
	public void changeScoreName(T key, String originalName, String objectiveName, String name) {
		Score oldScore = getScoreObject(key, objectiveName, originalName);
		getScoreObject(key, objectiveName, name).setScore(oldScore.getScore());
		this.getScoreboard(key).resetScores(originalName);
	}
	
	public void removeScore(T key, String name) {
		this.getScoreboard(key).resetScores(name);
	}
	
	public void showObjective(T key, String objectiveName, DisplaySlot slot) {
		Scoreboard board = getScoreboard(key);
		board.clearSlot(slot);
		Objective obj = board.getObjective(objectiveName);
		if (obj != null) {
			board.clearSlot(slot);
			obj.setDisplaySlot(slot);
		}
	}
	
	public void createObjective(T key, String name, DisplaySlot slot, Criteria criteria) {
		Scoreboard board = getScoreboard(key);
		if (board.getObjective(name) == null) {
			board.registerNewObjective(name, criteria.getCriteriaName());
			showObjective(key, name, slot);
		}
	}
	
	private Scoreboard newScoreboard(T key) {
		if (!scoreboards.containsKey(key)) {
			scoreboards.put(key, Bukkit.getScoreboardManager().getNewScoreboard());
		}
		return scoreboards.get(key);
	}
	
	public void clearScoreboard(T key) {
		Scoreboard board = getScoreboard(key);
		for (Team team : board.getTeams()) {
			board.getTeam(team.getName()).unregister();
		}

		for (Objective obj : board.getObjectives()) {
			board.getObjective(obj.getName()).unregister();
		}
	}
	
	public Scoreboard getScoreboard(T key) {
		return this.newScoreboard(key);
	}
	
	private Objective getObjective(T key, String name) {
		return this.getScoreboard(key).getObjective(name);
	}
	
	private Score getScoreObject(T key, String objective, String name) {
		Objective obj = getObjective(key, objective);
		return obj.getScore(name);
	}
}
