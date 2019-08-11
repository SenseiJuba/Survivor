package fr.lumin0u.vertix.weapons.ultimateWeapons.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.Utils;

public class Scavenger extends AbstractUltimateGun
{
	public Scavenger()
	{
		super("Scavenger", Material.SULPHUR, 2, 10, 0, 100, 0.00001, false, "guns.scavenger", 30, 0, "§6Durée du poison : §a6s", "§6Range : §a100");
	}

	@SuppressWarnings("deprecation")
	public void shoot(Player p)
	{
		Random rand = new Random();
		
		Location start = p.getEyeLocation();
		Vector increase = start.getDirection();
		
		Utils.playSound(p.getLocation(), sound, getMaxDistance());
		
		increase.multiply(0.5);

		boolean stop = false;
		
		increase.setX(increase.getX()+(rand.nextBoolean() ? rand.nextDouble() % precision/10 : 0-rand.nextDouble() % precision/10));
		
		increase.setY(increase.getY()+(rand.nextBoolean() ? rand.nextDouble() % precision/10 : 0-rand.nextDouble() % precision/10));
		
		increase.setZ(increase.getZ()+(rand.nextBoolean() ? rand.nextDouble() % precision/10 : 0-rand.nextDouble() % precision/10));

		for(int counter = 0; counter < 200; counter++)
		{
			Location point = start.add(increase);
			
			for(int i = 0; i < 10; i++)
				p.getWorld().spigot().playEffect(point.clone().add(rand.nextDouble()/2-0.25, rand.nextDouble()/2-0.25, rand.nextDouble()/2-0.25), Effect.COLOURED_DUST, 0, 1, 0.6f, 0.6f, 0.2f, 1f, 0, 100);
			
			stop = false;
			
			for(Player ent : p.getWorld().getPlayers())
			{
				if(GameManager.getInstance(ent.getWorld()).sameTeam(p, ent) || GameManager.getInstance(ent.getWorld()).getTeamOf(ent).getSafeZone().hasInside(ent))
					continue;
				
				if(ent.getLocation().distance(point) < 2)
				{
					if(!(point.getY()-1.6 > ent.getLocation().getY()) && ent.getLocation().distance(point) > 1.6)
						break;
					
					Location newPoint = new Location(p.getWorld(), point.getX(), ent.getLocation().getY(), point.getZ());
					
					if(newPoint.distance(ent.getLocation()) > 0.5)
						break;
					
					PotionEffectType pet = new PotionEffectType(PotionEffectType.POISON.getId())
					{
						@Override
						public boolean isInstant()
						{
							return false;
						}
						
						@Override
						public String getName()
						{
							return "Poison";
						}
						
						@Override
						public double getDurationModifier()
						{
							return 0;
						}
					};
					
					ent.removePotionEffect(pet);
					
					ent.addPotionEffect(new PotionEffect(pet, 100, 2));
					
					p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
					
					PlayerManager.getInstance().getTFPlayer(ent).addDamager(p);
				}
			}
			
			List<Material> transparent = new ArrayList<>();
			transparent.add(Material.LEAVES);
			transparent.add(Material.LEAVES_2);
			transparent.add(Material.BARRIER);
			transparent.add(Material.STEP);
			transparent.add(Material.WOOD_STEP);
			
			for(Material mat : Material.values())
				if(mat.isTransparent())
					transparent.add(mat);
			
			if(!point.getBlock().getType().equals(Material.AIR) && !point.getBlock().isLiquid() && !transparent.contains(point.getBlock().getType()))
			{
				stop = true;
			}
			
			if(stop)
				break;
		}
	}
}
