package fr.lumin0u.vertix.weapons.launchableItem;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;

public class GrenadeFlash extends AbstractLaunchableItem
{
	public GrenadeFlash()
	{
		super("Grenade Flash", Material.IRON_INGOT, 1, 15.9, 1, false, "", 1, 20l, "§6Rayon : §a6 blocs", "§6Recharge : §a16s", "§6Aveugle les ennemis");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void explode(Location l, Player launcher)
	{
		Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
		
		FireworkMeta fwMeta = fw.getFireworkMeta();
		fwMeta.addEffect(FireworkEffect.builder().trail(false).with(Type.BALL).withColor(Color.WHITE).build());
		fw.setFireworkMeta(fwMeta);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				fw.detonate();
			}
		}.runTaskLater(TF.getInstance(), 2l);
		
		for(Player ent : l.getWorld().getPlayers())
		{
			if(ent.getLocation().distance(l) < 6 && !GameManager.getInstance(ent.getWorld()).sameTeam(launcher, ent))
			{
				new BukkitRunnable()
				{
					int k = 0;
					
					@Override
					public void run()
					{
						k++;
						
						if(k > 15 || ent.getGameMode().equals(GameMode.SPECTATOR))
							cancel();
						
						for(int i = 0; i < 10; i++)
							ent.getWorld().playEffect(ent.getLocation().clone().add(new Random().nextDouble()%1-0.5, new Random().nextDouble()%1+1.5, new Random().nextDouble()%1-0.5), Effect.VILLAGER_THUNDERCLOUD, 1);
					}
				}.runTaskTimer(TF.getInstance(), 5, 5);
				
				ent.addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.BLINDNESS.getId())
				{
					
					@Override
					public boolean isInstant()
					{
						return false;
					}
					
					@Override
					public String getName()
					{
						return "Blindness";
					}
					
					@Override
					public double getDurationModifier()
					{
						return 0;
					}
				}, 100, 0));

				ent.addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.SLOW.getId())
				{
					
					@Override
					public boolean isInstant()
					{
						return false;
					}
					
					@Override
					public String getName()
					{
						return "Slowness";
					}
					
					@Override
					public double getDurationModifier()
					{
						return 0;
					}
				}, 80, 0));
			}
		}
	}
}
