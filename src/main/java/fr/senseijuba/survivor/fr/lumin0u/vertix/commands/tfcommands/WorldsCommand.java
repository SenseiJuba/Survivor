package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.GameType;
import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;

public class WorldsCommand extends TfArgCommand
{
	public WorldsCommand()
	{
		super("worlds", "§4§la regarder §7aide pour les mondes", "help", false, 0);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		String worldsHelp = "§0------------------------------------------------"//
				+ "\n§2-Utilisation de la commande /tf worlds : "//
				+ "\n§b-/tf worlds register §3: activer le monde dans lequel vous effectuez la commande pour que le plugin le prenne en compte"//
				+ "\n§b-/tf worlds setType <gameType> §3: changer le mode de jeu du monde"//
				+ "\n§b-/tf worlds gameTypes §3: vous donne la liste des modes de jeu possibles"//
				+ "\n§b-/tf worlds unregister §3: désactiver le monde dans lequel vous effectuez la commande pour que le plugin ne le prenne plus en compte";


//		TF.debug(gm);
//		TF.debug(args[0]);
//		TF.debug(args[1]);
		
		if(args.length < 1)
		{
			p.sendMessage(worldsHelp);

			return;
		}

		else if(args[0].equalsIgnoreCase("register"))
		{
			if(!TF.getInstance().getWorlds().contains(p.getWorld()))
			{
				TF.getInstance().addWorld(p.getWorld());
				p.sendMessage("§aMonde déclaré !");
			}

			else
				p.sendMessage("§cCe monde est deja pris en compte !");

			return;
		}

		else if(args[0].equalsIgnoreCase("gameTypes"))
		{
			String types = "";
			boolean dark = false;

			for(GameType type : GameType.notHiddenValues())
			{
				types = types + (dark ? "§5" : "§d") + type.get_Name() + " , ";
				dark = !dark;
			}

			types = types.substring(0, types.length() - 3);

			p.sendMessage(types);

			return;
		}

		else if(args[0].equalsIgnoreCase("unregister"))
		{
			TF.getInstance().disableWorld(p.getWorld());
			p.sendMessage("§aMonde désactivé !");

			return;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("settype") && gm != null)
		{
			GameType gt = GameType.byName(args[1]);

			if(gt != null)
			{
				if(!gm.isGameStarted())
				{
					p.sendMessage("§aLe mode de jeu de ce monde est désormais : §2" + gt.getName());
					gm.setType(gt);
					
					return;
				}

				else
					p.sendMessage("§cVeuillez attendre que la partie soit finie !");
			}

			else
				p.sendMessage("§cCe mode de jeu n'existe pas. Utilisez '/tf worlds gametypes' pour voir la liste des modes de jeux possibles");

			return;
		}

		if(gm == null)
			p.sendMessage("§cCe monde n'a pas été register. Utilisez '/tf worlds register'");

		else
			p.sendMessage(worldsHelp);

		return;
	}
	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		List<String> possibles = new ArrayList<>();
		
		if(args.length == 2)
		{
			possibles.add("help");
			possibles.add("register");
			possibles.add("setType");
			possibles.add("gameTypes");
			possibles.add("unregister");
		}
		
		else if(args.length == 3 && args[1].equalsIgnoreCase("setType"))
		{
			for(GameType g : GameType.values())
				if(!g.isHidden())
					possibles.add(g.get_Name());
		}
		
		return possibles;
	}
}
