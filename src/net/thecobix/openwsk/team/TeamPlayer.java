package net.thecobix.openwsk.team;

public class TeamPlayer {

	private PlayerRole role;
	private String playerName;
	
	public TeamPlayer(String name) {
		this.playerName = name;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public PlayerRole getRole() {
		return role;
	}
	
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	public void setRole(PlayerRole role) {
		this.role = role;
	}
	
}
