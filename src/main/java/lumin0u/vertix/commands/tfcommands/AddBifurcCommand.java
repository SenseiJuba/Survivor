package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.GameManager;

public class AddBifurcCommand extends TfArgCommand
{
	public AddBifurcCommand()
	{
		super("addBifurc", "ajouter une bifurcation, demandez plus d'info si besoin", "<T>", false, 1);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		Team t = gm.getTeamByLetter(args[0]);
		
		if(t == null)
		{
			p.sendMessage("§cIl n'existe pas de team commençant par la lettre '"+args[0]+"'");
			return;
		}
		
		t.setBifurcAdder(p);
		p.sendMessage("§7Posez un rail sur la bifurc");
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
