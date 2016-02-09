package net.thecobix.openwsk.invitation;

import net.thecobix.openwsk.team.Team;

public class Invitation {

	private String sender;
	private String receiver;
	private int duration;
	private Team team;
	
	public Invitation(String sender, String receiver, int duration, Team team) {
		this.duration = duration;
		this.receiver = receiver;
		this.sender = sender;
		this.team = team;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getReceiver() {
		return receiver;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Team getTeam() {
		return this.team;
	}
	
}
