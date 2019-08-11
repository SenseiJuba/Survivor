package fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.weapons.blocks.Mine;

public class RedButton extends AbstractNotDangerousUltimateWeapon
{
	public RedButton()
	{
		super("Red Button", Material.CLAY_BALL, 1, 10, false, "guns.redbutton", 15, "§6Devenez invincible pendant 5s");
	}

	@Override
	public void effect(Player p)
	{
		for(int i = 0; i < 4000; i++)
		{
			double angle1 = new Random().nextDouble()*2*Math.PI;
			double angle2 = new Random().nextDouble()*2*Math.PI-Math.PI/2;
			
			double x = Math.cos(angle1)*Math.cos(angle2);
			double z = Math.sin(angle1)*Math.cos(angle2);
			double y = Math.sin(angle2);
			
			System.out.println();
			
			p.getWorld().spigot().playEffect(p.getLocation().clone().add(x*2, y*2+1, z*2), Effect.COLOURED_DUST, 0, 1, 0f, 0f, 0f, 1f, 0, 300);
		}
		
		p.getInventory().addItem(((Mine)Mine.getInstance()).getItem(1));

		PlayerManager.getInstance().setIsInvicible(p, true);
		
		new BukkitRunnable()
		{
			int i = 0;
			
			@Override
			public void run()
			{
				double angle1;
				double angle2;
				
				double x;
				double z;
				double y;
				
				for(int i = 0; i < 5; i++)
				{
					angle1 = new Random().nextDouble()*2*Math.PI;
					angle2 = new Random().nextDouble()*2*Math.PI-Math.PI/2;
					
					x = Math.cos(angle1)*Math.cos(angle2);
					z = Math.sin(angle1)*Math.cos(angle2);
					y = Math.sin(angle2);
					
					p.getWorld().spigot().playEffect(p.getLocation().clone().add(x, y+1, z), Effect.CLOUD, 0, 1, 0f, 0f, 0f, 1f, 0, 300);
				}
				
				for(int i = 0; i < 2; i++)
				{
					angle1 = new Random().nextDouble()*2*Math.PI;
					angle2 = new Random().nextDouble()*2*Math.PI-Math.PI/2;
					
					x = Math.cos(angle1)*Math.cos(angle2);
					z = Math.sin(angle1)*Math.cos(angle2);
					y = Math.sin(angle2);
					
					p.getWorld().spigot().playEffect(p.getLocation().clone().add(x*2, y*2+1, z*2), Effect.COLOURED_DUST, 0, 1, 0f, 0f, 0f, 1f, 0, 300);
				}
				
				PlayerManager.getInstance().setIsInvicible(p, true);
				
				if(i == 100)
				{
					PlayerManager.getInstance().setIsInvicible(p, false);
					this.cancel();
				}
				
				i++;
			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);
	}
}
