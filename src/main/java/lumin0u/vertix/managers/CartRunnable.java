package fr.lumin0u.vertix.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.Team;

public class CartRunnable extends BukkitRunnable
{
	private Minecart cart;
	private Location lastPos;
	private GameManager gm;
	private CartManager cm;
	private Team t;
	private List<Player> wasThereLastSec;
	private boolean terminusSaid;
	private static HashMap<Minecart, Integer> hitCarts = new HashMap<>();
	
	public CartRunnable(Minecart cart, Team t)
	{
		this.cart = cart;
		this.t = t;
		
		lastPos = cart.getLocation().getBlock().getLocation();
		
		gm = GameManager.getInstance(t.getWorld());
		cm = CartManager.getInstance(t.getWorld());
		
		wasThereLastSec = new ArrayList<>();
		terminusSaid = false;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		Location railsLoc = cart.getLocation().getBlock().getLocation();
		
		List<Player> playersNear = new ArrayList<>();

		for(Player p : cart.getWorld().getPlayers())
		{
			if(p.getLocation().distance(cart.getLocation()) < 4 && gm.getTeamOf(p).equals(t))
			{
				playersNear.add(p);
				StatsManager.getInstance().startPushFor(p);
			}
		}

		for(Player p : wasThereLastSec)
		{
			if(!playersNear.contains(p))
			{
				StatsManager.getInstance().stopPushFor(p);
			}
		}

		wasThereLastSec = playersNear;

		if(cart.getLocation().getBlock().getLocation().clone().add(0, -1, 0).getBlock().getType().equals(Material.STAINED_CLAY))
		{
			cart.getWorld().getBlockAt(cart.getLocation().clone().add(0, -1, 0)).setType(Material.WOOL);
			
			BlockState blockState = cart.getWorld().getBlockAt(cart.getLocation().clone().add(0, -1, 0)).getState();
			blockState.setData(new Wool(t.getDyeColor()));
			blockState.update();
		}

		if(cm.getEnd(t).equals((railsLoc)))
		{
			cm.setPurcent(t, 100);
			gm.victory(t);
			cancel();
			return;
		}

		if(cm.getRails(t).contains(railsLoc))
		{
			cm.setPurcent(t, (int)((((double)cm.getRails(t).indexOf(railsLoc) + 1) * 100) / (double)cm.getRails(t).size()));
		}
		
		if(cm.getFinalTerminus(t) != null && cm.getFinalTerminus(t).equals(railsLoc) && !terminusSaid)
		{
			terminusSaid = true;

			for(Player p : cart.getWorld().getPlayers())
			{
				if(gm.getTeamOf(p).equals(t))
					p.playSound(railsLoc, "minecart.galerte", 100, 1);
				else
					p.playSound(railsLoc, "minecart.alerte", 100, 1);
			}
		}

		if(!cm.getRails(t).contains(railsLoc))
			return;

		double valeur = 0;

		for(Player p : cart.getWorld().getPlayers())
		{
			if(p.getLocation().distance(cart.getLocation()) < 4 && gm.getTeamOf(p).equals(t))
			{
				valeur += PlayerManager.getInstance().getTFPlayer(p).getKit().getValeurCart() * (PlayerManager.getInstance().getTFPlayer(p).getpower() != null && PlayerManager.getInstance().getTFPlayer(p).getpower().equals(SuperPower.PUSHER) ? 2 : 1);
			}
			
			else if(p.getLocation().distance(cart.getLocation()) < 4 && PlayerManager.getInstance().getTFPlayer(p).getpower() != null && PlayerManager.getInstance().getTFPlayer(p).getpower().equals(SuperPower.DEFENDER) && !gm.getTeamOf(p).equals(t))
				valeur -= 0.5;
		}
		
		if(valeur == 1)
			valeur += 0.5;

		if(cm.getRails(t).contains(railsLoc))
		{
			Location fineRailsLoc = railsLoc.clone().add(0.5, 0, 0.5);
			
			Location nextRails = cm.getRails(t).get(cm.getRails(t).indexOf(railsLoc) + 1).clone().add(0.5, 0, 0.5);
			
//			TF.debug(valeur);
			
			Vector newVelo = nextRails.toVector().subtract(fineRailsLoc.toVector()).multiply(0.08 * valeur);
			Vector dir = nextRails.toVector().subtract(fineRailsLoc.toVector()).normalize();

			cart.setDerailedVelocityMod(newVelo.clone().multiply(-10));
			cart.setFlyingVelocityMod(newVelo.clone().multiply(-10));
			
			if((valeur >= 2 || nextRails.getY() <= fineRailsLoc.getY()) && cart.getVelocity().length() < newVelo.length())
				cart.setVelocity(newVelo);
			
			else if(cart.getVelocity().length() < newVelo.length())
				cart.setVelocity(cart.getVelocity().multiply(0.95));

			else if(valeur < 2 && nextRails.getY() > fineRailsLoc.getY() && cart.getVelocity().normalize().equals(dir))
				cart.setVelocity(new Vector());
			
			if(hitCarts.containsKey(cart))
			{
				cart.setVelocity(new Vector());
				hitCarts.replace(cart, hitCarts.get(cart)+1);
				
				if(hitCarts.get(cart) >= 6)
					hitCarts.remove(cart);
			}
			
			if(!lastPos.equals(cart.getLocation().getBlock().getLocation()))
			{
				PlayerManager pm = PlayerManager.getInstance();
				
				for(Player p : cm.getWorld().getPlayers())
				{
					if(cm != null && pm.getTFPlayer(p).getTeamCartDirection() != null && cm.getCart(pm.getTFPlayer(p).getTeamCartDirection()) != null && cm.getCart(pm.getTFPlayer(p).getTeamCartDirection()).getLocation() != null)
						p.setCompassTarget(cm.getCart(pm.getTFPlayer(p).getTeamCartDirection()).getLocation());
				}
			}
			
			lastPos = cart.getLocation().getBlock().getLocation();
			
//			TF.debug(cart.getVelocity());
		}
		
		if(!gm.isGameStarted())
		{
			cart.remove();
			cancel();
		}
	}
	
	@Override
	public synchronized void cancel() throws IllegalStateException
	{
		super.cancel();
		
		for(Player p : wasThereLastSec)
			StatsManager.getInstance().stopPushFor(p);
	}
	
	public static void addHitCart(Minecart cart)
	{
		hitCarts.put(cart, 0);
	}
}
