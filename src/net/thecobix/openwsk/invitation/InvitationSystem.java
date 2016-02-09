package net.thecobix.openwsk.invitation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.thecobix.openwsk.main.OpenWSK;

public class InvitationSystem {

	public List<Invitation> invitations = new ArrayList<>();
	private int task;
	
	public InvitationSystem() {
		startTimer();
	}
	
	private void startTimer() {
		this.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(OpenWSK.getPluginInstance(), new Runnable() {
			
			@Override
			public void run() {
				if(invitations.isEmpty()) {
					return;
				}
				for(Invitation i : invitations) {
					int duration = i.getDuration();
					duration --;
					i.setDuration(duration);
					if(duration <= 0) {
						timeout(i);
					}
				}
			}
		}, 0L, 20L);
	}
	
	public void stopTimer() {
		Bukkit.getScheduler().cancelTask(task);
	}
	
	private void timeout(Invitation i) {
		invitations.remove(i);
		Player p = Bukkit.getPlayerExact(i.getSender());
		Player z = Bukkit.getPlayerExact(i.getReceiver());
		if(p != null) {
			p.sendMessage(OpenWSK.S_PREFIX+"Deine Einladung an §b"+z.getName()+" §7ist abgelaufen.");
		}
		if(z != null) {
			z.sendMessage(OpenWSK.S_PREFIX+"Die Einladung von §b"+p.getName()+" §7ist abgelaufen.");
		}
	}
	
	public void accept(Invitation i) {
		invitations.remove(i);
		Player p = Bukkit.getPlayerExact(i.getSender());
		Player z = Bukkit.getPlayerExact(i.getReceiver());
		if(p == null) {
			z.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist offline.");
			return;
		}
		p.sendMessage(OpenWSK.S_PREFIX+"§6"+z.getName()+" §ahat deine Einladung angenommen.");
	}
	
	public void decline(Invitation i) {
		invitations.remove(i);
		Player p = Bukkit.getPlayerExact(i.getSender());
		Player z = Bukkit.getPlayerExact(i.getReceiver());
		if(p == null) {
			return;
		}
		p.sendMessage(OpenWSK.S_PREFIX+"§6"+z.getName()+" §chat deine Einladung abgelehnt.");
	}
	
	public Invitation getInvitation(String receiver) {
		for(Invitation i : invitations) {
			if(i.getReceiver().equals(receiver)) {
				return i;
			}
		}
		return null;
	}
	
}
