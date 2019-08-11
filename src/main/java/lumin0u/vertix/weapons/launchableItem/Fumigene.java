package fr.lumin0u.vertix.weapons.launchableItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.utils.Utils;

public class Fumigene extends AbstractLaunchableItem
{
	public Fumigene()
	{
		super("Fumigène", Material.NETHER_BRICK_ITEM, 1, 13.9, 1, false, "", 1, 35l, "§6Rayon de fumée : §a5 blocs", "§6Recharge : §a14s");
	}

	@Override
	public void explode(Location l, Player launcher)
	{
		List<Location> fumiBlocks = new ArrayList<>();
		
		Utils.playSound(l, "guns.grenade", 35);
		
		for(int x = 0; x < 11; x++)
		{
			for(int y = 0; y < 11; y++)
			{
				for(int z = 0; z < 11; z++)
				{
					Location loc = l.clone().subtract(5.5, 5.5, 5.5).add(x, y, z);
					
					if(loc.distance(l) < 5.01 && loc.getBlock().getType().equals(Material.AIR) && ((double)Utils.pointsBetween(l, loc, null).size()) < 1)
					{
						fumiBlocks.add(loc);
					}
				}
			}
		}
		
		for(Location lok : fumiBlocks)
		{
			lok.getBlock().setType(Material.TRIPWIRE);
		}
		
		new BukkitRunnable()
		{
			int i = 0;
			
			@Override
			public void run()
			{
				i++;
				
				for(Projectile ent : l.getWorld().getEntitiesByClass(Projectile.class))
				{
					for(Location lo : fumiBlocks)
					{
						if(lo.distance(ent.getLocation()) < 1)
						{
							TF.getInstance().getListener().projectileBreak(new ProjectileHitEvent(ent));
							ent.remove();
						}
					}
				}
				
				if(i >= 60)
				{
					for(Location lo : fumiBlocks)
						lo.getBlock().setType(Material.AIR);
					
					this.cancel();
				}
			}
		}.runTaskTimer(TF.getInstance(), 2l, 2l);
	}
}
