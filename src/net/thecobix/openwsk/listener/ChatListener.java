package net.thecobix.openwsk.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.PlayerRole;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String format = e.getFormat();
		String msg = e.getMessage();
		Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
		StringBuilder sb = new StringBuilder();
		if(a != null) {
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t != null) {
				if(t.getTeamName().equals("team1")) {
					sb.append("§c["+a.getArenaName()+" ");
					for(TeamPlayer tp : t.getTeamMembers()) {
						if(tp.getPlayerName().equals(p.getName())) {
							if(tp.getRole() == PlayerRole.CAPTAIN) {
								sb.append("| Captain] ");
							} else if(tp.getRole() == PlayerRole.INGENIEUR) {
								sb.append("| Engineer] ");
							} else if(tp.getRole() == PlayerRole.SCHUETZE) {
								sb.append("| Sagittarius] ");
							} else if(tp.getRole() == PlayerRole.SOLDAT) {
								sb.append("| Soldier] ");
							} else if(tp.getRole() == PlayerRole.SPECIAL_FORCES) {
								sb.append("| Special Forces] ");
							} else {
								sb.append("| §4X§c] ");
							}
						}
					}
				} else {
					sb.append("§9["+a.getArenaName()+" ");
					for(TeamPlayer tp : t.getTeamMembers()) {
						if(tp.getPlayerName().equals(p.getName())) {
							if(tp.getRole() == PlayerRole.CAPTAIN) {
								sb.append("| Captain] ");
							} else if(tp.getRole() == PlayerRole.INGENIEUR) {
								sb.append("| Engineer] ");
							} else if(tp.getRole() == PlayerRole.SCHUETZE) {
								sb.append("| Sagittarius] ");
							} else if(tp.getRole() == PlayerRole.SOLDAT) {
								sb.append("| Soldier] ");
							} else if(tp.getRole() == PlayerRole.SPECIAL_FORCES) {
								sb.append("| Special Forces] ");
							} else {
								sb.append("| §4X§9] ");
							}
						}
					}
				}
			} else {
				sb.append("§7["+a.getArenaName()+"] ");
			}
		}
		sb.append(format);
		e.setFormat(sb.toString());
	}
	
}
