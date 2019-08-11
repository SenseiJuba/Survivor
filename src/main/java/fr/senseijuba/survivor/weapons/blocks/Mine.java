package fr.senseijuba.survivor.weapons.blocks;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;

public class Mine extends AbstractBlock
{
	public Mine()
	{
		super("Mine", Material.STONE_PLATE, 3, 0, 10, false, "", 1, null, Material.STONE_PLATE, "�6Les ennemis passant sur la mine", "�6la feront exploser");
	}
	
	public static void explode(Location l, Player p)
	{
		l.getBlock().setType(Material.AIR);
		
		try 
		{
			for(Player ent : l.getWorld().getPlayers())
			{
				if(ent.getLocation().distance(l) <= 3)
				{
					GameManager.damageTF(ent, p, (3-ent.getLocation().distance(l))/3*15, Utils.explosionVector(ent, l, 3).multiply(1));
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			Utils.playSound(p.getLocation(), "guns.grenade", 30);
			
			Utils.explosionParticles(l, 2, 60, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);
			
			for(int i = 0; i < 6.5*3; i++)
				l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5), Effect.LAVA_POP, 0);
		}
	}
}
