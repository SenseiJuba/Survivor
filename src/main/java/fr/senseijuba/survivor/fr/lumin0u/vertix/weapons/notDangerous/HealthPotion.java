package fr.lumin0u.vertix.weapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HealthPotion extends AbstractNotDangerousWeapon
{
	public HealthPotion()
	{
		super("Potion de Vie", Material.POTION, 1, 23.9, 10, true, "", 1, "§6Effet : §a+8 PV", "§6Recharge : §a24s");
	}

	@Override
	public void effect(Player p)
	{
		if(p.getMaxHealth() -8 > p.getHealth())
			p.setHealth(p.getHealth()+8);
		
		else
			p.setHealth(p.getMaxHealth());
	}
}
