package net.thecobix.openwsk.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.PlayerRole;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

public class InventoryListener implements Listener {

	
	@EventHandler
	public void click(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		try{
			e.getSlot();
		}catch(NullPointerException r) {
			return;
		}
		if(e.getInventory().getName() == null) {
			return;
		}
		if(e.getClickedInventory().getName().equals("§aEinheiten")) {
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t == null) {
				p.closeInventory();
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team.");
				return;
			}
			TeamPlayer tp = null;
			for(TeamPlayer tpl : t.getTeamMembers()) {
				if(tpl.getPlayerName().equals(p.getName())) {
					tp = tpl;
				}
			}
			if(e.getCurrentItem() == null) {
				return;
			}
			if(e.getCurrentItem().getType() == Material.ENDER_PEARL) {
				p.closeInventory();
				tp.setRole(PlayerRole.SPECIAL_FORCES);
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu wirst als Spezialeinheit Kämpfen");
			} else if(e.getCurrentItem().getType() == Material.STONE_SWORD) {
				p.closeInventory();
				tp.setRole(PlayerRole.SOLDAT);
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu wirst als Soldat Kämpfen");
			} else if(e.getCurrentItem().getType() == Material.REDSTONE) {
				p.closeInventory();
				tp.setRole(PlayerRole.INGENIEUR);
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu wirst als Ingenieur Kämpfen");
			} else if(e.getCurrentItem().getType() == Material.TNT) {
				p.closeInventory();
				tp.setRole(PlayerRole.SCHUETZE);
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu wirst als Schütze Kämpfen");
			}
			e.setCancelled(true);
		}
	}
}
