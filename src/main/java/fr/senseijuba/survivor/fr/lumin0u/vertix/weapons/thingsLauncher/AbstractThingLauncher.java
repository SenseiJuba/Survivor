package fr.lumin0u.vertix.weapons.thingsLauncher;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.AbstractWeapon;

public abstract class AbstractThingLauncher extends AbstractWeapon
{
	protected EntityType launched;
	protected double rayonExplosion;
	protected double damageExplosion;
	protected double valueKB;

	public AbstractThingLauncher(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, EntityType launched, double rayonExplosion, double damageExplosion, double valueKB, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
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
		Projectile projectile = (Projectile)p.getWorld().spawnEntity(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection().clone().multiply(2)), launched);

		if(projectile instanceof Fireball)
		{
			((Fireball)projectile).setYield(0f);
			((Fireball)projectile).setIsIncendiary(false);
		}

		projectile.setVelocity(p.getEyeLocation().getDirection().clone().multiply(1.5));

		projectile.setCustomName(name);

		projectile.setShooter(p);
		
		onLauch(p, projectile);
	}

	public void onLauch(Player p, Projectile po)
	{
		Vector velo = p.getEyeLocation().getDirection().clone().multiply(1.5);
		
		new BukkitRunnable()
		{
			int i = 0;
			
			@Override
			public void run()
			{
				po.setVelocity(velo);
				
				boolean stop = false;
				
				Utils.explosionParticles(po.getLocation(), 2, 3, Effect.CLOUD, Effect.FLAME, Effect.LARGE_SMOKE);
				
				for(Entity ent : p.getWorld().getEntities())
					if(ent.getLocation().distance(po.getLocation()) < 2.5 && !ent.equals(po) && !ent.equals(p))
						stop = true;
				
				if(i > 60 || Utils.pointsBetween(po.getLocation(), po.getLocation().add(velo.clone().multiply(2.5)), null).size() > 0 || stop || po.isDead())
				{
//					TF.debug("ok");
					
					if(!Utils.pointsBetween(po.getLocation(), po.getLocation().add(velo.clone().multiply(2.5)), null).isEmpty())
						po.teleport(Utils.pointsBetween(po.getLocation(), po.getLocation().add(velo.clone().multiply(2.5)), null).get(0).add(velo.clone().multiply(-0.2)));
					
					explode(p, po);
					cancel();
					return;
				}
				
				i++;
			}
		}.runTaskTimer(TF.getInstance(), 0, 1);
	}

	public void explode(Player p, Projectile po)// ROCKET LAUNCHER
	{
		po.remove();
		
		Location l = po.getLocation();
		
		for(Player ent : l.getWorld().getPlayers())
		{
			if(ent.getLocation().distance(l) <= rayonExplosion && !GameManager.getInstance(p.getWorld()).getTeamOf(p).getSafeZone().hasInside(p))
			{
				double m = 1 / (((double)Utils.pointsBetween(l, ent.getEyeLocation(), null).size()) / 20 + 0.5) / 2;
				
				if(!GameManager.getInstance(p.getWorld()).sameTeam(p, ent) || p.equals(ent))
					ent.setVelocity(Utils.explosionVector(ent, l, 10).multiply(7*m).add(new Vector(p.getVelocity().getX(), 0, p.getVelocity().getZ()).multiply(3)));

				GameManager.damageTF(ent, p, (rayonExplosion - ent.getEyeLocation().distance(l)) / rayonExplosion * damageExplosion * m, new Vector(0, 0, 0));
			}
		}

		Utils.playSound(l, "guns.grenade", 50);

		Utils.explosionParticles(l, (float)(10 / 1.5), 20 * 10, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);

		for(int i = 0; i < 6.5 * rayonExplosion; i++)
			l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

	}
}
