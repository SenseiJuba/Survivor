package fr.senseijuba.survivor.weapons.blocks;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.notDangerous.Detonateur;

public class C4 extends AbstractBlock
{
	public C4()
	{
		super("C4", Material.STONE_BUTTON, 1, 23.9, 10, false, "guns.charge", 5, new Detonateur(), Material.STONE_BUTTON, "�6Poser une C4 vous donnera un d�tonateur");
	}
	
	public void explode(Location l, Player p)
	{
		l.getBlock().setType(Material.AIR);
		
		try 
		{
			for(Player ent : l.getWorld().getPlayers())
			{
				if(ent.getLocation().distance(l) <= 4)
				{
					GameManager.damageTF(ent, p, (4-ent.getLocation().distance(l))/4*16, Utils.explosionVector(ent, l, 5).multiply(0.5));
				}
			}
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally
		{
			Utils.playSound(l, "guns.grenade", 40);
			
			Utils.explosionParticles(l, 5, 100, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);
			
			for(int i = 0; i < 6.5*5; i++)
				l.getWorld().playEffect(l.clone().add((((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5, (((double)new Random().nextInt(500))/100.0)-2.5), Effect.LAVA_POP, 0);
		}
	}
}
