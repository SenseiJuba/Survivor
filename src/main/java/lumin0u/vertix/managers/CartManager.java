package fr.lumin0u.vertix.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.utils.Utils;

public class CartManager
{
	private HashMap<Team, LinkedList<Location>> rails;
	private HashMap<Team, LinkedList<Location>> bifurcs;
	private HashMap<Team, Location> start;
	private HashMap<Team, Location> end;
	private HashMap<Team, Location> finalTerminus;
	private HashMap<Team, Minecart> cart;
	private HashMap<Team, Integer> purcent;
	private HashMap<Team, CartRunnable> cartRunnables;
	private World w;
	private GameManager gm;
	
	public CartManager(World w)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				init(w);
			}
		}.runTaskLater(TF.getInstance(), 1);
	}
	
	@SuppressWarnings({"unchecked", "deprecation"})
	public void init(World w)
	{
		this.w = w;
		
		gm = GameManager.getInstance(w);//ATTENTION INIT APRES GAMEMANAGER
		
		if(!gm.getGameType().isCarts() || !gm.getGameType().areTeamsActive())
			return;
		
		rails = new HashMap<>();
		bifurcs = new HashMap<>();
		start = new HashMap<>();
		end = new HashMap<>();
		finalTerminus = new HashMap<>();
		cart = new HashMap<>();
		purcent = new HashMap<>();
		cartRunnables = new HashMap<>();

		HashMap<Team, List<String>> bifurcsS = new HashMap<>();
		
		for(Team t : gm.getTeams())
		{
			purcent.put(t, 0);
			
			rails.put(t, new LinkedList<>());

			bifurcs.put(t, new LinkedList<>());
			
			bifurcsS.put(t, new ArrayList<>());
		}

//		TF.debug(TF.getInstance());
//		TF.debug(TF.getInstance().getConfig(w));
		
		ConfigurationSection f = TF.getInstance().getConfig(w);
		
		for(Team t : gm.getTeams())
		{
			if(f.contains(t.getName(false)+".bifurcs"))
				bifurcsS.replace(t, (List<String>) f.getList(t.getName(false)+".bifurcs"));
			
			if(f.contains(t.getName(false)+".startRails"))
				start.put(t, Utils.stringToLoc(f.getString(t.getName(false)+".startRails")));
			
			if(f.contains(t.getName(false)+".endRails"))
				end.put(t, Utils.stringToLoc(f.getString(t.getName(false)+".endRails")));
			
			if(f.contains(t.getName(false)+".finalterminus"))
				finalTerminus.replace(t, Utils.stringToLoc(f.getString(t.getName(false)+".finalterminus")));
			
			for(String s : bifurcsS.get(t))
			{
				bifurcs.get(t).add(Utils.stringToLoc(s));
			}
			
			if(start.get(t) != null && end.get(t) != null)
				recalculateRails(t);
		}
		
		Collection<Minecart> carts = w.getEntitiesByClass(Minecart.class);
		
		for(Team t : gm.getTeams())
		{
			for(Location loc : rails.get(t))
			{
				for(Minecart cart : carts)
					if(cart.getLocation().distance(loc) < 2)
						cart.remove();
				
				loc.clone().add(0, -1, 0).getBlock().setType(Material.STAINED_CLAY);
				loc.clone().add(0, -1, 0).getBlock().setData((byte)12);
			}
		}
	}
	
	public LinkedList<Location> getRails(Team t)
	{
		if(rails.containsKey(t))
			return rails.get(t);
		
		else
			return new LinkedList<>();
	}
	
	public void rmRails(Team t)
	{
		if(rails.containsKey(t))
			rails.replace(t, new LinkedList<>());
		
		else
			rails.put(t, new LinkedList<>());
	}
	
	public LinkedList<Location> getBifurcs(Team t)
	{
		if(bifurcs.containsKey(t))
			return bifurcs.get(t);
		
		else
			return new LinkedList<>();
	}

	public void setBifurcs(Team t, LinkedList<Location> bifurcs)
	{
		if(this.bifurcs.containsKey(t))
			this.bifurcs.replace(t, bifurcs);
		
		else
			this.bifurcs.put(t, bifurcs);
	}

	public void setStart(Team t, Location l)
	{
		if(this.start.containsKey(t))
			this.start.replace(t, l.clone());
		
		else
			this.start.put(t, l.clone());
	}
	
	public void setEnd(Team t, Location l)
	{
		if(this.end.containsKey(t))
			this.end.replace(t, l.clone());
		
		else
			this.end.put(t, l.clone());
	}
	
	public Location getStart(Team t)
	{
		return start.get(t);
	}

	public Location getEnd(Team t)
	{
		return end.get(t);
	}
	
	public LinkedList<Location> recalculateRails(Team t)
	{
		int indexBifurc = 0;
		Location lastLoc = start.get(t);
		
		rails.replace(t, new LinkedList<>());
		rails.get(t).add(lastLoc);
		
		Location nearestRail;
		
		Location loc;
		
		for(int i = 0; i < 1000; i++)
		{
			nearestRail = null;
			
			xloop: for(int x = -1; x < 2; x++)
			{
				for(int y = -1; y < 2; y++)
				{
					for(int z = -1; z < 2; z++)
					{
						loc = lastLoc.clone().add(x, y, z);

						
						if(!rails.get(t).contains(loc) && loc.getBlock().getType().equals(Material.RAILS) && (nearestRail == null || lastLoc.distance(loc) < lastLoc.distance(nearestRail)))
						{
							nearestRail = loc.clone();
							
//							if(!bifurcs.get(t).isEmpty())
//								TF.debug(bifurcs.get(t).contains(nearestRail));
						}
						
						if(bifurcs.get(t).contains(nearestRail))
							break xloop;
					}
				}
			}
			
//			TF.debug(nearestRail);
			
			if(nearestRail == null)
			{
				if(bifurcs.get(t).size() > 0)
					nearestRail = bifurcs.get(t).get(indexBifurc);
				
				else
					break;
				
				indexBifurc++;
			}
			
			rails.get(t).add(nearestRail);
			
			if(nearestRail.equals(end.get(t)))
				break;
			
			lastLoc = nearestRail;
		}
		
		return rails.get(t);
	}

	public void saveInConfig()
	{
		ConfigurationSection f = TF.getInstance().getConfig(w);
		
		for(Team t : gm.getTeams())
		{
			if(start.get(t) != null)
				f.set(t.getName(false)+".startRails", Utils.locToString(start.get(t)));
			
			if(end.get(t) != null)
				f.set(t.getName(false)+".endRails", Utils.locToString(end.get(t)));
			
			
			List<String> bifurcsS = new ArrayList<>();
			
			if(bifurcs.get(t) != null)
				for(Location bifurc : bifurcs.get(t))
					bifurcsS.add(Utils.locToString(bifurc));

			if(!bifurcsS.isEmpty())
				f.set(t.getName(false)+".bifurcs", bifurcsS);
			
			if(finalTerminus.get(t) != null)
				f.set(t.getName(false)+".finalterminus", Utils.locToString(finalTerminus.get(t)));
		}
		
		TF.getInstance().saveTheConfig(f, w);
	}
	
	public static CartManager getInstance(World w)
	{
		if(GameManager.getInstance(w) != null)
			return GameManager.getInstance(w).getCartManager();
		
		else
			return null;	
	}
	
	public static CartManager getInstance(GameManager gm)
	{
		return gm.getCartManager();
	}
	
	public Minecart getCart(Team t)
	{
		return cart.get(t);
	}
	
	public List<Minecart> getCarts()
	{
		List<Minecart> carts = new ArrayList<>();
		
		for(Team t : cart.keySet())
			carts.add(cart.get(t));
		
		return carts;
	}
	
	public Location getFinalTerminus(Team t)
	{
		return finalTerminus.get(t);
	}
	
	public int setFinalTerminus(Team t, Location finalTerminus)
	{
		if(rails.get(t).contains(finalTerminus))
		{
			this.finalTerminus.put(t, finalTerminus);
			return rails.get(t).indexOf(finalTerminus);
		}
		
		else
			return -1;
	}
	
	public int getPurcent(Team t)
	{
		if(purcent.get(t) != null)
			return purcent.get(t);
		
		return 0;
	}

	public void setPurcent(Team t, int purcent)
	{
		this.purcent.replace(t, purcent);
	}

	@SuppressWarnings("deprecation")
	public void onGameStart()
	{
		Collection<Minecart> carts = w.getEntitiesByClass(Minecart.class);
		
		for(Team t : gm.getTeams())
		{
			for(Location loc : rails.get(t))
			{
				for(Minecart cart : carts)
					if(cart.getLocation().distance(loc) < 2)
						cart.remove();
				
				loc.clone().add(0, -1, 0).getBlock().setType(Material.STAINED_CLAY);
				loc.clone().add(0, -1, 0).getBlock().setData((byte)12);
			}
			
			cart.put(t, (Minecart)w.spawnEntity(start.get(t).clone().add(0.5, 0, 0.5), EntityType.MINECART));
			cart.get(t).setDisplayBlock(new MaterialData(t.getBlockInCart()));
			
			CartRunnable cr = new CartRunnable(cart.get(t), t);
			
			cr.runTaskTimer(TF.getInstance(), 1, 1);
			
			cartRunnables.put(t, cr);
		}
	}
	
	public World getWorld()
	{
		return w;
	}
	
	public boolean areCartsCreated()
	{
		if(cart == null || cart == new HashMap<Team, Minecart>())
			return false;
		
		for(Team t : gm.getTeams())
			if(cart.get(t) == null)
				return false;
		
		return true;
	}
	
	public CartRunnable getCartRunnable(Team t)
	{
		return cartRunnables.get(t);
	}
	
	public void delete()
	{
		for(Team t : cartRunnables.keySet())
			cartRunnables.get(t).cancel();
	}
}
