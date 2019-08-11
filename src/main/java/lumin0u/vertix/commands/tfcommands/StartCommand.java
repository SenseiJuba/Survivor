package fr.lumin0u.vertix.commands.tfcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;

public class StartCommand extends TfArgCommand
{
	public StartCommand()
	{
		super("start", "lancer la partie", "", false, 0);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		if(!TF.getInstance().getWorlds().contains(p.getWorld()))
		{
			p.sendMessage("§cImpossible de lancer une partie sur ce monde.");
		}
		
		if(!gm.isGameStarted())
		{
			if(args.length > 1 && args[1].equalsIgnoreCase("cheat"))
				Bukkit.dispatchCommand(p, "/tf cheat");
			
			gm.setAttente(5);
			gm.forceStart();
		}
		
		else
		{
			p.sendMessage("§cLa partie est déja lancée !");
		}
	}
}
