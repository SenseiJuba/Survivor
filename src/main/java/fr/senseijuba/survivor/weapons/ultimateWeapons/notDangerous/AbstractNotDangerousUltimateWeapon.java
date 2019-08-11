package fr.senseijuba.survivor.weapons.ultimateWeapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;

public abstract class AbstractNotDangerousUltimateWeapon extends AbstractUltimateWeapon
{
	public AbstractNotDangerousUltimateWeapon(String name, Material mat, int munitions, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
	}
	
	public abstract void effect(Player p);
}
