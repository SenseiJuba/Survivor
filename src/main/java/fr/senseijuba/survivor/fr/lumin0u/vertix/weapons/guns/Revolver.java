package fr.lumin0u.vertix.weapons.guns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Revolver extends AbstractGun
{
	public Revolver()
	{
		super("Revolver", Material.GOLD_HOE, 1, 1.5, 1, 4, 30, 0.01, false, "guns.revolver", 30, 0.2);
	}
	
	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();
		
		loree.add("§6Degats à la tête : §a"+((int)damage+2));
		loree.add("§6Degats au corps : §a"+((int)damage));
		loree.add("§6Range : §a"+range);
		loree.add("§6Recharge : §a"+timeCharging+"s");
		
		return loree;
	}
}
