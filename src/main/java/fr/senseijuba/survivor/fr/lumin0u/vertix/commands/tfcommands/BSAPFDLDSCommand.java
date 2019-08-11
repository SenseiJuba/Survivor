package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;

public class BSAPFDLDSCommand extends TfArgCommand
{
	public BSAPFDLDSCommand()
	{
		super("bsapfdlds", "posez un block qui deviendra un block sur la tete d'un armorstand", "<true|false pour grand ou petit> <angle 1> <angle 2>", false, 3);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		if(!args[0].equalsIgnoreCase("true") && !args[0].equalsIgnoreCase("false"))
		{
			p.sendMessage("§cVeuillez remplir avec true ou false pour true -> grand block et false -> petit block");
			return;
		}
		
		else
		{
			try {
				if(args[0].equalsIgnoreCase("true"))
				{
					PlayerManager.getInstance().setBigBlockPoser(p);
					PlayerManager.getInstance().setBigBlockAngle(new EulerAngle(Double.parseDouble(args[1])/360*2*Math.PI, Double.parseDouble(args[2])/360*2*Math.PI, 0));
				}
				
				else if(args[0].equalsIgnoreCase("false"))
				{
					PlayerManager.getInstance().setSmallBlockPoser(p);
					PlayerManager.getInstance().setSmallBlockAngle(new EulerAngle(Double.parseDouble(args[1])/360*2*Math.PI, Double.parseDouble(args[2])/360*2*Math.PI, 0));
				}
				
				p.sendMessage("§aAllez-y, posez un block");
			}
			catch(NumberFormatException e)
			{
				p.sendMessage("§c'"+args[0]+"' ou '"+args[1]+"' n'est pas un nombre");
			}
		}
	}

	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		List<String> possibles = new ArrayList<>();
		
		if(args.length == 2)
		{
			possibles.add("true");
			possibles.add("false");
		}
		
		return possibles;
	}
}
