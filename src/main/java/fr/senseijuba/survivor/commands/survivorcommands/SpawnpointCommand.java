package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.entity.Player;

public class SpawnpointCommand extends SurvivorArgCommand {

    public SpawnpointCommand()
    {
        super("spawnpoint", "définir le point d'apparition des joueurs", "", false, 1);
    }

    @Override
    public void execute(Player p, String[] args)
    {

        Map map = Survivor.getInstance().worldtoMap(p.getWorld());

        map.setSpawnpoint(p.getLocation());

        p.sendMessage("§aLe spawn de la map a été enregistré en " + Utils.locToString(p.getLocation()));
    }
}
