package net.thecobix.openwsk.commands;

import java.nio.charset.IllegalCharsetNameException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.pro_crafting.commandframework.Command;
import de.pro_crafting.commandframework.CommandArgs;
import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.invitation.Invitation;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;

public class CommandTeam {

	@Command(name="wsk.team", description="Zeigt die Teambefehle", usage="/wsk team")
	public void teamCmd(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			int lvl = getTeamHelpPermissionLevel(p);
			if(lvl == 0) {
				p.sendMessage(OpenWSK.S_PREFIX+"Team Befehle:");
				p.sendMessage("§8/wsk team invite <Name> - §6Lädt einen Spieler in dein Team ein.");
				p.sendMessage("§8/wsk team leave - §6Du verlässt dein Team.");
				p.sendMessage("§8/wsk team accept <Name> - §6Du nimmst eine Einladung an.");
				p.sendMessage("§8/wsk team decline <Name> - §6Du lehnst eine Einladung ab.");
				p.sendMessage("§8/wsk team kick <Name> - §6Du wirfst einen Spieler aus deinem Team.");
				p.sendMessage("§8/wsk team ready - §6Setzt dein Team bereit.");
			} else if(lvl == 1) {
				p.sendMessage(OpenWSK.S_PREFIX+"Team Befehle:");
				p.sendMessage("§8/wsk team invite <Name> - §6Lädt einen Spieler in dein Team ein.");
				p.sendMessage("§8/wsk team leave - §6Du verlässt dein Team.");
				p.sendMessage("§8/wsk team accept - §6Du nimmst eine Einladung an.");
				p.sendMessage("§8/wsk team decline - §6Du lehnst eine Einladung ab.");
				p.sendMessage("§8/wsk team kick <Name> - §6Du wirfst einen Spieler aus deinem Team.");
				p.sendMessage("§8/wsk team ready - §6Setzt dein Team bereit.");
				p.sendMessage("§8/wsk team captain <Name> - §6Setzt den Captain eines Teams.");
				p.sendMessage("§8/wsk team clear [§cteam1,§9team2§8] - §6Leert ein oder beide Teams.");
			}
		}
	}
	
	@Command(name="wsk.team.invite", description="Lädt einen Spieler in dein Team ein.", usage="/wsk team invite <Name>")
	public void invite(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			if(args.getArgs().length == 0) {
				p.sendMessage(OpenWSK.S_PREFIX+"§bBenutzung: /wsk team invite <Name>");
				return;
			}
			Player z = Bukkit.getPlayerExact(args.getArgs(0));
			if(z == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§6"+args.getArgs(0)+" §cist offline.");
				return;
			}
			Team tp = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(tp == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team!");
				return;
			}
			if(!tp.getTeamLeader().equals(p.getName())) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cNur der Captain darf weitere Spieler einladen.");
				return;
			}
			if(p.getName().equals(z.getName())) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu kannst dich nicht selbst einladen.");
				return;
			}
			Team tt = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(z);
			if(tt != null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist bereits in einem Team.");
				return;
			}
			if(tp.getArena().getState() != ArenaState.SETUP) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu darfst jetzt keinen Spieler mehr einladen.");
				return;
			}
			Invitation iz = OpenWSK.getPluginInstance().getInvitationSystem().getInvitation(z.getName());
			if(iz != null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§6"+z.getName()+" §chat bereits eine Einladung erhalten.");
				return;
			}
			Invitation i = new Invitation(p.getName(), z.getName(), 60, tp);
			OpenWSK.getPluginInstance().getInvitationSystem().invitations.add(i);
			z.sendMessage(OpenWSK.S_PREFIX+"§7Du hast eine Einladung von §b"+p.getName()+"§7 erhalten.");
			z.sendMessage("§7Kampf in Arena §b"+i.getTeam().getArena().getArenaName()+" §7für §b"+i.getTeam().getTeamName());
			z.sendMessage("§8/wsk team accept - §6Du nimmst die Einladung an.");
			z.sendMessage("§8/wsk team decline - §6Du lehnst die Einladung ab.");
			p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast §6"+z.getName()+" §aeingeladen.");
		}
	}
	
	@Command(name="wsk.team.captain", description="Setzt den Captain eines Teams.", usage="/wsk team captain <Name>", permission="wsk.team.captain")
	public void captain(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(args.getArgs().length == 0) {
				p.sendMessage(OpenWSK.S_PREFIX+"§bBenutzung: /wsk team captain <Name>");
				return;
			}
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena.");
				return;
			}
			if(a.getState() == ArenaState.PRERUNNING || a.getState() == ArenaState.RESET || a.getState() == ArenaState.RUNNING) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu kannst jetzt keinen Spieler mehr hinzufügen.");
				return;
			}
			Team t = a.getTeamWithoutLeader();
			if(t == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cBeide Teams haben bereits einen Captain.");
				return;
			}
			Player z = Bukkit.getPlayerExact(args.getArgs(0));
			if(z == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDieser Spieler ist nicht online.");
				return;
			}
			Team tt = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(z);
			if(tt != null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist bereits in einem Team.");
				return;
			}
			t.addPlayer(z, true);
			if(t.getTeamName().equals("team1")) {
				z.teleport(a.getRepo().getTeam1Warp());
			} else {
				z.teleport(a.getRepo().getTeam2Warp());
			}
			z.sendMessage(OpenWSK.S_PREFIX+"§6Teammanagement:");
			z.sendMessage("§8/wsk team invite <Name> - §6Lädt einen Spieler in dein Team ein.");
			z.sendMessage("§8/wsk team kick <Name> - §6Wirft einen Spieler aus deinem Team.");
			z.sendMessage("§8/wsk team leave - §6Du verlässt das Team.");
			z.sendMessage("§8/wsk team ready - §6Du stellst dein Team bereit.");
			if(a.getState() != ArenaState.SETUP) {
				a.setState(ArenaState.SETUP);
			}
		}
	}
	
	@Command(name="wsk.team.decline", description="Du lehnst eine Einladung ab.", usage="/wsk team decline")
	public void decline(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Invitation i = OpenWSK.getPluginInstance().getInvitationSystem().getInvitation(p.getName());
			if(i == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu hast keine offenen Einladungen.");
				return;
			}
			OpenWSK.getPluginInstance().getInvitationSystem().decline(i);
			p.sendMessage(OpenWSK.S_PREFIX+"§cDu hast die Einladung abgelehnt.");
		}
	}
	
	private int getTeamHelpPermissionLevel(Player p) {
		if(p.hasPermission("wsk.mod.help")) {
			return 1;
		} else {
			return 0;
		}
	}
	
}
