package fr.senseijuba.survivor.weapons.notDangerous;

import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract class AbstractNotDangerousWeapon extends AbstractWeapon
{
	public AbstractNotDangerousWeapon(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
	}
	
	public abstract void effect(Player p);
}
