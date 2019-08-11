package fr.lumin0u.vertix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.commands.GetKit;
import fr.lumin0u.vertix.commands.GetWeapon;
import fr.lumin0u.vertix.commands.TfCommand;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.managers.StatsManager;
import fr.senseijuba.survivor.utils.NMSUtils;
import fr.lumin0u.vertix.weapons.WeaponManager;

public class TF extends JavaPlugin
{
	private PlayerManager playerManager;
	private WeaponManager weaponManager;
	private ListenerClass l;
	private HashMap<World, GameManager> gameManagers;
	private StatsManager statsManager;
	private List<World> worlds;
	private List<String> tips;

	private static TF instance;
	
	@Override
	public void onEnable()//TODO
	{
		instance = this;

		registerNMSClasses();
		
		l = new ListenerClass();

		getWhatsInConfig();
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				TfCommand tfcommand = new TfCommand();
				
				getServer().getPluginManager().registerEvents(l, instance);
				getCommand("getWeapon").setExecutor(new GetWeapon());
				getCommand("getKit").setExecutor(new GetKit());
				getCommand("Tf").setExecutor(tfcommand);
				getCommand("Tf").setTabCompleter(tfcommand);
				
				for(Player p : Bukkit.getOnlinePlayers())
				{
					p.setNoDamageTicks(20);
					
					if(gameManagers.get(p.getWorld()) != null && gameManagers.get(p.getWorld()).getLobbySpawnPoint() != null)
						p.setCompassTarget(gameManagers.get(p.getWorld()).getLobbySpawnPoint());
					
					else
						p.setCompassTarget(p.getLocation());
				}
			}
		}.runTaskLater(this, 20);
		
		new BukkitRunnable()
		{
			@Override
			public void run()//SAVES
			{
				if(!TfCommand.configDebug)
					for(World w : gameManagers.keySet())
						gameManagers.get(w).saveInConfig();
				
				else
					getWhatsInConfig();
			}
		}.runTaskTimer(this, 1200, 1200);
	}
	
	public void getWhatsInConfig()
	{
		if(gameManagers != null)
			for(World w : gameManagers.keySet())
				gameManagers.get(w).delete();
		
		FileConfiguration file = null;

		File check = new File(getDataFolder(), "config.yml");

		if(!getDataFolder().exists())
			getDataFolder().mkdir();

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

		worlds = new ArrayList<>();
		tips = new ArrayList<>();
		
		if(file.contains("tips"))
			for(String tip : file.getStringList("tips"))
				tips.add(tip);
		
		for(String worldName : file.getKeys(false))
		{
			String name = worldName;
			
			try {
				if(Bukkit.getWorld(name) == null && !name.equals("tips"))
					Bukkit.createWorld(new WorldCreator(name));
				
				if(file.getConfigurationSection(name).contains("enable") && file.getBoolean(name+".enable"))
					worlds.add(Bukkit.getWorld(name));
			}
			catch(Exception e)
			{
				worlds.remove(null);
			}
		}
		
		weaponManager = new WeaponManager();
		statsManager = new StatsManager();
		playerManager = new PlayerManager();
		
		gameManagers = new HashMap<>();
		
		for(World w : worlds)
		{
			GameType gt = GameType.PAYLOADS;
			
			if(getConfig(w) != null && getConfig(w).contains("gametype"))
				gt = GameType.byName(getConfig(w).getString("gametype"));
			
			gameManagers.put(w, new GameManager(w, gt));
		}
	}

	private void registerNMSClasses()
	{
		try
		{
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutPlayerListHeaderFooter");
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent");
			NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent$ChatSerializer");
			NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutChat");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
			NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
			NMSUtils.register("org.bukkit.craftbukkit._version_.entity.CraftPlayer");
			NMSUtils.register("net.minecraft.server._version_.EntityPlayer");
			NMSUtils.register("net.minecraft.server._version_.EntityHuman");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
			NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle$EnumTitleAction");
			NMSUtils.register("net.minecraft.server._version_.Packet");
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		PlayerManager.getInstance().saveInFile();
		
		for(World wo : gameManagers.keySet())
			gameManagers.get(wo).saveInConfig();
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			playerManager.deleteThingsLikeMineOrTurretOf(p);
		}
	}

	public static TF getInstance()
	{
		return instance;
	}
	
	public List<World> getWorlds()
	{
		return worlds;
	}
	
	public void addWorld(World w)
	{
		worlds.add(w);
		
		if(getConfig(w).contains("gametype"))
			gameManagers.put(w, new GameManager(w, GameType.byName(getConfig(w).getString("gametype"))));
		
		else
			gameManagers.put(w, new GameManager(w, GameType.PAYLOADS));
		
//		cartManagers.put(w, new CartManager(w));
		
		FileConfiguration file = getConfig();
		
		if(!file.contains(w.getName()))
			file.createSection(w.getName()).set("enable", true);
		
		else
			file.set(w.getName()+".enable", true);
		
		saveTheConfig(file);
		saveConfig();
	}
	
	public void rmWorld(World w)
	{
		worlds.remove(w);
		
		gameManagers.get(w).delete();
		
		gameManagers.remove(w);
		
		FileConfiguration file = getConfig();
		
		file.set(w.getName(), null);
		
		saveTheConfig(file);
		saveConfig();
	}
	
	public void disableWorld(World w)
	{
		worlds.remove(w);
		
		gameManagers.get(w).delete();
		
		gameManagers.remove(w);
		
		FileConfiguration file = getConfig();
		
		file.set(w.getName()+".enable", false);
		
		saveTheConfig(file);
		saveConfig();
	}

	public PlayerManager getPlayerManager()
	{
		return playerManager;
	}

	public WeaponManager getWeaponManager()
	{
		return weaponManager;
	}

	public ListenerClass getListener()
	{
		return l;
	}

	public GameManager getGM(World w)
	{
		return gameManagers.get(w);
	}

	public StatsManager getStatsManager()
	{
		return statsManager;
	}
	
	public void saveTips()
	{
		FileConfiguration f = getConfig();
		
		f.set("tips", tips);
		
		saveTheConfig(f);
	}

	public void saveTheConfig(FileConfiguration f)
	{
		try
		{
			f.save("config.yml");
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		saveConfig();
	}

	public void saveTheConfig(ConfigurationSection f, World w)
	{
		FileConfiguration file = getConfig();
		
		file.set(w.getName(), f);
		
		try
		{
			file.save("config.yml");
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		
		saveConfig();
	}

	public static void debug(Object debug)
	{
		Bukkit.broadcastMessage("�6[TF2 DEBUG] �r" + debug);
	}
	
	public ConfigurationSection getConfig(World w)
	{
		if(getConfig().getConfigurationSection(w.getName()) != null)
			return getConfig().getConfigurationSection(w.getName());
		
		else
			return getConfig().createSection(w.getName());
	}
	
	public static void copyWorld(File source, File target)
    {
        try
        {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName()))
            {
                if(source.isDirectory())
                {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for(String file : files)
                    {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                }
                else
                {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }
	
	public List<String> getTips()
	{
		return tips;
	}
	
	public GameManager reinitializeGameManager(GameManager gm)
	{
		World w = gm.getWorld();
		GameType gt = gm.getGameType();
		
		gm.delete();
		gm = new GameManager(w, gt);
		
		gameManagers.replace(w, gm);
		
		return gm;
	}
}
