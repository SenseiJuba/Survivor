package fr.senseijuba.survivor.mobs;

import fr.senseijuba.survivor.Survivor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

import static java.lang.Math.round;

public abstract class Zombie extends AbstractMob {

    public Zombie() {
        super(EntityType.ZOMBIE, 0, 20, null, null, 100);
    }

    public void spawnMob(List<Location> loctableau, int nb){

        Player p = null;

        for(Player pls : Bukkit.getOnlinePlayers()){
            p = pls;
        }

        for(int i = 0; i<nb; i++) {

            Location loc = loctableau.get(new Random().nextInt(loctableau.size()-1));

            org.bukkit.entity.Zombie zombie = (org.bukkit.entity.Zombie) p.getWorld().spawnEntity(loc, this.getEntity());
            zombie.setHealth(pv);

            int vague = Survivor.getInstance().getVague();

            if (5 <= vague && vague < 10) {
                if (new Random().nextInt(20) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.LEATHER_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.LEATHER_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.LEATHER_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.LEATHER_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.WOOD_SWORD);
                }
            } else if (10 <= vague && vague < 15) {
                if (new Random().nextInt(15) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.LEATHER_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.LEATHER_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.LEATHER_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.LEATHER_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.WOOD_SWORD);
                }
                else if (new Random().nextInt(20) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.IRON_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.IRON_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.IRON_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.IRON_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.IRON_SWORD);
                }
            } else if (15 <= vague && vague < 20) {
                if (new Random().nextInt(10) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.LEATHER_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.LEATHER_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.LEATHER_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.LEATHER_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.WOOD_SWORD);
                }
                else if (new Random().nextInt(15) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.IRON_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.IRON_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.IRON_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.IRON_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.IRON_SWORD);
                }
            } else if (20 <= vague) {
                if (new Random().nextInt(10) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.LEATHER_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.LEATHER_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.LEATHER_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.LEATHER_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.WOOD_SWORD);
                }
                else if (new Random().nextInt(15) == 0) {
                    zombie.getEquipment().getBoots().setType(Material.IRON_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.IRON_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.IRON_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.IRON_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.IRON_SWORD);
                }
                else if(new Random().nextInt(50-vague/2) == 0){
                    zombie.getEquipment().getBoots().setType(Material.DIAMOND_BOOTS);
                    zombie.getEquipment().getLeggings().setType(Material.DIAMOND_LEGGINGS);
                    zombie.getEquipment().getChestplate().setType(Material.DIAMOND_CHESTPLATE);
                    zombie.getEquipment().getHelmet().setType(Material.DIAMOND_HELMET);
                    zombie.getEquipment().getItemInHand().setType(Material.DIAMOND_SWORD);
                }
            }

            Survivor.getInstance().getMobManager().getZombies().putIfAbsent(this, zombie);
        }
    }
}
