package fr.senseijuba.survivor.weapons.guns.shotguns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.guns.AbstractGun;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public abstract class AbstractShotgun extends AbstractGun
{
	protected int nbTirs;
	
	public AbstractShotgun(String name, Material mat, int munitions, int maxMunitions, double timeCharging, int ratioTir, double damage, int range, double precision, int nbTirs, boolean enchanted, String sound, float amplifier, double knockback, String... lore)
	{
		super(name, mat, munitions, maxMunitions, timeCharging, ratioTir, damage, range, precision, enchanted, sound, amplifier, knockback, lore);
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
			
			increase.setX(increase.getX()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			increase.setY(increase.getY()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			increase.setZ(increase.getZ()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
			
			for(int counter = 0; counter < range*2; counter++)
			{
				if(counter == 1)
					p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);
				
				Location point = start.add(increase);
				
				p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);
				
				p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);
	
				stop = false;
	
				for(Player ent : p.getWorld().getPlayers())
				{
					if(ent.isDead())
						continue;
					
					if(!nearestPoint.containsKey(ent) || nearestPoint.get(ent).distance(ent.getEyeLocation()) > point.distance(ent.getEyeLocation()))
						nearestPoint.put(ent, point.clone());
					
					if(Survivor.getInstance().getPlayerManager().bodyCub(ent).hasInside(point) || Survivor.getInstance().getPlayerManager().headCub(ent).hasInside(point))
					{
						if(damageTF(ent, p, damage, increase.clone().normalize().multiply(knockback/3), point))
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
				transparent.add(Material.BARRIER);
				
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

	public static boolean damageTF(Entity victim, Player damager, double damage, Vector kb)
	{
		return damageTF(victim, damager, damage, kb, victim.getLocation().clone().add(0, 1.9, 0));
	}

	public static boolean damageTF(Entity victim, Player damager, double damage, Vector kb, Location damagePoint)
	{
		return damageTF(victim, damager, damage, kb, damagePoint, false);
	}

	@SuppressWarnings("deprecation")
	public static boolean damageTF(Entity victim, Player damager, double damage, Vector kb, Location damagePoint, boolean thorns)
	{
		if(!Survivor.getInstance().gameState.equals(GameState.STARTED))
			return false;

		if(damager != null)
			if(victim instanceof Player || !(victim instanceof Creature) || victim.isDead())
				return false;

		if(victim instanceof Creature) {
			Creature v = (Creature) victim;

			if(v == null)
				return false;

			v.setVelocity(v.getVelocity().add(kb));

			v.setLastDamageCause(new EntityDamageByEntityEvent(damager, v, EntityDamageEvent.DamageCause.PROJECTILE, damage));

			Utils.playSound(v.getLocation(), "player.shot", 20);
		}

		return true;////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}
}
