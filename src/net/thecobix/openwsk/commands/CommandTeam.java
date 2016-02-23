package net.thecobix.openwsk.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.pro_crafting.commandframework.Command;
import de.pro_crafting.commandframework.CommandArgs;
import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.fight.PreRunningTimer;
import net.thecobix.openwsk.invitation.Invitation;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

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
			a.getScoreboard().addTeamMember(z, t);
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
	
	@Command(name="wsk.team.accept", description="Nimmt eine Einladung an.", usage="/Wsk team accpet")
	public void onAccept(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t != null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist bereits in einem Team.");
				return;
			}
			Invitation i = OpenWSK.getPluginInstance().getInvitationSystem().getInvitation(p.getName());
			if(i == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu hast keine offenen Einladungen.");
				return;
			}
			Player z = Bukkit.getPlayerExact(i.getSender());
			if(z == null) {
				OpenWSK.getPluginInstance().getInvitationSystem().invitations.remove(i);
				p.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist bereits offline.");
				return;
			}
			Team tz = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(z);
			if(tz == null) {
				OpenWSK.getPluginInstance().getInvitationSystem().invitations.remove(i);
				p.sendMessage(OpenWSK.S_PREFIX+"§cDas Zielteam existiert nicht mehr.");
				return;
			}
			if(!tz.getTeamLeader().equals(z.getName())) {
				OpenWSK.getPluginInstance().getInvitationSystem().invitations.remove(i);
				p.sendMessage(OpenWSK.S_PREFIX+"§cDas wird nicht mehr von §6"+z.getName()+" §cangeführt.");
				return;
			}
			if(tz.getArena().getState() != ArenaState.SETUP) {
				OpenWSK.getPluginInstance().getInvitationSystem().invitations.remove(i);
				p.sendMessage(OpenWSK.S_PREFIX+"§cDie Arena ist nicht mehr in der Vorbereitungsphase.");
				return;
			}
			if(tz.addPlayer(p)) {
				OpenWSK.getPluginInstance().getInvitationSystem().accept(i);
				tz.getArena().getScoreboard().addTeamMember(p, tz);
				if(tz.getTeamName().equals("team1")) {
					p.teleport(tz.getArena().getRepo().getTeam1Warp());
				} else {
					p.teleport(tz.getArena().getRepo().getTeam2Warp());
				}
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu bist standartmäßig ein §6Schütze §a. Wechsle deine Rolle mit §6/wsk team changerole§a.");
			} else {
				OpenWSK.getPluginInstance().getInvitationSystem().invitations.remove(i);
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu konntest nicht hinzugefügt werden!");
			}
		}
	}
	
	@Command(name="wsk.team.leave", description="Du verlässt dein Team.", usage="/Wsk team leave")
	public void leave(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Team sucken = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(sucken == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team.");
				return;
			}
			if(sucken.getArena().getState() != ArenaState.SETUP) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu kannst jetzt nicht das Team verlassen.");
				return;
			}
			p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast dein Team im Stich gelassen...");
			sucken.removePlayer(p.getName());
		}
	}
	
	@Command(name="wsk.team.kick", description="Wirft einen Spieler aus deinem Team.", usage="/wsk team kick <Name>")
	public void kick(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			if(args.getArgs().length == 0) {
				p.sendMessage(OpenWSK.S_PREFIX+"§bBenutzung: /wsk team kick <Name>");
				return;
			}
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team");
				return;
			}
			Player z = Bukkit.getPlayerExact(args.getArgs(0));
			if(z == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDieser Spieler ist nicht online.");
				return;
			}
			Team tz = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(z);
			if(tz == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist in keinem Team");
				return;
			}
			if(!t.getTeamLeader().equals(p.getName())) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cNur der Captain kann Änderungen am Team vornehmen.");
				return;
			}
			if(p.getName().equals(z.getName())) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu kannst dich nicht selbst kicken!");
				return;
			}
			if(t.getArena().getArenaName().equals(tz.getArena().getArenaName()) && t.getTeamName().equals(tz.getTeamName())) {
				z.sendMessage(OpenWSK.S_PREFIX+"§cDu wurdest aus dem Team geworfen.");
				t.removePlayer(z.getName());
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast §6"+z.getName()+" §aaus deinem Team entfernt.");
			} else {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDer Spieler ist nicht in deinem Team.");
			}
		}
	}
	
	@Command(name="wsk.team.ready", description="Setzt dein Team bereit", usage="/wsk team ready")
	public void ready(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team!");
				return;
			}
			if(!t.getTeamLeader().equals(p.getName())) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cNur der Captain kann Änderungen am Team vornehmen.");
				return;
			}
			if(t.getArena().getState() != ArenaState.SETUP) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu kannst jetzt dein Teamstatus nicht ändern.");
				return;
			}
			t.setReady(!t.isReady());
			String msg = t.isReady() ? "Dein Team ist nun bereit" : "Dein Team ist nicht mehr bereit";
			p.sendMessage(OpenWSK.S_PREFIX+msg);
			if(t.getArena().areBothTeamsReady()) {
				t.getArena().setState(ArenaState.PRERUNNING);
				PreRunningTimer prt = new PreRunningTimer(t.getArena());
				prt.preRunningTimer();
			}
		}
	}
	
	@Command(name="wsk.team.changerole", description="Ändert deine Rolle", usage="/wsk team changerole")
	public void changeRole(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist in keinem Team.");
				return;
			}if(t.getTeamLeader() != null) {
				if( t.getTeamLeader().equals(p.getName())) {
					p.sendMessage(OpenWSK.S_PREFIX+"§cDu bist Captain. Du kannst keine andere Rolle übernehmen.");
					return;
				}
			}
			Inventory inv = Bukkit.createInventory(null, 27, "§aEinheiten");
			ItemStack spf = new ItemStack(Material.ENDER_PEARL);
			ItemMeta spf_meta = spf.getItemMeta();
			spf_meta.setDisplayName("§aSpezialeinheit");
			List<String> spf_lore = new ArrayList<>();
			spf_lore.add("§7Als Spezialeinheit kannst du das");
			spf_lore.add("§7gegnerische Schiff entern.");
			spf_meta.setLore(spf_lore);
			spf_meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
			spf.setItemMeta(spf_meta);
			spf.setAmount(1);
			inv.setItem(0, spf);
			
			ItemStack ing = new ItemStack(Material.REDSTONE);
			ItemMeta ing_meta = ing.getItemMeta();
			ing_meta.setDisplayName("§cIngenieur");
			List<String> ing_lore = new ArrayList<>();
			ing_lore.add("§7Als Ingenieur musst du helfen");
			ing_lore.add("§7Kanonen zu reparieren.");
			ing_meta.setLore(ing_lore);
			ing_meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
			ing.setItemMeta(ing_meta);
			ing.setAmount(1);
			inv.setItem(1, ing);
			
			ItemStack sol = new ItemStack(Material.STONE_SWORD);
			ItemMeta sol_meta = sol.getItemMeta();
			sol_meta.setDisplayName("§5Soldat");
			List<String> sol_lore = new ArrayList<>();
			sol_lore.add("§7Soldaten patrouillieren das eigene");
			sol_lore.add("§7Schiff und töten Eindringlinge.");
			sol_meta.setLore(sol_lore);
			sol_meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
			sol.setItemMeta(sol_meta);
			sol.setAmount(1);
			inv.setItem(2, sol);
			
			ItemStack sag = new ItemStack(Material.TNT);
			ItemMeta sag_meta = sag.getItemMeta();
			sag_meta.setDisplayName("§6Schütze");
			List<String> sag_lore = new ArrayList<>();
			sag_lore.add("§7Schützen beladen und feuern");
			sag_lore.add("§7Kanonen auf den Gegner ab.");
			sag_meta.setLore(sag_lore);
			sag_meta.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
			sag.setItemMeta(sag_meta);
			sag.setAmount(1);
			inv.setItem(3, sag);
			p.openInventory(inv);
		}
	}
	
	@Command(name="wsk.team.clear", description="Leert ein oder beide Teams", usage="/wsk team clear [team1,team2]", permission="wsk.team.clear")
	public void clear(CommandArgs args) {
		if(args.isPlayer()) {
			Player p = args.getPlayer();
			Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
			if(a == null) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu stehst in keiner Arena!");
				return;
			}
			if(args.getArgs().length == 0) {
				for(Team t : a.getTeams()) {
					t.getTeamMembers().clear();
					t.teamLeader = null;
				}
				p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast beide Teams geleert");
				a.getScoreboard().clearScoreboard();
				a.getScoreboard().initScoreboard();
			} else {
				if(args.getArgs(0).equalsIgnoreCase("team1")) {
					for(TeamPlayer tp : a.getTeam1().getTeamMembers()) {
						a.getScoreboard().removeTeamMember(tp, "team1");
					}
					a.getTeam1().getTeamMembers().clear();
					a.getTeam1().teamLeader = null;
					p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast §cTeam1§a geleert");
				} else {
					for(TeamPlayer tp : a.getTeam1().getTeamMembers()) {
						a.getScoreboard().removeTeamMember(tp, "team2");
					}
					a.getTeam2().getTeamMembers().clear();
					a.getTeam2().teamLeader = null;
					p.sendMessage(OpenWSK.S_PREFIX+"§aDu hast §9Team2§a geleert");
				}
			}
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
