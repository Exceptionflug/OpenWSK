package net.thecobix.openwsk.fight;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.earth2me.essentials.signs.SignProtection.SignProtectionState;

import net.thecobix.openwsk.arena.Arena;
import net.thecobix.openwsk.arena.ArenaState;
import net.thecobix.openwsk.events.ArenaStateChangedEvent;
import net.thecobix.openwsk.main.OpenWSK;
import net.thecobix.openwsk.team.TeamPlayer;

public class FightScoreboard implements Listener {

	private Arena arena;
	private ScoreboardManager manager;
	private Scoreboard board;
	private Team teamRed;
	private Team teamBlue;
	private FightTimer timer;
	private net.thecobix.openwsk.team.Team team1;
	private net.thecobix.openwsk.team.Team team2;
	
	public static boolean isStarted = false;
	public int timerInt = 5;
	private int task2;
	
	public FightScoreboard(Arena a) {
		this.arena = a;
		this.manager = Bukkit.getServer().getScoreboardManager();
		this.board = this.manager.getNewScoreboard();
		Bukkit.getServer().getPluginManager().registerEvents(this, OpenWSK.getPluginInstance());
		this.team1 = arena.getTeam1();
		this.team2 = arena.getTeam2();
	}
	
	public void objectiveSwitcher() {
		task2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(OpenWSK.getPluginInstance(), new Runnable() {
			
			@Override
			public void run() {
				
				timerInt --;
				
				switch (timerInt) {
				case 5:
					board.getObjective("Team1").setDisplayName("§cTeam1 | Leben");
					board.getObjective("Team1").setDisplaySlot(DisplaySlot.SIDEBAR);
					break;

				case 2:
					board.getObjective("Team2").setDisplayName("§9Team2 | Leben");
					board.getObjective("Team2").setDisplaySlot(DisplaySlot.SIDEBAR);
					break;
					
				case -1:
					board.getObjective("Info").setDisplayName("§aInfo");
					board.getObjective("Info").setDisplaySlot(DisplaySlot.SIDEBAR);
					break;
					
				case -3:
					timerInt = 6;
					break;
					
				default:
					break;
				}
				
			}
		}, 0, 80);
	}
	
	public void initScoreboard() {
		if(this.board.getObjective("Team1") != null) {
			return;
		}
		if(this.board.getObjective("Team2") != null) {
			return;
		}
		if(this.board.getObjective("Info") != null) {
			return;
		}
		this.board.registerNewObjective("Team1", "dummy");
		this.board.registerNewObjective("Team2", "dummy");
		this.board.registerNewObjective("Info", "dummy");
		this.board.getObjective("Team1").setDisplayName("§cTeam1 | Leben");
		this.board.getObjective("Team1").setDisplaySlot(DisplaySlot.SIDEBAR);
		this.board.getObjective("Info").getScore("§eZeit in Min.:").setScore(this.arena.getRepo().timeLeftMinutes);
		initTeams();
		objectiveSwitcher();
	}
	
	private void initTeams() {
		this.teamRed = this.board.registerNewTeam("team_red");
		this.teamRed.setDisplayName("teamred");
		this.teamRed.setPrefix("§c");
		
		this.teamBlue = this.board.registerNewTeam("team_blue");
		this.teamBlue.setDisplayName("teamblue");
		this.teamBlue.setPrefix("§9");
		
		this.teamRed.addEntry("Niemand");
		this.teamBlue.addEntry("Niemand");
		this.board.getObjective("Team2").getScore("Niemand").setScore(20);
		this.board.getObjective("Team1").getScore("Niemand").setScore(20);
	}
	
	public void removeTeamMember(TeamPlayer member, String teamName) {
		initScoreboard();
		if(teamName.equals("team1")) {
			this.teamRed.removeEntry(member.getPlayerName());
			if(this.teamRed.getSize() == 0) {
				this.teamRed.addEntry("Niemand");
				this.board.getObjective("Team1").getScore("Niemand").setScore(20);
			}
		} else {
			this.teamBlue.removeEntry(member.getPlayerName());
			if(this.teamBlue.getSize() == 0) {
				this.teamBlue.addEntry("Niemand");
				this.board.getObjective("Team2").getScore("Niemand").setScore(20);
			}
		}
		this.board.resetScores(member.getPlayerName());
	}
	
	public void addTeamMember(Player p, net.thecobix.openwsk.team.Team t) {
		initScoreboard();
		if(t.getTeamName().equals("team1")) {
			this.teamRed.removeEntry("Niemand");
			this.teamRed.addEntry(p.getName());
			this.board.getObjective("Team1").getScore(p.getName()).setScore(20);
		} else {
			this.teamBlue.removeEntry("Niemand");
			this.teamBlue.addEntry(p.getName());
			this.board.getObjective("Team2").getScore(p.getName()).setScore(20);
		}
	}
	
	public void clearScoreboard() {
		this.teamRed.unregister();
		this.teamBlue.unregister();
		this.board.getObjective("Team1").unregister();
		this.board.getObjective("Team2").unregister();
		this.board.getObjective("Info").unregister();
		Bukkit.getScheduler().cancelTask(task2);
		timerInt = 5;
	}
	
	public void enterArena(Player p) {
		p.setScoreboard(this.board);
	}
	
	public void leaveArena(Player p) {
		p.setScoreboard(this.manager.getNewScoreboard());
	}
	
	public void updateHealthOfPlayer(Player p) {
		if(this.arena.getTeamManager().isPlayerAlive(p)) {
			double health = p.getHealth();
			net.thecobix.openwsk.team.Team t = OpenWSK.getPluginInstance().getArenaManager().getTeamFromPlayer(p);
			if(t == null) {
				return;
			}
			if(t.getTeamName().equals("team1")) {
				this.board.getObjective("Team1").getScore(p.getName()).setScore((int)Math.ceil(health));
			} else {
				this.board.getObjective("Team2").getScore(p.getName()).setScore((int)Math.ceil(health));
			}
		} else {
			this.board.resetScores(p.getName());
		}
	}
	
	public void updateTime(int time) {
		this.board.getObjective("Info").getScore("§eZeit in Min.:").setScore(time);
	}
	
	@EventHandler
	public void arenaStateChanged(ArenaStateChangedEvent e) {
		if(!e.getArena().equals(this.arena)) {
			return;
		}
		if(e.getNewState() == ArenaState.SETUP) {
			initScoreboard();
		} else if(e.getNewState() == ArenaState.RUNNING) {
			Fight f = new Fight(this.arena);
			f.startFight();
			FightManager.fights.add(f);
		} else if(e.getOldState() == ArenaState.RUNNING) {
			Fight fi = null;
			for(Fight f : FightManager.fights) {
				if(f.getArena().getArenaName().equals(this.arena.getArenaName())) {
					fi = f;
				}
			}
			if(fi.isTimerRunning()) {
				fi.stopTimer();
				clearScoreboard();
			}
		}
	}
	
}
