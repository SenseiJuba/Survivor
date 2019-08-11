package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;

public class SuperPowerCommand extends TfArgCommand
{
	public SuperPowerCommand()
	{
		super("superPower", "hidden", "<superpower>", true, 1, "sp");
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		if(gm.getGameType().isSuperPowerMode() && SuperPower.valueOf(args[0]) != null)
			PlayerManager.getInstance().getTFPlayer(p).setpower(SuperPower.valueOf(args[0]));

		else if(SuperPower.valueOf(args[0]) == null)
			p.sendMessage("§cNON");
	}
	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		if(args.length == 2)
		{
			List<String> possibles = new ArrayList<>();
			
			for(SuperPower p : SuperPower.values())
				possibles.add(p.name());
			
			return possibles;
		}
		
		else
			return new ArrayList<>();
	}
}
