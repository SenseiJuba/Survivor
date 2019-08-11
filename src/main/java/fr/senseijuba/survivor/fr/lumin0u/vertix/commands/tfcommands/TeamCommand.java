package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.GameManager;

public class TeamCommand extends TfArgCommand
{
	public TeamCommand()
	{
		super("team", "hidden", "<T>", true, 1);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		if(gm.getTeamOf(p) != null)
			gm.getTeamOf(p).getPlayers().remove(p.getUniqueId());

		gm.getTeamByLetter(args[0]).getPlayers().add(p.getUniqueId());
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
