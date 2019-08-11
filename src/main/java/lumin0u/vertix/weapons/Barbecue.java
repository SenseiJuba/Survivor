package fr.lumin0u.vertix.weapons;

import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.FireCause;
import fr.lumin0u.vertix.utils.Utils;

public class Barbecue extends AbstractWeapon
{
	private HashMap<Player, BukkitRunnable> runnables = new HashMap<>();
	
	public Barbecue()
	{
		super("Barbecue", Material.STONE_HOE, 32, 1, 4, false, "guns.lanceflammes", 20, "§6Enflammez vos ennemis");
	}
	
	public void shoot(Player p)
	{
		Utils.playSound(p.getLocation(), "guns.lanceflammes", getMaxDistance());
		
		for(Player ent : p.getWorld().getPlayers())
		{
			if(GameManager.getInstance(p.getWorld()).sameTeam(ent, p))
				continue;
			
			if(ent.getEyeLocation().distance(p.getEyeLocation()) < 1 && ent.getEyeLocation().distance(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection())) < 1 && ent.getFireTicks() < 80 && ((double)Utils.pointsBetween(ent.getEyeLocation(), p.getEyeLocation(), null).size()) < 1)
			{
				ent.setFireTicks(80);
				PlayerManager.getInstance().getTFPlayer(ent).addDamager(p);
				PlayerManager.getInstance().getTFPlayer(ent).setFireCause(new FireCause(false, p.getUniqueId(), this));
			}
			
			if(ent.getEyeLocation().distance(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().clone().multiply(3))) < 1.75 && ent.getFireTicks() < 80 && ((double)Utils.pointsBetween(ent.getEyeLocation(), p.getEyeLocation(), null).size()) < 1)
			{
				ent.setFireTicks(80);
				PlayerManager.getInstance().getTFPlayer(ent).addDamager(p);
				PlayerManager.getInstance().getTFPlayer(ent).setFireCause(new FireCause(false, p.getUniqueId(), this));
			}
			
			if(ent.getLocation().distance(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().clone().multiply(5))) < 2.5 && ent.getFireTicks() < 80 && ((double)Utils.pointsBetween(ent.getEyeLocation(), p.getEyeLocation(), null).size()) < 1)
			{
				ent.setFireTicks(80);
				PlayerManager.getInstance().getTFPlayer(ent).addDamager(p);
				PlayerManager.getInstance().getTFPlayer(ent).setFireCause(new FireCause(false, p.getUniqueId(), this));
			}
		}
		
//		Random r = new Random();
		Location pLoc = p.getEyeLocation().clone().add(p.getEyeLocation().clone().getDirection().multiply(0.2));
		
		for(double i = 0; i < 8; i++)
		{
//			for(int k = 0; k < 20; k++)
//			{
//				Location start = new Location(p.getWorld(), pLoc.getX()+r.nextDouble()/3, pLoc.getY()+r.nextDouble()/3, pLoc.getZ()+r.nextDouble()/3);
//				double x = (pLoc.getDirection().clone().getX()+r.nextDouble()-0.5)/2;
//				double y = (pLoc.getDirection().clone().getY()+r.nextDouble()-0.5)/2;
//				double z = (pLoc.getDirection().clone().getZ()+r.nextDouble()-0.5)/2;
//				
//				start.getWorld().spigot().playEffect(start, Effect.FLAME, 0, 0, (float)x, (float)y, (float)z, 1f, 0, 200);
//			}
			
//			if(!p.getEyeLocation().clone().getDirection().multiply(0.4*i+1).toLocation(p.getWorld()).getBlock().getType().equals(Material.AIR))
//				break;

			for(int j = 0; j < 22; j++)
			{
				Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
				Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
				
				Location l = pLoc.clone().add(p.getEyeLocation().getDirection().multiply(0.4*i+1)).add(x1.clone().multiply((i/8+0.25) * Math.sin((double)j / 20 * Math.PI * 2d))).add(x2.clone().multiply((((double)i/8)+0.25) * Math.cos((double)j / 20 * Math.PI * 2d)));
				
				if(((double)Utils.pointsBetween(l, p.getEyeLocation(), null).size()) < 1)
					p.getWorld().spigot().playEffect(l, Effect.FLAME, 0, 0, (float)p.getEyeLocation().getDirection().getX()/4, (float)p.getEyeLocation().getDirection().getY()/4, (float)p.getEyeLocation().getDirection().getZ()/4, 1, 0, 100);
			}
		}
		
		reload(p, this);
	}
	
	public void reload(Player p, Barbecue w)
	{
		if(runnables.get(p) != null)
			runnables.get(p).cancel();
		
		runnables.put(p, new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(!p.getInventory().contains(w.getItem(1).getType()) || p.getInventory().getItem(p.getInventory().first(w.getItem(1).getType())).getAmount() >= 32)
				{
					cancel();
					return;
				}
				
				int place = p.getInventory().first(w.getItem(1).getType());
				int ammo = p.getInventory().getItem(place).getAmount();
				
				p.getInventory().remove(w.getItem(1).getType());
				p.getInventory().setItem(place, w.getItem(ammo+1));
			}
		});
		
		runnables.get(p).runTaskTimer(TF.getInstance(), 45, 8);
	}
}
