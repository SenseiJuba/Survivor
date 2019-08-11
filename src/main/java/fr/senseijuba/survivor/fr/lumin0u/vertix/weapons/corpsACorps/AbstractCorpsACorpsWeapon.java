package fr.lumin0u.vertix.weapons.corpsACorps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import fr.lumin0u.vertix.weapons.AbstractWeapon;

public abstract class AbstractCorpsACorpsWeapon extends AbstractWeapon
{
	protected double damage;
	
	public AbstractCorpsACorpsWeapon(String name, Material mat, int munitions, double timeCharging, int ratioTir, double damage, boolean enchanted, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, "guns.melee", 15, lore);
		
		this.damage = damage;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();
		
		loree.add("§6Degats : §a"+damage);
		
		if(timeCharging > 0.9)
			loree.add("§6Recharge : §a"+timeCharging+"s");
		
		loree.addAll(super.getLore());
		
		return loree;
	}
}
