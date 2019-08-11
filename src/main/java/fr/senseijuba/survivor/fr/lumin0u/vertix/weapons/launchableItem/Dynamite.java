package fr.lumin0u.vertix.weapons.launchableItem;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;

public class Dynamite extends AbstractLaunchableItem
{
	public Dynamite()
	{
		super("Dynamite", Material.CLAY_BRICK, 1, 7.9, 1, false, "", 1, 35l, "§6Degats : §a20", "§6Rayon : §a7 blocs", "§6Recharge : §a7s");
	}

	@Override
	public void explode(Location l, Player launcher)
	{
		double rayonExplosion = 7;
		double damageExplosion = 20;
		double valueKB = 0.7;

		for(Player ent : l.getWorld().getPlayers())
		{
			if(ent.getLocation().distance(l) <= rayonExplosion && ent instanceof Damageable)
			{
				double m = 1 / (((double)Utils.pointsBetween(l, ent.getEyeLocation(), null).size()) / 20 + 0.5) / 2;

				GameManager.damageTF(ent, launcher, (rayonExplosion - ent.getLocation().distance(l)) / rayonExplosion * damageExplosion * m, Utils.explosionVector(ent, l, rayonExplosion).multiply(m * valueKB));
			}
		}

		Utils.playSound(l, "guns.grenade", 35);

		Utils.explosionParticles(l, (float)(rayonExplosion / 1.5), 20 * (int)rayonExplosion, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);

		for(int i = 0; i < 6.5 * rayonExplosion; i++)
			l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

	}
}
