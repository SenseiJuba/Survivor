package fr.senseijuba.survivor.weapons.thingsLauncher;

import java.util.Random;

import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

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
				if(!GameManager.getInstance(p.getWorld()).sameTeam(p, ent) || p.equals(ent))
					ent.setVelocity(Utils.explosionVector(ent, l, rayonExplosion).multiply(7).add(new Vector(p.getVelocity().getX(), 0, p.getVelocity().getZ()).multiply(2)));
				
				GameManager.damageTF(ent, p, (rayonExplosion - ent.getLocation().distance(l)) / rayonExplosion * damageExplosion, new Vector(0, 0, 0));
			}
		}
		
		Utils.playSound(l, "guns.grenade", 50);
		
		Utils.explosionParticles(l, (float)(rayonExplosion/1.5), 20*(int)rayonExplosion, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);
		
		for(int i = 0; i < 6.5 * rayonExplosion; i++)
			l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

	}
}
