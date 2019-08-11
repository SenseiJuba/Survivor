package fr.lumin0u.vertix.weapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.weapons.AbstractWeapon;

public abstract class AbstractNotDangerousWeapon extends AbstractWeapon
{
	public AbstractNotDangerousWeapon(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
	}
	
	public abstract void effect(Player p);
}
