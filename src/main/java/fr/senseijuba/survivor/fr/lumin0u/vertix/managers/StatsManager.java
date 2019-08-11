package fr.lumin0u.vertix.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;

public class StatsManager
{
	private FileConfiguration file;

	private HashMap<String, Integer> kills;
	private HashMap<String, Integer> deaths;
	private HashMap<String, Integer> secPush;
	private HashMap<String, Integer> secPlayed;
	private HashMap<String, Integer> secCap;
	private HashMap<String, Integer> victories;
	private HashMap<String, Integer> defeats;
	private HashMap<String, Double> damage;
	private HashMap<UUID, BukkitRunnable> pushPlayerTasks;
	private HashMap<UUID, BukkitRunnable> capPlayerTasks;
	
	public StatsManager()
	{
		kills = new HashMap<>();
		deaths = new HashMap<>();
		secPush = new HashMap<>();
		secPlayed = new HashMap<>();
		secCap = new HashMap<>();
		victories = new HashMap<>();
		defeats = new HashMap<>();
		damage = new HashMap<>();
		pushPlayerTasks = new HashMap<>();
		capPlayerTasks = new HashMap<>();

		File check = new File(TF.getInstance().getDataFolder(), "stats.yml");

		if(!TF.getInstance().getDataFolder().exists())
			TF.getInstance().getDataFolder().mkdir();

		if(!check.exists())
		{
			try
			{
				check.createNewFile();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			file = YamlConfiguration.loadConfiguration(check);
		}catch(NullPointerException ex)
		{
			ex.printStackTrace();
		}
	}

	public void saveInFile()
	{
		for(String p : kills.keySet())
			if(kills.get(p) != null)
				file.set("kills."+p, kills.get(p)+(file.contains("kills."+p) ? file.getInt("kills."+p) : 0));
		
		for(String p : deaths.keySet())
			if(deaths.get(p) != null)
				file.set("deaths."+p, deaths.get(p)+(file.contains("deaths."+p) ? file.getInt("deaths."+p) : 0));

		for(String p : damage.keySet())
			if(damage.get(p) != null)
				file.set("damage."+p, damage.get(p)+(file.contains("damage."+p) ? file.getDouble("damage."+p) : 0));

		for(String p : secPush.keySet())
			if(secPush.get(p) != null)
				file.set("secPush."+p, secPush.get(p)+(file.contains("secPush."+p) ? file.getInt("secPush."+p) : 0));

		for(String p : secPlayed.keySet())
			if(secPlayed.get(p) != null)
				file.set("secPlayed."+p, secPlayed.get(p)+(file.contains("secPlayed."+p) ? file.getInt("secPlayed."+p) : 0));

		for(String p : secCap.keySet())
			if(secCap.get(p) != null)
				file.set("secCap."+p, secCap.get(p)+(file.contains("secCap."+p) ? file.getInt("secCap."+p) : 0));

		for(String p : victories.keySet())
			if(victories.get(p) != null)
				file.set("victories."+p, victories.get(p)+(file.contains("victories."+p) ? file.getInt("victories."+p) : 0));

		for(String p : defeats.keySet())
			if(defeats.get(p) != null)
				file.set("defeats."+p, defeats.get(p)+(file.contains("defeats."+p) ? file.getInt("defeats."+p) : 0));
		
		
		kills = new HashMap<>();
		deaths = new HashMap<>();
		secPush = new HashMap<>();
		secPlayed = new HashMap<>();
		damage = new HashMap<>();
		secCap = new HashMap<>();
		victories = new HashMap<>();
		defeats = new HashMap<>();
		
		try
		{
			file.save(new File(TF.getInstance().getDataFolder(), "stats.yml"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void addKill(Player p, int nbKills)
	{
		if(kills.containsKey(p.getName().toLowerCase()))
			kills.replace(p.getName().toLowerCase(), kills.get(p.getName().toLowerCase()) + nbKills);

		else
			kills.put(p.getName().toLowerCase(), nbKills);
	}

	public void addDeath(Player p)
	{
		if(deaths.containsKey(p.getName().toLowerCase()))
			deaths.replace(p.getName().toLowerCase(), deaths.get(p.getName().toLowerCase()) + 1);

		else
			deaths.put(p.getName().toLowerCase(), 1);
	}

	public void addDamage(Player p, double nbDamage)
	{
		if(damage.containsKey(p.getName().toLowerCase()))
			damage.replace(p.getName().toLowerCase(), damage.get(p.getName().toLowerCase()) + nbDamage);

		else
			damage.put(p.getName().toLowerCase(), nbDamage);
	}

	public void addPush(Player p, int nbPushSec)
	{
		if(secPush.containsKey(p.getName().toLowerCase()))
			secPush.replace(p.getName().toLowerCase(), secPush.get(p.getName().toLowerCase()) + nbPushSec);

		else
			secPush.put(p.getName().toLowerCase(), nbPushSec);
	}

	public void addCap(Player p, int nbCapSec)
	{
		if(secCap.containsKey(p.getName().toLowerCase()))
			secCap.replace(p.getName().toLowerCase(), secCap.get(p.getName().toLowerCase()) + nbCapSec);

		else
			secCap.put(p.getName().toLowerCase(), nbCapSec);
	}

	public void addVictories(Player p, int nbVictories)
	{
		if(victories.containsKey(p.getName().toLowerCase()))
			victories.replace(p.getName().toLowerCase(), victories.get(p.getName().toLowerCase()) + nbVictories);

		else
			victories.put(p.getName().toLowerCase(), nbVictories);
	}

	public void addDefeats(Player p, int nbDefeats)
	{
		if(defeats.containsKey(p.getName().toLowerCase()))
			defeats.replace(p.getName().toLowerCase(), defeats.get(p.getName().toLowerCase()) + nbDefeats);

		else
			defeats.put(p.getName().toLowerCase(), nbDefeats);
	}

	public void addTimePlayed(Player p, int playedSec)
	{
		if(secPlayed.containsKey(p.getName().toLowerCase()))
			secPlayed.replace(p.getName().toLowerCase(), secPlayed.get(p.getName().toLowerCase()) + playedSec);

		else
			secPlayed.put(p.getName().toLowerCase(), playedSec);
	}

	public int getTimePlayedOf(Player p)
	{
		if(secPlayed.containsKey(p.getName().toLowerCase()))
			return secPlayed.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public int getKillsOf(Player p)
	{
		if(kills.containsKey(p.getName().toLowerCase()))
			return kills.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public int getDeathsOf(Player p)
	{
		if(deaths.containsKey(p.getName().toLowerCase()))
			return deaths.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public double getDamageOf(Player p)
	{
		if(damage.containsKey(p.getName().toLowerCase()))
			return damage.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public int getPushSecOf(Player p)
	{
		if(secPush.containsKey(p.getName().toLowerCase()))
			return secPush.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public int getCapSecOf(Player p)
	{
		if(secCap.containsKey(p.getName().toLowerCase()))
			return secCap.get(p.getName().toLowerCase());

		else
			return 0;
	}

	public static StatsManager getInstance()
	{
		return TF.getInstance().getStatsManager();
	}

	public void startPushFor(Player p)
	{
		if(pushPlayerTasks.get(p.getUniqueId()) != null)
			return;
		
		else
		{
			BukkitRunnable task = new BukkitRunnable()
			{
				boolean stop = false;
				
				@Override
				public void run()
				{
					addPush(p, 1);
					
					if(stop)
					{
						pushPlayerTasks.replace(p.getUniqueId(), null);
						super.cancel();
					}
				}
				
				@Override
				public synchronized void cancel() throws IllegalStateException
				{
					stop = true;
				}
			};
			
			task.runTaskTimer(TF.getInstance(), 20, 20);
			
			pushPlayerTasks.put(p.getUniqueId(), task);
		}
	}

	public void stopPushFor(Player p)
	{
		if(pushPlayerTasks.get(p.getUniqueId()) != null)
			pushPlayerTasks.get(p.getUniqueId()).cancel();
	}

	public void startCapFor(Player p)
	{
		if(capPlayerTasks.get(p.getUniqueId()) != null)
			return;
		
		else
		{
			BukkitRunnable task = new BukkitRunnable()
			{
				boolean stop = false;
				
				@Override
				public void run()
				{
					addCap(p, 1);
					
					if(stop)
					{
						capPlayerTasks.replace(p.getUniqueId(), null);
						super.cancel();
					}
				}
				
				@Override
				public synchronized void cancel() throws IllegalStateException
				{
					stop = true;
				}
			};
			
			task.runTaskTimer(TF.getInstance(), 20, 20);
			
			capPlayerTasks.put(p.getUniqueId(), task);
		}
	}

	public void stopCapFor(Player p)
	{
		if(capPlayerTasks.get(p.getUniqueId()) != null)
			capPlayerTasks.get(p.getUniqueId()).cancel();
	}
	
	public double getKDR(Player p)
	{
		if(!kills.containsKey(p.getName()))
			return 0;
		
		else if(!deaths.containsKey(p.getName()) && kills.containsKey(p.getName()))
			return Double.POSITIVE_INFINITY;
		
		else
			return (double)kills.get(p.getName()) / (double)deaths.get(p.getName());
	}
}
