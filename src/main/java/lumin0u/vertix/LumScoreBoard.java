package fr.lumin0u.vertix;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import fr.lumin0u.vertix.commands.TfCommand;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.managers.StatsManager;
import fr.senseijuba.survivor.utils.NMSUtils;
import fr.senseijuba.survivor.utils.PacketUtils;

public class LumScoreBoard
{
	public static void refreshScoreBoard(Player p)
	{
		GameManager gm = GameManager.getInstance(p.getWorld());
		
		if(gm == null)
			return;

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective rightObjective = board.registerNewObjective("TF2 scoreboard", "dummy");
		
		int score = 10;

		rightObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		rightObjective.setDisplayName("�6-=�8TF2�6=-");
		rightObjective.getScore("�6Timer: �7" + ((gm.getTimeStarted() / 1000) / 60 < 10 ? "0" : "") + (gm.getTimeStarted() / 1000) / 60 + ":" + ((gm.getTimeStarted() / 1000) % 60 < 10 ? "0" : "") + (gm.getTimeStarted() / 1000) % 60).setScore(score--);
		rightObjective.getScore("�e").setScore(score--);

		if(gm.getGameType().isCarts() && gm.getCartManager() != null)
		{
			for(Team t : gm.getTeams())
			{
				rightObjective.getScore(t.getPrefix() + "Equipe " + t.getName(true) + ": �7" + gm.getCartManager().getPurcent(t) + "%").setScore(score--);
			}
		}
		
//		TF.debug(gm.getZoneManager());

		if(gm.getGameType().isKoth())
		{
			for(Team t : gm.getTeams())
			{
				rightObjective.getScore(t.getPrefix() + "Equipe " + t.getName(true) + ": �7" + (int) gm.getZoneManager().getPurcent(t) + "%").setScore(score--);
			}
		}

		if(gm.getGameType().isTDM())
		{
			for(Team t : gm.getTeams())
			{
				rightObjective.getScore(t.getPrefix() + "Equipe " + t.getName(true) + ": �7" + t.getKills() + " kill" + (t.getKills() > 1 ? "s" : "")).setScore(score--);
			}
		}

		if(gm.getGameType().isTDM() && gm.getTeamOf(p) != null)
		{
			rightObjective.getScore("�f").setScore(score--);
			rightObjective.getScore("�6Kills restants : �e" + (gm.getKillsToWin() - gm.getTeamOf(p).getKills())).setScore(score--);
		}

		if(gm.getGameType().areTeamsActive() && gm.getTeamOf(p) != null)
		{
			rightObjective.getScore("�l").setScore(score--);
			rightObjective.getScore("�7Equipe: " + gm.getTeamOf(p).getPrefix() + gm.getTeamOf(p).getName(true)).setScore(score--);
		}

		if(PlayerManager.getInstance().getTFPlayer(p).getpower() != null)
		{
			rightObjective.getScore("�8").setScore(score--);
			rightObjective.getScore("�7Pouvoir : �2"+PlayerManager.getInstance().getTFPlayer(p).getpower().getName()).setScore(score--);
		}

		Objective tabObjective = board.registerNewObjective("TF2 tab", "dummy");
		tabObjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		tabObjective.setDisplayName("�6-=�8TF2�6=-");

		for(Player pl : gm.getPlayersOnline())
		{
			tabObjective.getScore(pl.getName()).setScore((int)(StatsManager.getInstance().getKillsOf(pl)));
		}

		p.setScoreboard(board);

		String listName = p.getDisplayName();

		if(gm.getGameType().areTeamsActive() && gm.getTeamOf(p) != null)
			listName = gm.getTeamOf(p).getPrefix() + p.getName();

		else if(!gm.getPlayers().contains(p.getUniqueId()) && gm.isGameStarted())
			listName = "�7�o" + p.getDisplayName();
		
		if(TfCommand.cheaters.contains(p))
			listName = listName + "�km";
		
		for(Team t : gm.getTeams())
		{
			board.registerNewTeam(t.getName(false));
			board.getTeam(t.getName(false)).setPrefix(t.getPrefix());
		}
		
		for(Player pl : p.getWorld().getPlayers())
			if(gm.getTeamOf(PlayerManager.getInstance().getTFPlayer(pl).getDisguised()) != null)
				board.getTeam(gm.getTeamOf(PlayerManager.getInstance().getTFPlayer(pl).getDisguised()).getName(false)).addPlayer(pl);

		p.setPlayerListName(listName);

		String title = "�8==========�6TF2�8==========";

		try
		{
			Class<?> packetHeaderFooterClass = NMSUtils.getClass("PacketPlayOutPlayerListHeaderFooter");// NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "")
			Object packet = packetHeaderFooterClass.getDeclaredConstructor(NMSUtils.getClass("IChatBaseComponent")).newInstance(NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, ""));
			PacketUtils.setField("a", NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "{\"translate\":\"" + title + "\"}"), packet);
			PacketUtils.setField("b", NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "{\"translate\":\"" + title + "\"}"), packet);
			PacketUtils.sendPacket(p, packet);

		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
