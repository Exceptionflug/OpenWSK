package net.thecobix.openwsk.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.events.FightQuitEvent;
import net.thecobix.openwsk.fight.Fight;
import net.thecobix.openwsk.fight.FightManager;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.PlayerRole;
import net.thecobix.openwsk.team.Team;
import net.thecobix.openwsk.team.TeamPlayer;

public class FightListener implements Listener {

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		for(Fight fi : FightManager.fights) {
			for(Team t : fi.getArena().getTeams()) {
				for(TeamPlayer tp : t.getTeamMembers()) {
					if(tp.getPlayerName().equals(p.getName())) {
						if(t.getArena().getState() != ArenaState.RUNNING) {
							e.setCancelled(true);
						} else {
							Bukkit.getScheduler().runTask(OpenWSK.getPluginInstance(), new Runnable() {
								public void run() {
									t.getArena().getScoreboard().updateHealthOfPlayer(p);
								}
							});
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void move(PlayerMoveEvent e) {
		for(Fight fi : FightManager.fights) {
			if(e.getPlayer().getLocation().getY() <= fi.getArena().getRepo().getWaterLevel()) {
				for(Team t : fi.getArena().getTeams()) {
					for(TeamPlayer tp : t.getTeamMembers()) {
						if(tp.getPlayerName().equals(e.getPlayer().getName())) {
							if(!fi.getArena().getRepo().isEnteringAllowed() && t.getArena().getState() == ArenaState.RUNNING) {
								e.getPlayer().damage((double)fi.getArena().getRepo().getWaterLevelDamage());
							}
							if(t.getArena().getState() != ArenaState.RUNNING) {
								e.getPlayer().setHealth(20.0);
								e.getPlayer().setFoodLevel(20);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
		if(t == null) {
			return;
		}
		boolean doEnd = false;
		for(TeamPlayer tp : t.getTeamMembers()) {
			if(tp.getPlayerName().equals(p.getName())) {
				if(tp.getRole() == PlayerRole.CAPTAIN) {
					doEnd = true;
				}
				tp.setRole(PlayerRole.DIED);
			}
		}
		String msg = t.getTeamName().equals("team1") ? "§c"+p.getName()+" ist gestorben!" : "§9"+p.getName()+" ist gestorben!";
		t.getArena().broadcastInside(OpenWSK.S_PREFIX+msg);
		t.getArena().getScoreboard().updateHealthOfPlayer(p);
		if(doEnd) {
			endFightNoCaptain(t, p);
			return;
		}
		for(TeamPlayer tp : t.getTeamMembers()) {
			if(tp.getRole() != PlayerRole.DIED) {
				return;
			}
		}
		t.getArena().broadcastInside(OpenWSK.S_PREFIX+"§2Jeder aus "+t.getTeamName()+" ist tot!");
		p.setHealth(20.0);
		Fight fi = null;
		for(Fight f : FightManager.fights) {
			if(f.getArena().getArenaName().equals(t.getArena().getArenaName())) {
				fi = f;
			}
		}
		if(fi == null) {
			t.getArena().broadcastInside(OpenWSK.S_PREFIX+"§4Ooops... ein Fehler ist aufgetreten. Der Kampf muss manuell beendet werden!");
			return;
		}
		String win = t.getTeamName().equals("team1") ? "team2 hat gewonnen!" : "team1 hat gewonnen!";
		Bukkit.getPluginManager().callEvent(new FightQuitEvent(fi, "§2Alle tot - "+t.getTeamName()+" hat verloren! "+win));
	}
	
	private void endFightNoCaptain(Team looserPack, Player lastDied) {
		looserPack.getArena().broadcastInside(OpenWSK.S_PREFIX+"§2Der Captain aus "+looserPack.getTeamName()+" ist tot!");
		lastDied.setHealth(20.0);
		Fight fi = null;
		for(Fight f : FightManager.fights) {
			if(f.getArena().getArenaName().equals(looserPack.getArena().getArenaName())) {
				fi = f;
			}
		}
		if(fi == null) {
			looserPack.getArena().broadcastInside(OpenWSK.S_PREFIX+"§4Ooops... ein Fehler ist aufgetreten. Der Kampf muss manuell beendet werden!");
			return;
		}
		String win = looserPack.getTeamName().equals("team1") ? "team2 hat gewonnen!" : "team1 hat gewonnen!";
		Bukkit.getPluginManager().callEvent(new FightQuitEvent(fi, "§2Captain tot - "+looserPack.getTeamName()+" hat verloren! "+win));
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
		if(t == null) {
			return;
		}
		for(TeamPlayer tp : t.getTeamMembers()) {
			if(tp.getPlayerName().equals(p.getName())) {
				if(tp.getRole() == PlayerRole.DIED) {
					FightManager.respawnAsSpectator(p, t.getArena());
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onQuit(FightQuitEvent e) {
		e.getFight().stopTimer();
		Arena a = e.getFight().getArena();
		a.broadcastInside(OpenWSK.S_PREFIX+e.getReason());
		a.setState(ArenaState.SPECTATE);
	}
	
	@EventHandler
	public void damageByEntity(EntityDamageByEntityEvent e) {
		Player damager = null;
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		if (e.getDamager() instanceof Projectile
				&& ((Projectile)e.getDamager()).getShooter() instanceof Player) {
			damager = (Player) ((Projectile)e.getDamager()).getShooter();
		} else if (e.getDamager() instanceof Player) {
			damager = (Player) e.getDamager();
		}
		if(damager != null) {
			Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			Team z = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(damager);
			if(t == null || z == null) {
				return;
			}
			if(t.equals(z)) {
				damager.sendMessage(OpenWSK.S_PREFIX+"§cDu darfst Spieler aus deinem Team nicht schaden!");
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void entityRegainHealth(EntityRegainHealthEvent e) {
		if(!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		Arena a = OpenWSK.getPluginInstance().getArenaManager().getArenaFromPlayer(p);
		if(a == null) {
			return;
		}
		for(Team t : a.getTeams()) {
			for(TeamPlayer tp : t.getTeamMembers()) {
				if(tp.getPlayerName().equals(p.getName()) && tp.getRole() != PlayerRole.DIED) {
					Bukkit.getScheduler().runTask(OpenWSK.getPluginInstance(), new Runnable() {
						public void run() {
							t.getArena().getScoreboard().updateHealthOfPlayer(p);
						}
					});
				}
			}
		}
	}
	
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
		if(t == null) {
			return;
		}
		if(b != null && b.getType() == Material.CAKE_BLOCK) {
			p.sendMessage(OpenWSK.S_PREFIX+"§cDu darfst nichts essen.");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void consume(PlayerItemConsumeEvent e) {
		Player p = e.getPlayer();
		ItemStack is = e.getItem();
		if(OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p) != null) {
			if(is.getType().isEdible() || is.getType() == Material.POTION) {
				p.sendMessage(OpenWSK.S_PREFIX+"§cDu darfst nichts essen oder trinken!");
				p.getInventory().remove(is);
				p.getInventory().setArmorContents(null);
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p) != null) {
			p.sendMessage(OpenWSK.S_PREFIX+"§cDu darfst nichts craften!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(p == null) {
			return;
		}
		Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
		if(t != null) {
			if(t.getArena().isOpen()) {
				TeamPlayer tp = null;
				for(TeamPlayer tpl : t.getTeamMembers()) {
					if(tpl.getPlayerName().equals(p.getName())) {
						tp = tpl;
					}
				}
				if(tp == null) {
					return;
				}
				Block b = e.getBlock();
				if(b.getType() == Material.TNT) {
					if(tp.getRole() == PlayerRole.SCHUETZE || tp.getRole() == PlayerRole.INGENIEUR || tp.getRole() == PlayerRole.CAPTAIN) {
						e.setBuild(true);
					} else {
						p.sendMessage(OpenWSK.S_PREFIX+"§cNur Schützen, Ingenieure und der Captain können TNT platzieren.");
						e.setBuild(false);
						e.setCancelled(true);
					}
				} else if(b.getType() == Material.REDSTONE || b.getType() == Material.REDSTONE_BLOCK ||
						b.getType() == Material.REDSTONE_COMPARATOR || b.getType() == Material.REDSTONE_COMPARATOR_OFF ||
						b.getType() == Material.REDSTONE_COMPARATOR_ON || b.getType() == Material.REDSTONE_LAMP_OFF ||
						b.getType() == Material.REDSTONE_LAMP_ON ||b.getType() == Material.REDSTONE_TORCH_OFF ||
						b.getType() == Material.REDSTONE_TORCH_ON ||b.getType() == Material.REDSTONE_WIRE || 
						b.getType() == Material.DIODE_BLOCK_OFF ||b.getType() == Material.DIODE_BLOCK_ON ||
						b.getType() == Material.PISTON_BASE ||b.getType() == Material.PISTON_STICKY_BASE) {
					if(tp.getRole() == PlayerRole.INGENIEUR || tp.getRole() == PlayerRole.CAPTAIN) {
						e.setBuild(true);
					} else {
						p.sendMessage(OpenWSK.S_PREFIX+"§cNur Ingenieure und der Captain können Redstone Elemente setzen.");
						e.setBuild(false);
						e.setCancelled(true);
					}
				}
			}
		}
	}
	
}
