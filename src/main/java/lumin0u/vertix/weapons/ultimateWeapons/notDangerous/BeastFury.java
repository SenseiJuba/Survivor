package fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BeastFury extends AbstractNotDangerousUltimateWeapon
{
	public BeastFury()
	{
		super("Beast Fury", Material.GOLDEN_APPLE, 1, 10, true, "", 1, "§6Health boost et régénération pendant 17,5s");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void effect(Player p)
	{
		p.addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.HEALTH_BOOST.getId())
		{
			
			@Override
			public boolean isInstant()
			{
				return false;
			}
			
			@Override
			public String getName()
			{
				return "Health Boost";
			}
			
			@Override
			public double getDurationModifier()
			{
				return 0;
			}
		}, 350, 1));
		
		p.addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.REGENERATION.getId())
		{
			
			@Override
			public boolean isInstant()
			{
				return false;
			}
			
			@Override
			public String getName()
			{
				return "Régénération";
			}
			
			@Override
			public double getDurationModifier()
			{
				return 0;
			}
		}, 350, 2));
	}
}
