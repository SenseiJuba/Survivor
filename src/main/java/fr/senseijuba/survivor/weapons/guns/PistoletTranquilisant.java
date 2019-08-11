package fr.senseijuba.survivor.weapons.guns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;

public class PistoletTranquilisant extends AbstractGun
{
	public PistoletTranquilisant()
	{
		super("Pistolet Tranquilisant", Material.WOOD_SPADE, 1, 3.2, 10, 3, 20, 0.001, false, "guns.arbalete", 20, 0, "�6Ralentit l'ennemi pendant 10 sec");
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

		for(int counter = 0; counter < range*2; counter++)
		{
			Location point = start.add(increase);
			
			for(int i = 0; i < 10; i++)
				p.getWorld().spigot().playEffect(point.clone().add(rand.nextDouble()/2-0.25, rand.nextDouble()/2-0.25, rand.nextDouble()/2-0.25), Effect.COLOURED_DUST, 0, 1, 0f, 0f, 0.7f, 1f, 0, 100);
			
			stop = false;
			
			for(Player ent : p.getWorld().getPlayers())
			{
				if(GameManager.getInstance(p.getWorld()).sameTeam(p, ent) && ent.getLocation().distance(point) < 2 && ent.getGameMode().equals(GameMode.SPECTATOR))
				{
					if(!(point.getY()-1.6 > ent.getLocation().getY()) && ent.getLocation().distance(point) > 1.6)
						break;
					
					Location newPoint = new Location(p.getWorld(), point.getX(), ent.getLocation().getY(), point.getZ());
					
					if(newPoint.distance(ent.getLocation()) > 0.5)
						break;

					
					if(GameManager.damageTF(ent, p, damage, new Vector(0, 0, 0), point))
					{
						p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 0.5f, 1.0f);
						
						stop = true;
					}
					
					
					((LivingEntity)ent).addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.SLOW.getId())
					{
						@Override
						public boolean isInstant()
						{
							return false;
						}
						
						@Override
						public String getName()
						{
							return "Lenteur";
						}
						
						@Override
						public double getDurationModifier()
						{
							return 0;
						}
					}, 180, 1));
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
