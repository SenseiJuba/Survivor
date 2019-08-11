package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TipsCommand extends SurvivorArgCommand {

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
                Survivor.getInstance().saveTips();
                // TF.debug("Saved");
            }
        };

        saveTipsRunnable.runTaskLater(Survivor.getInstance(), 200);

        if(args.length < 1)
        {
            p.sendMessage("§0------------------------------------------------"//
                    + "\n§2-Utilisation de la commande /survivor tips : "//
                    + "\n§b-/survivor tips add <tip> §3: ajouter un tip (n'oubliez pas de mettre des couleurs avec '&', faites '\\&' pour garder un &)"//
                    + "\n§b-/survivor tips rm <id du tip> §3: enlever un tip"//
                    + "\n§b-/survivor tips list §3: liste des tips avec les id");

            return;
        }

        else if(args[0].equalsIgnoreCase("add"))
        {
            String tache = String.join(" ", args).replaceAll(args[0] + " ", "").replaceAll("\\\\&", "JEVEUXMETTREUNE").replaceAll("&", "�").replaceAll("JEVEUXMETTREUNE", "&");
            Survivor.getInstance().getTips().add(tache);

            p.sendMessage("§aTip ajoutée");

            return;
        }

        else if(args[0].equalsIgnoreCase("list"))
        {
            String list = "§6Tips :";

            if(Survivor.getInstance().getTips().size() == 0)
                list = list + "\n§e-aucun";

            for(int i = 0; i < Survivor.getInstance().getTips().size(); i++)
            {
                list = list + "\n§e" + i + " : §r" + Survivor.getInstance().getTips().get(i);
            }

            p.sendMessage(list);

            return;
        }

        else if(args[0].equalsIgnoreCase("rm") && args.length > 1)
        {
            try
            {
                int id = Integer.parseInt(args[1]);

                if(id < 0 || id > Survivor.getInstance().getTips().size() - 1)
                {
                    p.sendMessage("§cCe tip n'existe pas.");
                }

                else
                {
                    Survivor.getInstance().getTips().remove(id);
                    p.sendMessage("§aTip supprimée");

                    String list = "§6Tips :";

                    if(Survivor.getInstance().getTips().size() == 0)
                        list = list + "\n§e-aucun";

                    for(int i = 0; i < Survivor.getInstance().getTips().size(); i++)
                    {
                        list = list + "\n§e" + i + " : �r" + Survivor.getInstance().getTips().get(i);
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
                + "\n§2-Utilisation de la commande /survivor tips : "//
                + "\n§b-/survivor tips add <tip> §3: ajouter un tip (n'oubliez pas de mettre des couleurs avec '&', faites '\\&' pour garder un &)"//
                + "\n§b-/survivor tips rm <id du tip> §3: enlever un tip"//
                + "\n§b-/survivor tips list §3: liste des tips avec les id");

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
