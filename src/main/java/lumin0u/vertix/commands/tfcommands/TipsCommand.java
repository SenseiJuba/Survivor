package fr.lumin0u.vertix.commands.tfcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;

public class TipsCommand extends TfArgCommand
{
	private BukkitRunnable saveTipsRunnable;
	
	public TipsCommand()
	{
		super("tips", "aide pour les tips", "help", false, 0);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		if(saveTipsRunnable != null)
			saveTipsRunnable.cancel();

		saveTipsRunnable = new BukkitRunnable()
		{
			@Override
			public void run()
			{
				TF.getInstance().saveTips();
				// TF.debug("Saved");
			}
		};

		saveTipsRunnable.runTaskLater(TF.getInstance(), 200);

		if(args.length < 1)
		{
			p.sendMessage("§0------------------------------------------------"//
					+ "\n§2-Utilisation de la commande /tf tips : "//
					+ "\n§b-/tf tips add <tip> §3: ajouter un tip (n'oubliez pas de mettre des couleurs avec '&', faites '\\&' pour garder un &)"//
					+ "\n§b-/tf tips rm <id du tip> §3: enlever un tip"//
					+ "\n§b-/tf tips list §3: liste des tips avec les id");

			return;
		}

		else if(args[0].equalsIgnoreCase("add"))
		{
			String tache = String.join(" ", args).replaceAll(args[0] + " ", "").replaceAll("\\\\&", "JEVEUXMETTREUNE").replaceAll("&", "§").replaceAll("JEVEUXMETTREUNE", "&");
			TF.getInstance().getTips().add(tache);

			p.sendMessage("§aTip ajoutée");

			return;
		}

		else if(args[0].equalsIgnoreCase("list"))
		{
			String list = "§6Tips :";

			if(TF.getInstance().getTips().size() == 0)
				list = list + "\n§e-aucun";

			for(int i = 0; i < TF.getInstance().getTips().size(); i++)
			{
				list = list + "\n§e" + i + " : §r" + TF.getInstance().getTips().get(i);
			}

			p.sendMessage(list);

			return;
		}

		else if(args[0].equalsIgnoreCase("rm") && args.length > 1)
		{
			try
			{
				int id = Integer.parseInt(args[1]);

				if(id < 0 || id > TF.getInstance().getTips().size() - 1)
				{
					p.sendMessage("§cCe tip n'existe pas.");
				}

				else
				{
					TF.getInstance().getTips().remove(id);
					p.sendMessage("§aTip supprimée");

					String list = "§6Tips :";

					if(TF.getInstance().getTips().size() == 0)
						list = list + "\n§e-aucun";

					for(int i = 0; i < TF.getInstance().getTips().size(); i++)
					{
						list = list + "\n§e" + i + " : §r" + TF.getInstance().getTips().get(i);
					}

					p.sendMessage(list);
				}
			}catch(NumberFormatException e)
			{
				p.sendMessage("§c" + args[1] + " n'est pas un nombre. Désolé.");
			}

			return;
		}

		p.sendMessage("§0------------------------------------------------"//
				+ "\n§2-Utilisation de la commande /tf tips : "//
				+ "\n§b-/tf tips add <tip> §3: ajouter un tip (n'oubliez pas de mettre des couleurs avec '&')"//
				+ "\n§b-/tf tips rm <id du tip> §3: enlever un tip"//
				+ "\n§b-/tf tips list §3: liste des tips avec les id");

		return;
	}
	
	@Override
	public List<String> getPossibleArgs(Player executer, String[] args)
	{
		List<String> possibles = new ArrayList<>();
		
		if(args.length == 2)
		{
			possibles.add("help");
			possibles.add("add");
			possibles.add("rm");
			possibles.add("list");
		}
		
		return possibles;
	}
}
