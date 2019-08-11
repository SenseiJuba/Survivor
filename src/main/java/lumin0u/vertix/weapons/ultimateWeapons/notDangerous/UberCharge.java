package fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.weapons.notDangerous.MedecinePortable;

public class UberCharge extends AbstractNotDangerousUltimateWeapon
{
	private HashMap<Player, Boolean> isRegening = new HashMap<>();
	
	public UberCharge()
	{
		super("Uber Charge", Material.GLOWSTONE_DUST, 1, 1, true, "guns.remedy", 100, "§6Soigne tout les alliés pendant 8s");
	}

	@Override
	public void effect(Player p)
	{
		List<Player> pNF = new ArrayList<>();
		
		for(Player pla : p.getWorld().getPlayers())
		{
			if(GameManager.getInstance(p.getWorld()).sameTeam(p, pla))
			{
				pNF.add(pla);
			}
		}
		
		for(Player pl : pNF)
		{
			if(isRegening.get(pl) == null)
				isRegening.put(pl, true);
			
			else
				isRegening.replace(pl, true);
		}
		
		for(Player pl : pNF)
		{
			new BukkitRunnable()
			{
				int i = 0;
				
				@Override
				public void run()
				{
					if(pl.isDead())
					{
						this.cancel();
						return;
					}
					
					if(pl.getHealth()+2 > pl.getMaxHealth() && pl.getMaxHealth()+4 <= 30)
					{
						pl.setMaxHealth(pl.getMaxHealth()+4);
					}
					
					if(pl.getHealth()+2 <= pl.getMaxHealth())
						pl.setHealth(pl.getHealth()+2);
					
					if(i == 16)
					{
						this.cancel();
						
						isRegening.replace(pl, false);
						
						new BukkitRunnable()
						{
							int i = 0;
							
							@Override
							public void run()
							{
								try {
									if(isRegening.get(pl) || pl.isDead() || MedecinePortable.isRegening == null || MedecinePortable.isRegening.get(pl))
									{
										this.cancel();
										return;
									}
								}
								catch(Exception e)
								{
									this.cancel();
									return;
								}
								
								if(i == 100)
								{
									pl.setMaxHealth(PlayerManager.getInstance().getTFPlayer(pl).getKit().getMaxHealth());
									
									this.cancel();
								}
								
								i++;
							}
						}.runTaskTimer(TF.getInstance(), 1l, 1l);
					}
					
					double angle1 = new Random().nextDouble()*2*Math.PI;
					double angle2 = new Random().nextDouble()*2*Math.PI-Math.PI/2;
					
					double x = Math.cos(angle1)*Math.cos(angle2);
					double z = Math.sin(angle1)*Math.cos(angle2);
					double y = Math.sin(angle2);
					
					for(int i = 0; i < 100; i++)
					{
						angle1 = new Random().nextDouble()*2*Math.PI;
						angle2 = new Random().nextDouble()*2*Math.PI-Math.PI/2;
						
						x = Math.cos(angle1)*Math.cos(angle2);
						z = Math.sin(angle1)*Math.cos(angle2);
						y = Math.sin(angle2);
						
						pl.getWorld().spigot().playEffect(pl.getLocation().clone().add(x*1.5, y*1.5+1, z*1.5), Effect.HAPPY_VILLAGER, 0, 1, 0f, 0f, 0f, 1f, 0, 200);
					}
					
					if(!p.equals(pl))
					{
						Location point = p.getEyeLocation();
						
						Vector increase = pl.getEyeLocation().toVector().clone().subtract(p.getEyeLocation().toVector()).normalize().multiply(0.5);
						
						for(int i = 0; point.distance(pl.getEyeLocation()) > 1; i++)
						{
							point.add(increase);
							
							if(i > 100)
								break;
							
							point.getWorld().playEffect(point, Effect.HAPPY_VILLAGER, 1);
						}
					}
					
					i++;
				}
			}.runTaskTimer(TF.getInstance(), 0, 10l);
		}
	}
}
