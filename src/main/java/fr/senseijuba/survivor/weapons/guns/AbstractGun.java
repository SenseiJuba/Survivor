package fr.senseijuba.survivor.weapons.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.atouts.Atout;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class AbstractGun extends AbstractWeapon
{
	protected double damage;
	protected int range;
	protected double precision;
	protected double knockback;
	protected int currentMunitions;
	
	public AbstractGun(String name, Material mat, int munitions, int maxMunitions, double timeCharging, int ratioTir, double damage, int range, double precision, boolean enchanted, String sound, float amplifier, double knockback, String... lore)
	{
		super(name, mat, munitions, maxMunitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
		
		this.damage = damage;
		this.range = range;
		this.precision = precision;
		this.knockback = knockback;
		this.currentMunitions = maxMunitions;
	}
	
	@SuppressWarnings("deprecation")
	public void shoot(Player p)
	{
		this.currentMunitions--;

		p.getInventory().getItemInHand().setAmount(this.currentMunitions);

		Location start = p.getEyeLocation();
		Vector increase = start.getDirection();
		
		Utils.playSound(p.getLocation(), sound, getMaxDistance());
		
		increase.multiply(0.5);
		
		increase.setX(increase.getX()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
		
		increase.setY(increase.getY()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
		
		increase.setZ(increase.getZ()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
		
		List<Entity> alreadyHit = new ArrayList<>();
		
		HashMap<Player, Location> nearestPoint = new HashMap<>();
		
		double hitboxMultiplier = (this instanceof DRAGUNOV ? 1.1 : 1);

		if(Survivor.getInstance().getPlayerAtout().get(p).contains(Atout.DOUBLE_COUP)){

			AbstractGun inst = this;

			new BukkitRunnable() {
				@Override
				public void run() {
					counter: for(int counter = 0; counter < range*2; counter++)
					{
						if(counter == 1)
							p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);

						Location point = start.add(increase);

						p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);

						p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100); //TODO CHANGE COLOR FOR LASER

						for(Entity ents : p.getWorld().getEntities())
						{
							if(alreadyHit.contains(ents) || ents.isDead())
								continue;

							Creature ent = null;
							Player pl = null;

							if(ents instanceof Creature){
								ent = (Creature) ents;
							}
							else if(ents instanceof Player){
								pl = (Player) ents;
								if(!nearestPoint.containsKey(pl) || nearestPoint.get(pl).distance(pl.getEyeLocation()) > point.distance(pl.getEyeLocation()))
									nearestPoint.put(pl, point.clone());
							}
							else{
								return;
							}

							if(Survivor.getInstance().getPlayerManager().bodyCub(ent).multiply(hitboxMultiplier).hasInside(point) || Survivor.getInstance().getPlayerManager().headCub(ent).multiply(hitboxMultiplier).hasInside(point))
							{
								boolean damageDone = false;

								if(Survivor.getInstance().getPlayerManager().isScope(p) && inst instanceof DRAGUNOV)//looking sniper
								{
//						TF.debug("Sniper scope");
									damageDone = damageTF(ent, p, damage*1.5, p.getEyeLocation().getDirection().multiply(knockback/3).add(increase.clone().normalize()).multiply(0.5), point);
								}

								else//normal
								{
//						TF.debug("Normal hit");
									damageDone = damageTF(ent, p, damage, p.getEyeLocation().getDirection().multiply(knockback/3).add(increase.clone().normalize()).multiply(0.5), point);
								}

								if(damageDone)
								{
									ent.setVelocity(ent.getVelocity().multiply(0.25));

									alreadyHit.add(ents);

									for(int i = 0; i < 10; i++)
										p.getWorld().playEffect(point, Effect.TILE_BREAK, 152);

									if(!p.equals(ent))
										p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 0.5f, 1.0f);

									if(!(inst instanceof DRAGUNOV))
										break counter;
								}
							}
						}

						List<Material> transparent = new ArrayList<>();
						transparent.add(Material.LEAVES);
						transparent.add(Material.LEAVES_2);
						transparent.add(Material.BARRIER);
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

							break;
						}
					}
				}
			}.runTaskLater(Survivor.getInstance(), 2);
		}

		counter: for(int counter = 0; counter < range*2; counter++)
		{
			if(counter == 1)
				p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);
			
			Location point = start.add(increase);

			p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100);

			p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float) 1/ 255, (float) 0/ 255, (float) 0/ 255, 1, 0, 100); //TODO CHANGE COLOR FOR LASER
			
			for(Entity ents : p.getWorld().getEntities())
			{
				if(alreadyHit.contains(ents) || ents.isDead())
					continue;

				Creature ent = null;
				Player pl = null;

				if(ents instanceof Creature){
					ent = (Creature) ents;
				}
				else if(ents instanceof Player){
					pl = (Player) ents;
					if(!nearestPoint.containsKey(pl) || nearestPoint.get(pl).distance(pl.getEyeLocation()) > point.distance(pl.getEyeLocation()))
						nearestPoint.put(pl, point.clone());
				}
				else{
					return;
				}

				if(Survivor.getInstance().getPlayerManager().bodyCub(ent).multiply(hitboxMultiplier).hasInside(point) || Survivor.getInstance().getPlayerManager().headCub(ent).multiply(hitboxMultiplier).hasInside(point))
				{
					boolean damageDone = false;
					
					if(Survivor.getInstance().getPlayerManager().isScope(p) && this instanceof DRAGUNOV)//looking sniper
					{
//						TF.debug("Sniper scope");
						damageDone = damageTF(ent, p, damage*1.5, p.getEyeLocation().getDirection().multiply(knockback/3).add(increase.clone().normalize()).multiply(0.5), point);
					}
					
					else//normal
					{
//						TF.debug("Normal hit");
						damageDone = damageTF(ent, p, damage, p.getEyeLocation().getDirection().multiply(knockback/3).add(increase.clone().normalize()).multiply(0.5), point);
					}
					
					if(damageDone)
					{
						ent.setVelocity(ent.getVelocity().multiply(0.25));
						
						alreadyHit.add(ents);
						
						for(int i = 0; i < 10; i++)
							p.getWorld().playEffect(point, Effect.TILE_BREAK, 152);
						
						if(!p.equals(ent))
							p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 0.5f, 1.0f);

						if(!(this instanceof DRAGUNOV))
							break counter;
					}
				}
			}
			
			List<Material> transparent = new ArrayList<>();
			transparent.add(Material.LEAVES);
			transparent.add(Material.LEAVES_2);
			transparent.add(Material.BARRIER);
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
				
				break;
			}
		}
		
		for(Player ent : nearestPoint.keySet())
			Utils.playSound(ent, nearestPoint.get(ent), "guns.fieew", 5);
	}
	
	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();
		
		loree.add("�6Degats par balle : �a"+damage);
		loree.add("�6Range : �a"+range);
		
		if(timeCharging > 0)
			loree.add("�6Recharge : �a"+timeCharging+"s");

		loree.addAll(super.getLore());
		
		return loree;
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
