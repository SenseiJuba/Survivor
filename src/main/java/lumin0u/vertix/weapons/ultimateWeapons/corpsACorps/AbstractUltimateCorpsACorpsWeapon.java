package fr.lumin0u.vertix.weapons.ultimateWeapons.corpsACorps;

import org.bukkit.Material;

import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;

public abstract class AbstractUltimateCorpsACorpsWeapon extends AbstractUltimateWeapon
{
	protected double damage;
	
	public AbstractUltimateCorpsACorpsWeapon(String name, Material mat, int munitions, int ratioTir, double damage, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
		
		this.damage = damage;
	}
	
	public double getDamage()
	{
		return damage;
	}
}
