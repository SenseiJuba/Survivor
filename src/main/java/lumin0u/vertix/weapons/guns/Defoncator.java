package fr.lumin0u.vertix.weapons.guns;

import java.util.Random;

import org.bukkit.Material;

public class Defoncator extends AbstractGun
{
	public Defoncator()
	{
		super("Defonçator", Material.QUARTZ_ORE, 1, 0.2, 1, new Random().nextInt(1000), 200, 0.000001, true, "guns.revolver", 100, 10, "§6C'est du debug ok ?");
	}
}
