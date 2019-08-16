package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.Survivor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class MapsCommand extends SurvivorArgCommand {

    public MapsCommand()
    {
        super("maps", "§4§la regarder §7aide pour les maps", "help", false, 0);
    }

    @Override
    public void execute(Player p, String[] args)
    {
        String mapsHelp = "§0------------------------------------------------"//
                + "\n§2-Utilisation de la commande /survivor maps : "//
                + "\n§b-/survivor maps register §3: activer le monde dans lequel vous effectuez la commande pour que le plugin le prenne en compte"//
                + "\n§b-/survivor maps list §3: vous donne la liste des mondes"//
                + "\n§b-/survivor maps mapIcon <Material.ITEM> §3: change l'icone de la map"//
                + "\n§b-/survivor maps place <place> §3: change le nombre de place"//
                + "\n§b-/survivor maps unregister §3: d§sactiver le monde dans lequel vous effectuez la commande pour que le plugin ne le prenne plus en compte";


//		TF.debug(gm);
//		TF.debug(args[0]);
//		TF.debug(args[1]);


        Map map = Survivor.getInstance().emptyMap(p.getWorld(), p.getWorld().getName());

        if(Survivor.getInstance().getMaps().contains(Survivor.getInstance().worldtoMap(p.getWorld()))){
            map = Survivor.getInstance().worldtoMap(p.getWorld());
        }

        if(args.length < 1)
        {
            p.sendMessage(mapsHelp);

            return;
        }

        else if(args[0].equalsIgnoreCase("register") && !args[1].isEmpty())
        {

            if(!Survivor.getInstance().getMaps().contains(map))
            {
                Survivor.getInstance().addMap(map);
                p.sendMessage("§aMonde déclaré !");
            }

            else
                p.sendMessage("§cCe monde est déjà pris en compte !");

            return;
        }

        else if(args[0].equalsIgnoreCase("list")){

            for(Map m : Survivor.getInstance().getMaps()){
                p.sendMessage(m.getPrefix() + m.getName());
            }
        }

        else if(Survivor.getInstance().getMaps().contains(map)) {

            if (args[0].equalsIgnoreCase("unregister")) {
                Survivor.getInstance().disableMap(map);
                p.sendMessage("§aMonde désactivé !");

                return;
            }

            else if(args[0].equalsIgnoreCase("mapIcon")) {
                if (Material.getMaterial(args[1]) != null) {
                    map.setMapIcon(Material.getMaterial(args[1]));
                }
                else {
                    p.sendMessage("§c" + args[1] + " n'est pas un item, écrire <Material.ITEM>");
                }
                return;
            }

            else if(args[0].equalsIgnoreCase("place")){
                try{
                    map.setPlace(Integer.parseInt(args[0]));
                } catch (Exception e) {
                    p.sendMessage("§c" + args[1] + " n'est pas un nombre");
                }
            }
        }

        p.sendMessage(mapsHelp);

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
            possibles.add("list");
            possibles.add("mapIcon");
            possibles.add("place");
            possibles.add("unregister");
        }

        return possibles;
    }
}
