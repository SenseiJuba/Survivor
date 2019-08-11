package fr.lumin0u.vertix.weapons.guns.shotguns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.guns.AbstractGun;

public abstract class AbstractShotgun extends AbstractGun
{
	protected int nbTirs;
	
	public AbstractShotgun(String name, Material mat, int munitions, double timeCharging, int ratioTir, double damage, int range, double precision, int nbTirs, boolean enchanted, String sound, float amplifier, double knockback, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, damage, range, precision, enchanted, sound, amplifier, knockback, lore);
		this.nbTirs = nbTirs;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void shoot(Player p)
	{
		Utils.playSound(p.getLocation(), sound, getMaxDistance());
		
		List<Player> alreadyHit = new ArrayList<>();
		
		HashMap<Player, Location> nearestPoint = new HashMap<>();
		
		for(int k = 0; k < nbTirs; k++)
		{
			Location start = p.getEyeLocation();
			Vector increase = start.getDirection();
			increase.multiply(0.5);
	
			boolean stop = false;
			
			try
			{
				if(PlayerManager.getInstance().getTFPlayer(p).getpower() != null && PlayerManager.getInstance().getTFPlayer(p).getpower().equals(SuperPower.AIMBOT))
				precision = getClass().newInstance().precision*0.6;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			increase.setX(increase.getX()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			increase.setY(increase.getY()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			increase.setZ(increase.getZ()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			for(int counter = 0; counter < range*2; counter++)
			{
				if(counter == 1)
					p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);
				
				Location point = start.add(increase);
				
				p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);
				
				if(PlayerManager.getInstance().getTFPlayer(p).getpower() != null && PlayerManager.getInstance().getTFPlayer(p).getpower().equals(SuperPower.DMG))
					p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, 1f, 0f, 0f, 1, 0, 100);//RED
				
				else
					p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);
	
				stop = false;
	
				for(Player ent : p.getWorld().getPlayers())
				{
					if(GameManager.getInstance(p.getWorld()).sameTeam(p, ent) || ent.isDead())
						continue;
					
					if(!nearestPoint.containsKey(ent) || nearestPoint.get(ent).distance(ent.getEyeLocation()) > point.distance(ent.getEyeLocation()))
						nearestPoint.put(ent, point.clone());
					
					if(PlayerManager.bodyCub(ent).hasInside(point) || PlayerManager.headCub(ent).hasInside(point))
					{
						if(GameManager.damageTF(ent, p, damage, increase.clone().normalize().multiply(knockback/3), point))
						{
							for(int i = 0; i < 10; i++)
								p.getWorld().playEffect(point, Effect.TILE_BREAK, 152);
							
							
							p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 0.5f, 1.0f);
							
							
							stop = true;
						}
						
						break;
					}
				}
				
				List<Material> transparent = new ArrayList<>();
				transparent.add(Material.LEAVES);
				transparent.add(Material.LEAVES_2);
				transparent.add(Material.STEP);
				transparent.add(Material.WOOD_STEP);
				transparent.add(Material.STONE_PLATE);
				transparent.add(Material.GOLD_PLATE);
				transparent.add(Material.IRON_PLATE);
				
				for(Material mat : Material.values())
					if(mat.isTransparent())
						transparent.add(mat);
				
				if(!point.getBlock().getType().equals(Material.AIR) && !point.getBlock().isLiquid() && !transparent.contains(point.getBlock().getType()))
				{
					Material mat = point.getBlock().getType();
					byte data = point.getBlock().getData();
					
					if(!point.getBlock().isLiquid())
						for(int i = 0; i < 20; i++)
							p.getWorld().playEffect(point.clone().subtract(increase), Effect.TILE_BREAK, new MaterialData(mat, data));
					
					stop = true;
				}
	
				if(stop)
					break;
			}
		}
		
		for(Player ent : nearestPoint.keySet())
			if(!alreadyHit.contains(ent))
				Utils.playSound(ent, nearestPoint.get(ent), "guns.fieew", 3);
	}
}
