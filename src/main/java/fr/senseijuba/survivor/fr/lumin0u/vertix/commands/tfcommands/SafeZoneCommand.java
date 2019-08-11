package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.GameManager;

public class SafeZoneCommand extends TfArgCommand
{
	public SafeZoneCommand()
	{
		super("safeZone", "définir la SafeZone d'une équipe en posant des blocks dans les coins", "<T>", false, 1, "sz");
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
		
		t.setSZ1Modifier(p);
		p.sendMessage("§7Posez des blocks aux coins de la SafeZone " + t.getName(true));
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
