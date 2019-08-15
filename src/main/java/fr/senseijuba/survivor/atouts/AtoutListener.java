package fr.senseijuba.survivor.atouts;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class AtoutListener implements Listener {

    private Survivor inst = Survivor.getInstance();
    private static HashMap<UUID, Atout> atoutchoosen;

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        inst.getPlayerAtout().put(e.getPlayer().getUniqueId(), new ArrayList<>());
        e.getPlayer().getInventory().setItem(8, Atout.nameTag());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        ItemStack item = e.getItem();

        if (item == null || !e.getAction().equals(Action.RIGHT_CLICK_AIR) || !e.getItem().isSimilar(Atout.nameTag())){
            return;
        }

        if(e.getItem().isSimilar(Atout.nameTag())){

            e.getPlayer().openInventory(Atout.getInventory(e.getPlayer()));
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    @Deprecated
    public void onInventoryClick(PlayerInteractEvent e){

        Player player = e.getPlayer();

        if(player.getInventory().equals(Atout.getInventory(e.getPlayer()))){

            if(e.getItem() == null)
                return;

            if(e.getItem().getType().equals(Material.STAINED_GLASS_PANE)){
                player.openInventory(Atout.getInventory(e.getPlayer()));
            }
            else if(e.getItem().getType().equals(Material.COOKED_CHICKEN)){
                player.openInventory(Atout.getInventory(e.getPlayer()));
                inst.getPlayerManager().setTipsActive(player, false);
            }
            else if(e.getItem().getType().equals(Material.RAW_CHICKEN)){
                player.openInventory(Atout.getInventory(e.getPlayer()));
                inst.getPlayerManager().setTipsActive(player, true);
            }
            else {
                Atout atout = Atout.byRepItem(e.getMaterial());

                if (!inst.getPlayerAtout().get(player.getUniqueId()).contains(atout)) {
                    if (atout.getMoney() <= inst.getPlayerManager().getMoney().get(player.getUniqueId())) {
                        player.openInventory(getPlayerAtoutInv(player));
                        atoutchoosen.put(player.getUniqueId(), atout);
                    }
                }
            }

            e.setCancelled(true);
            return;
        }

        if(player.getInventory().equals(getPlayerAtoutInv(e.getPlayer()))){

            if(e.getItem() == null)
                return;

            if(e.getItem().getType().equals(Material.STAINED_GLASS_PANE)){
                e.setCancelled(true);
                return;
            }
            else if(e.getItem().getType().equals(Material.BARRIER)){
                Atout nextAtout = atoutchoosen.get(player.getUniqueId());

                if(inst.getPlayerManager().getMoney().get(player.getUniqueId())>= nextAtout.getMoney()){
                    inst.getPlayerManager().getMoney().replace(player.getUniqueId(), inst.getPlayerManager().getMoney().get(player.getUniqueId())-nextAtout.getMoney());
                    inst.getPlayerAtout().get(player.getUniqueId()).add(nextAtout);
                }

                updateAtout(player);
            }
            else{
                Atout previousAtout = Atout.byRepItem(e.getMaterial());
                Atout nextAtout = atoutchoosen.get(player.getUniqueId());

                if(inst.getPlayerManager().getMoney().get(player.getUniqueId())>= nextAtout.getMoney()){
                    inst.getPlayerManager().getMoney().replace(player.getUniqueId(), inst.getPlayerManager().getMoney().get(player.getUniqueId())-nextAtout.getMoney());
                    inst.getPlayerAtout().get(player.getUniqueId()).remove(previousAtout);
                    inst.getPlayerAtout().get(player.getUniqueId()).add(nextAtout);
                }

                updateAtout(player);
            }

            player.closeInventory();
            e.setCancelled(true);
            return;
        }
    }

    public Inventory getPlayerAtoutInv(Player player){
        Inventory inv = Bukkit.createInventory(null, 9*3, "§eChoisir une place d'atout");

        ItemStack barrer = new ItemBuilder(Material.BARRIER)
                .name("Aucun atout")
                .build();

        for(ItemStack item : inv.getContents()){
            item = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name("")
                    .data(10)
                    .build();
        }

        int i = 0;
        List<Integer> places = Arrays.asList(11, 13, 15);

        for(Atout atout : inst.getPlayerAtout().get(player)){
            i++;
            inv.setItem(places.get(i), atout.getRepItemChange(player, true));
        }

        switch (i){
            case(0):
                for(int t : places){
                    inv.setItem(places.get(t), barrer);
                }
                break;
            case(1):
                inv.setItem(13, barrer);
                inv.setItem(15, barrer);
                break;
            case(2):
                inv.setItem(15, barrer);
                break;
        }

        return inv;
    }

    public static void updateAtout(Player player){

        Survivor inst = Survivor.getInstance();

        for(ItemStack armor : player.getInventory().getArmorContents()){
            armor.setType(null);
        }

        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }

        inst.getPlayerManager().setDoubleCoup(player, false);
        inst.getPlayerManager().setSpeedCola(player, false);
        inst.getPlayerManager().setThreeWeapons(player, false);
        inst.getPlayerManager().setQuickRevive(player, false);
        inst.getPlayerManager().setGrave(player, false);
        player.setWalkSpeed(0.1f);
        player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));

        for(Atout atout : inst.getPlayerAtout().get(player)){

            switch(atout.getPlaceInInventory()){
                case(0):
                    player.setMaxHealth(40);
                    player.setHealth(20);
                    player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    break;
                case(1):
                    inst.getPlayerManager().setDoubleCoup(player, true);
                    break;
                case(2):
                    player.setWalkSpeed(0.2f);
                    break;
                case(3):
                    inst.getPlayerManager().setSpeedCola(player, true);
                    break;
                case(4):
                    inst.getPlayerManager().setThreeWeapons(player, true);//TODO ALL ATOUTS APPLICATION
                    break;
                case(5):
                    inst.getPlayerManager().setQuickRevive(player, true);
                    break;
                case(6):
                    inst.getPlayerManager().setGrave(player, true);
                    break;
            }
        }
    }
}
