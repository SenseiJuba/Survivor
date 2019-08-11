package fr.lumin0u.vertix.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.utils.Cuboid;
import fr.senseijuba.survivor.utils.Title;

public class ZoneRunnable extends BukkitRunnable
{
	private Cuboid zone;
	private GameManager gm;
	private ZoneManager zm;
	private List<Player> wasThereLastSec;
	private List<Block> clayOrWool;
	private HashMap<Team, Long> timeLastCapture;
	private Team ancientTeam;
	private long delay;

	public ZoneRunnable(Cuboid zone, long delay)
	{
		this.zone = zone;
		this.delay = delay;

		gm = GameManager.getInstance(zone.getWorld());
		zm = ZoneManager.getInstance(zone.getWorld());

		wasThereLastSec = new ArrayList<>();

		clayOrWool = new ArrayList<>();

		timeLastCapture = new HashMap<>();

		for(Block b : zone.blocksInside())
			if(b.getType().equals(Material.STAINED_CLAY) || b.getType().equals(Material.WOOL))
				clayOrWool.add(b);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		for(Team t : timeLastCapture.keySet())
			timeLastCapture.replace(t, timeLastCapture.get(t) + delay);

//		TF.debug(timeLastCapture);

		List<Player> playersIn = new ArrayList<>();

		HashMap<Team, Integer> teamsIn = new HashMap<>();
		Team majorityTeam = null;

		for(Player p : zone.getWorld().getPlayers())
		{
			if(zone.hasInside(p))
			{
				playersIn.add(p);
				StatsManager.getInstance().startCapFor(p);
			}
		}

		for(Player p : wasThereLastSec)
		{
			if(!playersIn.contains(p))
			{
				StatsManager.getInstance().stopCapFor(p);
			}
		}

		for(Player p : playersIn)
		{
			Team t = gm.getTeamOf(p);

			int value = PlayerManager.getInstance().kitOf(p).getValeurCap();

			if(!teamsIn.containsKey(t))
				teamsIn.put(t, value);
			else
				teamsIn.replace(t, teamsIn.get(t) + value);

			if(majorityTeam == null || teamsIn.get(t) > teamsIn.get(majorityTeam))
				majorityTeam = t;
		}

		if(majorityTeam != null)
		{
			for(Team t : teamsIn.keySet())
			{
				if(teamsIn.get(t) == teamsIn.get(majorityTeam) && t != majorityTeam)
				{
					majorityTeam = null;
					break;
				}
			}
		}

		wasThereLastSec = playersIn;

		if(majorityTeam != null)
		{
			for(Block b : clayOrWool)
			{
				if(!(b.getType().equals(Material.WOOL) && ((Wool)b.getState().getData()).getColor().equals(majorityTeam.getDyeColor())))
				{
					b.setType(Material.WOOL);

					BlockState blockState = b.getState();
					blockState.setData(new Wool(majorityTeam.getDyeColor()));
					blockState.update();
				}
			}
		}

		else
		{
			for(Block b : clayOrWool)
			{
				if(b.getType().equals(Material.WOOL))
				{
					b.setType(Material.STAINED_CLAY);
					b.setData((byte)12);
				}
			}
		}

		if(majorityTeam != null && (ancientTeam == null || !majorityTeam.equals(ancientTeam)) && (timeLastCapture.get(majorityTeam) == null || timeLastCapture.get(majorityTeam) > 60))
			for(Player p : zm.getWorld().getPlayers())
				Title.sendTitle(p, 5, 30, 5, majorityTeam.getPrefix() + "L'�quipe " + majorityTeam.getName(true).toUpperCase(), majorityTeam.getPrefix() + " capture la zone");

		if(majorityTeam != null)
		{
			zm.setPurcent(majorityTeam, zm.getPurcent(majorityTeam) + ((double)delay) / 80 * teamsIn.get(majorityTeam));
			timeLastCapture.put(majorityTeam, 0l);
		}

		if(zm.getPurcent(majorityTeam) >= 100)
			gm.victory(majorityTeam);

		if(!gm.isGameStarted())
			cancel();

		ancientTeam = majorityTeam;
	}

	@Override
	public synchronized void cancel() throws IllegalStateException
	{
		super.cancel();

		for(Player p : wasThereLastSec)
			StatsManager.getInstance().stopCapFor(p);
	}
}
