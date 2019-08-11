package fr.lumin0u.vertix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Kit;
import fr.lumin0u.vertix.managers.PlayerManager;

public class GetKit implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length < 1)
		{
			sender.sendMessage("§cUsage : /getKit <kit>");
			
			for(Kit k : Kit.values())
				sender.sendMessage("§a"+k.name());
			
			return true;
		}
		
		boolean ok = false;
		
		for(Kit kit : Kit.values())
		{
			if(kit.name().equalsIgnoreCase(args[0]) && sender instanceof Player)
			{
				PlayerManager.getInstance().changeKit((Player)sender, kit);
				
				ok = true;
			}
		}
		
		if(!ok)
			sender.sendMessage("§cKit inconnu");
		
		return true;
	}
}
