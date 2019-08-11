package fr.senseijuba.survivor.weapons.ultimateWeapons.thingsLaunchers;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.commands.TfCommand;

public class Striker extends AbstractUltimateThingLauncher
{
	public Striker()
	{
		super("Striker", Material.QUARTZ, 4, 10, false, "guns.grenadelauncher", 40, EntityType.EGG, 6.5, 19, 2, "�6Envoie des projectiles explosifs");
	}
	
	@Override
	public void launch(Player p)
	{
		if(TfCommand.bulles)
		{
			for(int i = 0; i < 50; i++)
				p.getWorld().spigot().playEffect(p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(new Random().nextDouble()*2)), Effect.SPLASH, 0, 0, 0, 0, 0, 1, 0, 100);

			for(int i = 0; i < 20; i++)
				p.getWorld().spigot().playEffect(p.getEyeLocation().add(p.getEyeLocation().getDirection().multiply(new Random().nextDouble()*2)), Effect.POTION_SWIRL, 0, 1, 0, 0, 1, 1, 0, 100);
		}
			
		else
			super.launch(p);
	}
}
