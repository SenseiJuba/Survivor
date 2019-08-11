package fr.senseijuba.survivor.weapons.thingsLauncher;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class RocketLauncher extends AbstractThingLauncher
{
	public RocketLauncher()
	{
		super("Lance Roquette", Material.STONE_AXE, 1, 7.2, 13, false, "guns.liberateur", 30, EntityType.FIREBALL, 9, 16, 1.5, "�6Recharge : �a7.2s", "�6Lance une roquette qui explose au contact");
	}
	
	@Override
	public void onLauch(Player p)
	{
		Random rand = new Random();
		
		float x = (float)(p.getEyeLocation().clone().subtract(p.getEyeLocation().getDirection()).subtract(p.getEyeLocation()).getX());
		float y = (float)(p.getEyeLocation().clone().subtract(p.getEyeLocation().getDirection()).subtract(p.getEyeLocation()).getY());
		float z = (float)(p.getEyeLocation().clone().subtract(p.getEyeLocation().getDirection()).subtract(p.getEyeLocation()).getZ());
		
		for(int i = 0; i < 50; i++)
		{
			p.getWorld().spigot().playEffect(p.getEyeLocation(), Effect.CLOUD, 0, 0, (float)(x+rand.nextDouble()-0.5), (float)(y+rand.nextDouble()-0.5), (float)(z+rand.nextDouble()-0.5), Math.abs(rand.nextFloat()), 0, 10);
		}
		
	}
}
