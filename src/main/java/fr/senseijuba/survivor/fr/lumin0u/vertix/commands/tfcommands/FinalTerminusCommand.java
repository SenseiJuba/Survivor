package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.GameManager;

public class FinalTerminusCommand extends TfArgCommand
{
	public FinalTerminusCommand()
	{
		super("finalTerminus", "d�finir le point d'alerte du chemin de rail d'une �quipe", "<T>", false, 1);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		Team t = gm.getTeamByLetter(args[0]);
		
		if(t == null)
		{
			p.sendMessage("�cIl n'existe pas de team commen�ant par la lettre '"+args[0]+"'");
			return;
		}
		
		t.setFinalTerminusModifier(p);
		p.sendMessage("�7Cliquez sur le rail du final terminus " + t.getName(false));
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
