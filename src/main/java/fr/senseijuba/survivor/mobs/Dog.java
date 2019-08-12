package fr.senseijuba.survivor.mobs;

import fr.senseijuba.survivor.Survivor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Dog extends AbstractMob{

    public Dog() {
        super(EntityType.WOLF, 0, 20, 100);
    }

    @Override
    public void spawnMob(List<Location> loctableau, int nb){

        Player p = null;

        for(Player pls : Bukkit.getOnlinePlayers()){
            p = pls;
        }

        for(int i = 0; i<nb; i++) {

            Location loc = loctableau.get(new Random().nextInt(loctableau.size()) + 1);

            org.bukkit.entity.Wolf wolf = (org.bukkit.entity.Wolf) p.getWorld().spawnEntity(loc, this.getEntity());
            wolf.setHealth(pv);
            wolf.setAngry(true);
            Survivor.getInstance().getMobManager().getDogs().putIfAbsent(this, wolf);
        }
    }
}
