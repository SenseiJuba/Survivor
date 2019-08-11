package fr.senseijuba.survivor.weapons.guns;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

public class Sniper extends AbstractGun
{
	public Sniper()
	{
		super("Sniper", Material.STICK, 1, 3.2, 1, 10, 120, 0.0001, false, "guns.sniper", 50, 4);
	}
	
	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();

		loree.add("�6Degats au corps sans vis�e : �a"+damage);
		loree.add("�6Degats � la t�te sans vis�e ou au");
		loree.add("�6corps avec vis�e : �a"+damage*1.5);
		loree.add("�6Degats � la t�te avec vis�e : �a"+damage*2);
		loree.add("�6Range : �a"+range);
		loree.add("�6Recharge : �a"+timeCharging+"s");
		
		return loree;
	}
}
