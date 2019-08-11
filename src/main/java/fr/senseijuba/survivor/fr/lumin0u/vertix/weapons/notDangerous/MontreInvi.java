package fr.lumin0u.vertix.weapons.notDangerous;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;

public class MontreInvi extends AbstractNotDangerousWeapon
{
	public MontreInvi()
	{
		super("Time Out", Material.GOLD_INGOT, 1, 19.9, 10, false, "", 1, "§6Recharge : §a20s", "§6Devenez rapide, furtif et plus fort", "§6pendant 5 secondes");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void effect(Player p)
	{
		PlayerManager.getInstance().setIsInvisible(p, true);
		
		PotionEffectType invi = new PotionEffectType(PotionEffectType.INVISIBILITY.getId())
		{

			@Override
			public boolean isInstant()
			{
				return false;
			}

			@Override
			public String getName()
			{
				return "Invisibilité";
			}

			@Override
			public double getDurationModifier()
			{
				return 0;
			}
		};
		
		PotionEffectType speed = new PotionEffectType(PotionEffectType.SPEED.getId())
		{

			@Override
			public boolean isInstant()
			{
				return false;
			}

			@Override
			public String getName()
			{
				return "Vitesse";
			}

			@Override
			public double getDurationModifier()
			{
				return 0;
			}
		};

		p.removePotionEffect(invi);
		p.addPotionEffect(new PotionEffect(invi, 100, 1));

		for(Player pl : Bukkit.getOnlinePlayers())
			if(!GameManager.getInstance(p.getWorld()).sameTeam(p, pl))
				pl.hidePlayer(p);

		p.removePotionEffect(speed);
		p.addPotionEffect(new PotionEffect(speed, 100, 2));

		new BukkitRunnable()
		{
			int i = 0;

			@Override
			public void run()
			{
				for(Player pl : Bukkit.getOnlinePlayers())
					pl.hidePlayer(p);

				if(i == 28 || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					PlayerManager.getInstance().setIsInvisible(p, false);

					for(Player pl : Bukkit.getOnlinePlayers())
						pl.showPlayer(p);

					this.cancel();
				}
				
				i++;
			}
		}.runTaskTimer(TF.getInstance(), 5, 5);
	}
}
