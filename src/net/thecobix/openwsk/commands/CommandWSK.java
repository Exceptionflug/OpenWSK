package net.thecobix.openwsk.commands;

import org.bukkit.entity.Player;

import de.pro_crafting.commandframework.Command;
import de.pro_crafting.commandframework.CommandArgs;
import net.thecobix.openwsk.main.OpenWSK;

public class CommandWSK {

	@Command(name="wsk", description="Zeigt Informationen über das Plugin an.", usage="/wsk")
	public void wskCommand(CommandArgs args) {
		Player p = args.getPlayer();
		p.sendMessage(OpenWSK.S_PREFIX+"OpenWSK WarShip Fightsystem by St0n3gr1d / MrCreeperkopf");
		p.sendMessage(OpenWSK.S_PREFIX+"Version: "+OpenWSK.S_VERSION);
		p.sendMessage(OpenWSK.S_PREFIX+"Um eine Liste mit Befehlen zu erhalten, gebe §6/wsk help §7ein.");
	}
	
	@Command(name="wsk.help", description="Zeigt eine Liste mit Befehlen.", usage="/wsk help")
	public void wskHelpCommand(CommandArgs args) {
		Player p = args.getPlayer();
		int permlevel = getHelpPermissionLevel(p);
		if(permlevel == 0) {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
		} else if(permlevel == 1) {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
			p.sendMessage("§8/wsk arena - §6Zeigt eine Liste mit Arenabefehlen.");
			p.sendMessage("§8/wsk quit [§cteam1§8, §9team2§8] - §6Beendet einen Kampf");
		} else {
			p.sendMessage(OpenWSK.S_PREFIX+"§8--- §6Open§bWSK §8---");
			p.sendMessage("§8/wsk - §6Zeigt Informationen über das Plugin an.");
			p.sendMessage("§8/wsk help - §6Zeigt eine Liste mit Befehlen.");
			p.sendMessage("§8/wsk team - §6Zeigt eine Liste mit Teambefehlen.");
			p.sendMessage("§8/wsk arena - §6Zeigt eine Liste mit Arenabefehlen.");
			p.sendMessage("§8/wsk quit [§cteam1§8, §9team2§8] - §6Beendet einen Kampf");
			p.sendMessage("§8/wsk reload - §6Reloaded das Plugin.");
		}
	}
	
	private int getHelpPermissionLevel(Player p) {
		if(p.hasPermission("wsk.mod.help")) {
			return 1;
		} else if(p.hasPermission("wsk.admin.help")) {
			return 2;
		} else {
			return 0;
		}
	}
	
}
