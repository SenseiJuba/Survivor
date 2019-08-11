package fr.lumin0u.vertix.managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.utils.Cuboid;
import fr.lumin0u.vertix.utils.Utils;

public class ZoneManager
{
	private HashMap<Team, Double> purcent;
	private Cuboid zone;
	private World w;
//	private GameManager gm;
	private ZoneRunnable zr;
	
	@SuppressWarnings("deprecation")
	public ZoneManager(World w)
	{
		this.w = w;
//		gm = GameManager.getInstance(w);
		purcent = new HashMap<>();

		ConfigurationSection file = TF.getInstance().getConfig(w);
		
		if(file.contains("zone1") && file.contains("zone2"))
		{
			zone = new Cuboid(Utils.stringToLoc(file.getString("zone1")), Utils.stringToLoc(file.getString("zone2")));
			
			for(Block b : zone.blocksInside())
			{
				if(b.getType().equals(Material.WOOL))
				{
					b.setType(Material.STAINED_CLAY);
					b.setData((byte)12);
				}
			}
		}
	}
	
	public double getPurcent(Team t)
	{
		if(purcent.get(t) != null)
			return purcent.get(t);
		
		else
			return 0;
	}
	
	public void setPurcent(Team t, double purcent)
	{
		this.purcent.put(t, purcent);
	}
	
	public void onGameStart()
	{
		final long delay = 2;
		
		zr = new ZoneRunnable(zone, delay);
		zr.runTaskTimer(TF.getInstance(), delay, delay);
	}
	
	public static ZoneManager getInstance(World w)
	{
		return GameManager.getInstance(w).getZoneManager();
	}
	
	public void setZone(Cuboid zone)
	{
		this.zone = zone;
	}
	
	public Cuboid getZone()
	{
		return zone;
	}
	
	public World getWorld()
	{
		return w;
	}
	
	public void delete()
	{
		if(zr != null)
			zr.cancel();
	}
	
	public void saveInConfig()
	{
		ConfigurationSection f = TF.getInstance().getConfig(w);
		
		if(zone != null)
		{
			f.set("zone1", Utils.locToString(zone.getLoc1()));
			f.set("zone2", Utils.locToString(zone.getLoc2()));
		}
		
		TF.getInstance().saveTheConfig(f, w);
	}
}
