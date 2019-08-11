package fr.lumin0u.vertix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.weapons.AbstractWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;

public class GetWeapon implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length < 1)
		{
			sender.sendMessage("§cUsage : /getWeapon <arme>");
			return true;
		}
		
		boolean ok = false;
		
		for(AbstractWeapon gun : TF.getInstance().getWeaponManager().listWeapons())
		{
			if(gun.getName().equalsIgnoreCase(String.join(" ", args)) && sender instanceof Player)
			{
				((Player)sender).getInventory().addItem(gun.getItem(gun.getMaxMunitions()));
				((Player)sender).setNoDamageTicks(20);
				
				ok = true;
			}
		}
		
		for(AbstractUltimateWeapon gun : TF.getInstance().getWeaponManager().listUltimateWeapons())
		{
			if(gun.name().equalsIgnoreCase(String.join(" ", args)) && sender instanceof Player)
			{
				((Player)sender).getInventory().addItem(gun.getItem(gun.getMaxMunitions()));
				((Player)sender).setNoDamageTicks(20);
				
				ok = true;
			}
		}
		
		if(!ok)
			if(args[0].equalsIgnoreCase("list"))
				for(AbstractWeapon k : TF.getInstance().getWeaponManager().listWeapons())
					sender.sendMessage("§a"+k.getName());
		
			else if(args[0].equalsIgnoreCase("all"))
				for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
					((Player)sender).getInventory().addItem(w.getItem(w.getMaxMunitions()));
			
			else
				sender.sendMessage("§cArme inconnue");
		
		sender.sendMessage(String.join(" ", args));
		
		return true;
	}
}
