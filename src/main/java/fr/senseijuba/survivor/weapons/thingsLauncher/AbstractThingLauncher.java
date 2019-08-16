package fr.senseijuba.survivor.weapons.thingsLauncher;

import java.util.Random;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

public abstract class AbstractThingLauncher extends AbstractWeapon
{
	protected EntityType launched;
	protected double rayonExplosion;
	protected double damageExplosion;
	protected double valueKB;

	public AbstractThingLauncher(String name, Material mat, int munitions, int maxmunitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, EntityType launched, double rayonExplosion, double damageExplosion, double valueKB, String... lore)
	{
		super(name, mat, munitions, maxmunitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
		this.launched = launched;
		this.rayonExplosion = rayonExplosion;
		this.damageExplosion = damageExplosion;
		this.valueKB = valueKB;
	}

	public EntityType getProjectile()
	{
		return launched;
	}

	public double getRayon()
	{
		return rayonExplosion;
	}

	public double getDamage()
	{
		return damageExplosion;
	}

	public double getValueKB()
	{
		return valueKB;
	}

	public void launch(Player p)
	{
		onLauch(p);

		Projectile projectile = (Projectile)p.getWorld().spawnEntity(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().clone().multiply(2)), launched);

		if(projectile instanceof Fireball)
		{
			((Fireball)projectile).setYield(0f);
			((Fireball)projectile).setIsIncendiary(false);
		}

		projectile.setVelocity(p.getEyeLocation().getDirection().clone().multiply(1.5));

		projectile.setCustomName(name);

		projectile.setShooter(p);
	}

	public void onLauch(Player p)
	{

	}

	public void explode(Player p, Location l)//ROCKET LAUNCHER
	{
		for(Player ent : l.getWorld().getPlayers())
		{
			if(ent.getLocation().distance(l) <= rayonExplosion)
			{
				if(p.equals(ent))
					ent.setVelocity(Utils.explosionVector(ent, l, rayonExplosion).multiply(7).add(new Vector(p.getVelocity().getX(), 0, p.getVelocity().getZ()).multiply(2)));
				
				damageTF(ent, p, (rayonExplosion - ent.getLocation().distance(l)) / rayonExplosion * damageExplosion, new Vector(0, 0, 0));
			}
		}
		
		Utils.playSound(l, "guns.grenade", 50);
		
		Utils.explosionParticles(l, (float)(rayonExplosion/1.5), 20*(int)rayonExplosion, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);
		
		for(int i = 0; i < 6.5 * rayonExplosion; i++)
			l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

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
