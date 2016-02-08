package net.thecobix.openwsk.team;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.thecobix.openwsk.main.OpenWSK;

public class Team {

	private String teamName;
	private ArrayList<TeamPlayer> teamMembers = new ArrayList<>();
	private String teamLeader;
	private int maxTeamSize = 10;
	
	public Team(String name) {
		this.teamName = name;
	}
	
	public String getTeamLeader() {
		return teamLeader;
	}
	
	public ArrayList<TeamPlayer> getTeamMembers() {
		return teamMembers;
	}
	
	public String getTeamName() {
		return teamName;
	}
	
	public void setTeamLeader(String teamLeader) {
		if(teamMembers.contains(teamLeader)) {
			this.teamLeader = teamLeader;
		} else {
			OpenWSK.Logger.log("Team", teamLeader+" is not in "+teamName+". Coud not set new Teamleader.", 1);
		}
	}
	
	public void setTeamMembers(ArrayList<TeamPlayer> teamMembers) {
		this.teamMembers = teamMembers;
	}
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public boolean isTeamMember(String name) {
		return teamMembers.contains(name);
	}
	
	public void sendTeamMessage(String msg) {
		for(TeamPlayer i : teamMembers) {
			Player z = Bukkit.getPlayerExact(i.getPlayerName());
			if(z == null) {
				removePlayer(i.getPlayerName());
				continue;
			}
			if(z.isOnline() == false) {
				removePlayer(i.getPlayerName());
				continue;
			}
			z.sendMessage(msg);
		}
	}
	
	public boolean removePlayer(String name) {
		for(TeamPlayer tp : teamMembers) {
			if(tp.getPlayerName().equals(name)) {
				teamMembers.remove(tp);
				return true;
			}
		}
		return false;
	}
	
	public boolean addPlayer(Player player) {
		return addPlayer(player.getName());
	}
	
	public boolean addPlayer(String name) {
		return addPlayer(name, false);
	}
	
	public boolean addPlayer(String name, boolean isLeader) {
		int i = teamMembers.size();
		i ++;
		if(i >= maxTeamSize) {
			return false;
		}
		teamMembers.add(new TeamPlayer(name));
		return true;
	}
	
	public boolean addPlayer(Player player, boolean isLeader) {
		return addPlayer(player.getName(), isLeader);
	}
	
	public int getMaxTeamSize() {
		return maxTeamSize;
	}
	
	public void setMaxTeamSize(int maxTeamSize) {
		this.maxTeamSize = maxTeamSize;
	}
	
}
