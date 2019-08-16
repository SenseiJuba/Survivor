package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StartCommand extends SurvivorArgCommand {

    public StartCommand(){ super("start", "lancer la partie", "", false, 0); }

    @Override
    public void execute(Player p, String[] args) {
        if(!Survivor.getInstance().getMaps().contains(Survivor.getInstance().worldtoMap(p.getWorld())))
        {
            p.sendMessage("§cImpossible de lancer une partie sur ce monde.");
        }

        if(!Survivor.getInstance().gameState.equals(GameState.STARTED))
        {
            if(args.length > 1 && args[1].equalsIgnoreCase("cheat"))
                Bukkit.dispatchCommand(p, "/survivor cheat");

            //TODO forcestart
        }

        else
        {
            p.sendMessage("§cLa partie est déjà lancée !");
        }
    }
}
