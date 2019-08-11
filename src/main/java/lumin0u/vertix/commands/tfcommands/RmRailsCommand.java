package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.CartManager;
import fr.lumin0u.vertix.managers.GameManager;

public class RmRailsCommand extends TfArgCommand
{
	public RmRailsCommand()
	{
		super("rmRails", "supprimer le chemin d'une éqipe ou bleu §ndéfinitivement", "<T>", false, 1);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		CartManager cm = gm.getCartManager();
		
		if(cm == null)
		{
			p.sendMessage("§cCe monde n'est pas prévu pour du payloads. Essayez '/tf worlds'");
			return;
		}
		
		Team t = gm.getTeamByLetter(args[0]);
		
		if(t == null)
		{
			p.sendMessage("§cIl n'existe pas de team commençant par la lettre '"+args[0]+"'");
			return;
		}
		
		cm.rmRails(t);
		p.sendMessage("§aChemin " + t.getName(false) + " supprimé");
	}
	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		if(args.length == 2)
		{
			List<String> possibles = new ArrayList<>();
			
			for(Team t : GameManager.getInstance(executer.getWorld()).getTeams())
				possibles.add(t.getCharFR());
			
			return possibles;
		}
		
		else
			return new ArrayList<>();
	}
}
