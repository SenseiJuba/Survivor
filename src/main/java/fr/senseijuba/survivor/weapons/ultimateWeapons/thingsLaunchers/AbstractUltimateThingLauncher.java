package fr.senseijuba.survivor.weapons.ultimateWeapons.thingsLaunchers;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;

public abstract class AbstractUltimateThingLauncher extends AbstractUltimateWeapon
{
	protected EntityType launched;
	protected double rayonExplosion;
	protected double damageExplosion;
	protected double valueKB;
	
	public AbstractUltimateThingLauncher(String name, Material mat, int munitions, int ratioTir, boolean enchanted, String sound, float amplifier, EntityType launched, double rayonExplosion, double damageExplosion, double valueKB, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
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
		Entity projectile = p.getWorld().spawnEntity(p.getEyeLocation().clone().add(p.getEyeLocation().getDirection()), launched);
		
		projectile.setVelocity(p.getEyeLocation().getDirection().clone().multiply(2.25));
		
		if(this instanceof CoktailMolotov)
			projectile.setVelocity(p.getEyeLocation().getDirection().clone().multiply(0.6));
		
		projectile.setCustomName(name);
		
		((Projectile)projectile).setShooter(p);
	}
	
	public void explode(Player p, Location l)
	{
		for(Player ent : l.getWorld().getPlayers())
		{
			if(ent.equals(p))
				continue;
			
			if(ent.getLocation().distance(l) <= rayonExplosion && ent instanceof Damageable)
			{
				GameManager.damageTF(ent, p, (rayonExplosion-ent.getLocation().distance(l))/rayonExplosion*damageExplosion, Utils.explosionVector(ent, l, rayonExplosion).multiply(valueKB));
				
			}
		}

		Utils.playSound(l, "guns.grenade", 40);
		
		Utils.explosionParticles(l, (float)(rayonExplosion/1.5), 20*(int)rayonExplosion, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);
		
		for(int i = 0; i < 6.5*rayonExplosion; i++)
			l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5), Effect.LAVA_POP, 0);
		
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Chicken ent : l.getWorld().getEntitiesByClass(Chicken.class))
				{
					if(ent.getLocation().distance(l) < 3)
						ent.remove();
				}
			}
		}.runTaskLater(TF.getInstance(), 2);
	}
}
