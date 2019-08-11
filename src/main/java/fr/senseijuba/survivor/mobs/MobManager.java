package fr.senseijuba.survivor.mobs;

import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;

public class MobManager implements Listener {

    private HashMap<Zombie, Entity> zombies;
    private HashMap<Dog, Entity> dogs;

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e){
        if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)){
           e.setCancelled(true);
        }

        Creature entity = (Creature) e.getEntity();

        if(entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.WOLF)){
            Player p = Utils.nearestPlayer(entity.getLocation());
            if(p !=  null){
                entity.setTarget(p);
            }
        }
    }

    public HashMap<Zombie, Entity> getZombies() {
        return zombies;
    }

    public void setZombies(HashMap<Zombie, Entity> zombies) {
        this.zombies = zombies;
    }

    public HashMap<Dog, Entity> getDogs() {
        return dogs;
    }

    public void setDogs(HashMap<Dog, Entity> dogs) {
        this.dogs = dogs;
    }
}
