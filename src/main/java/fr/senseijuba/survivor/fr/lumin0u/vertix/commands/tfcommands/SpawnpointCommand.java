package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.managers.GameManager;

public class SpawnpointCommand extends TfArgCommand
{
	public SpawnpointCommand()
	{
		super("spawnpoint", "définir le point d'apparition d'une équipe ou du lobby", "<T|lobby>", false, 1);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		Team t = gm.getTeamByLetter(args[0]);
		
		if(!args[0].equalsIgnoreCase("lobby") && t != null)
		{
			t.setSpawnPoint(p.getLocation());
			p.sendMessage("§aPoint défini !");
		}

		else if(args[0].equalsIgnoreCase("lobby"))
		{
			gm.setLobbySpawnPoint(p.getLocation());
			p.sendMessage("§aPoint défini !");
		}
		
		else if(t == null)
			p.sendMessage("§cIl n'existe pas de team commençant par la lettre '"+args[0]+"'");
	}
	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		if(args.length == 2)
		{
			List<String> possibles = new ArrayList<>();
			
			for(Team t : GameManager.getInstance(executer.getWorld()).getTeams())
				possibles.add(t.getCharFR());
			
			possibles.add("lobby");
			
			return possibles;
		}
		
		else
			return new ArrayList<>();
	}
}
