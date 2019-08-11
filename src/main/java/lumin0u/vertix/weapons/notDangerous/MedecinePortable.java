package fr.lumin0u.vertix.weapons.notDangerous;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.senseijuba.survivor.utils.Title;

public class MedecinePortable extends AbstractNotDangerousWeapon
{
	public static HashMap<Player, Boolean> isRegening = new HashMap<>();
	
	public MedecinePortable()
	{
		super("M�decine Portable", Material.IRON_PICKAXE, 1, 4.4, 10, false, "guns.remedy", 30, "�6Permet de soigner le joueur alli� le", "�6plus proche dans un rayon de 20 blocs,", "�6ou soi-m�me si aucun co�quipier", "�6ne se trouve dans la zone.");
	}

	@Override
	public void effect(Player p)
	{
		double distance = 100;
		
		Player victim = p;
		
		for(Player pla : p.getWorld().getPlayers())
		{
			if(pla.getLocation().distance(p.getLocation()) < 20 && pla.getLocation().distance(p.getLocation()) < distance && !pla.equals(p) && GameManager.getInstance(p.getWorld()).sameTeam(p, pla))
			{
				victim = pla;
				distance = pla.getLocation().distance(p.getLocation());
			}
		}
		
		effect(p, victim);
	}

	public static void effect(Player pl, Player victim)
	{
		Player pNF = victim;
		
		if(isRegening.get(pNF) == null)
			isRegening.put(pNF, true);
		
		else
			isRegening.replace(pNF, true);
		
		Title.sendActionBar(pl, "�2Target : �a"+victim.getName());
		
		final Player p = pNF;
		
		new BukkitRunnable()
		{
			int i = 0;
			
			@Override
			public void run()
			{
				if(p.isDead() || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					this.cancel();
					return;
				}
				
				if(p.getHealth()+0.85 > p.getMaxHealth() && p.getMaxHealth()+4 <= 40)
				{
					p.setMaxHealth(p.getMaxHealth()+4);
				}
				
				if(p.getHealth()+0.85 <= p.getMaxHealth())
					p.setHealth(p.getHealth()+0.85);
				
				if(i == 4)
				{
					this.cancel();
					
					isRegening.replace(p, false);
					
					new BukkitRunnable()
					{
						int i = 0;
						
						@Override
						public void run()
						{
							if(isRegening.get(p) || p.isDead())
							{
								this.cancel();
								return;
							}
							
							if(i == 150)
							{
								p.setMaxHealth(PlayerManager.getInstance().getTFPlayer(p).getKit().getMaxHealth());
								
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
					
					p.getWorld().spigot().playEffect(p.getLocation().clone().add(x*1.5, y*1.5+1, z*1.5), Effect.HAPPY_VILLAGER, 0, 1, 0f, 0f, 0f, 1f, 0, 200);
				}
				
				if(!pl.equals(p))
				{
					Location point = pl.getEyeLocation();
					
					Vector increase = p.getEyeLocation().toVector().clone().subtract(pl.getEyeLocation().toVector()).normalize().multiply(0.5);
					
					for(int i = 0; point.distance(p.getEyeLocation()) > 1; i++)
					{
						point.add(increase);
						
						if(i > 100)
							break;
						
						point.getWorld().playEffect(point, Effect.HAPPY_VILLAGER, 1);
					}
				}
				
				i++;
			}
		}.runTaskTimer(TF.getInstance(), 0, 15l);
	}
}
