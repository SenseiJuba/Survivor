package fr.lumin0u.vertix.weapons.guns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.FireCause;
import fr.lumin0u.vertix.utils.Utils;

public class FuseeDeDetresse extends AbstractGun
{
	public FuseeDeDetresse()
	{
		super("Pistolet de Detresse", Material.DIAMOND_SPADE, 1, 3.9, 1, 0, 10, 0, false, "guns.detresse", 15, 0);
	}

	public void shoot(Player p)
	{
		Location start = p.getEyeLocation();
		Vector increase = start.getDirection();
		
		increase.multiply(0.25);

		Utils.playSound(p.getLocation(), sound, getMaxDistance());
		
		FuseeDeDetresse instance = this;
		
		new BukkitRunnable()
		{
			int nbTours = -1;
			boolean stop = false;
			List<Entity> alreadyFired = new ArrayList<>();
			
			@Override
			public void run()
			{
				for(int a = 0; a < 3; a++)
				{
					nbTours++;
					
					Location point = start.add(increase);
					
//					Random r = new Random();
					
//					for(int i = 0; i < 30; i++)
//					{
//						double x = point.getX()+((r.nextDouble()%2)-0.5);
//						double y = point.getY()+((r.nextDouble()%2)-0.5);
//						double z = point.getZ()+((r.nextDouble()%2)-0.5);
//						
//						if(new Location(point.getWorld(), x, y, z).distance(point) < 0.6)
//							p.getWorld().spigot().playEffect(new Location(point.getWorld(), x, y, z), Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 1, 0, 10);
//						
//						else
//							i--;
//					}
					
					for(int j = 0; j < 22; j++)
					{
//						Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
//						Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
//						Location l = l1.clone().add(x1.clone().multiply(multi * Math.sin((double)j / 20 * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos((double)j / 20 * Math.PI * 2d)));
						
						Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
						Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
						
						Location l = point.clone().add(x1.clone().multiply(0.4 * Math.sin((double)j / 20 * Math.PI * 2d))).add(x2.clone().multiply(0.4 * Math.cos((double)j / 20 * Math.PI * 2d)));
						p.getWorld().spigot().playEffect(l, Effect.COLOURED_DUST, 0, 0, 0, 0, 0, 1, 0, 100);
					}
	
					stop = false;
	
					for(Player ent : p.getWorld().getPlayers())
					{
						if(ent.getLocation().distance(point) < 2.5 && !GameManager.getInstance(ent.getWorld()).sameTeam(p, ent))
						{
							if(!(point.getY()-1.5 > ent.getLocation().getY()) && ent.getLocation().distance(point) > 1.5)
								break;
							
							if(!alreadyFired.contains(ent))
							{
								Location newPoint = new Location(p.getWorld(), point.getX(), ent.getLocation().getY(), point.getZ());
								
								if(newPoint.distance(ent.getLocation()) > 1.25)
									break;
								
								ent.setFireTicks(80);
								
								PlayerManager.getInstance().getTFPlayer(ent).setFireCause(new FireCause(false, p.getUniqueId(), instance));
								
								alreadyFired.add(ent);
								
								break;
							}
						}
					}
					
					List<Material> transparent = new ArrayList<>();
					transparent.add(Material.LEAVES);
					transparent.add(Material.LEAVES_2);
					
					for(Material mat : Material.values())
						if(mat.isTransparent())
							transparent.add(mat);
					
					if(!point.getBlock().getType().equals(Material.AIR) && !point.getBlock().isLiquid() && !transparent.contains(point.getBlock().getType()))
					{
						stop = true;
					}
	
					if(stop)
						this.cancel();
					
					if(nbTours == 40)
						this.cancel();
				}
			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);
	}
	
	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();
		
		loree.add("§6Durée du feu : §a4s");
		loree.add("§6Range : §a"+range);
		loree.add("§6Recharge : §a"+timeCharging+"s");
		
		return loree;
	}
}
