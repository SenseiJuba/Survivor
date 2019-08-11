package fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ScoutRace extends AbstractNotDangerousUltimateWeapon
{
	public ScoutRace()
	{
		super("Scout Race", Material.MILK_BUCKET, 1, 10, false, "player.drink", 10, "§6Speed III pendant 15 sec");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void effect(Player p)
	{
		p.addPotionEffect(new PotionEffect(new PotionEffectType(PotionEffectType.SPEED.getId())
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
		}, 300, 2));
	}
}
