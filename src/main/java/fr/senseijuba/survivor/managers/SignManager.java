package fr.senseijuba.survivor.managers;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.atouts.Atout;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.utils.ItemBuilder;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SignManager implements Listener {

    Survivor inst;
    HashMap<UUID, AbstractWeapon> weaponWanted;
    HashMap<AbstractWeapon, Integer> weaponPrize;

    public SignManager(Survivor inst){
        this.inst = inst;
    }

    @EventHandler
    public void onSignPlace(BlockPlaceEvent e) {

        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        AbstractWeapon weapon = null;

        for (AbstractWeapon weapons : inst.getWeaponManager().listWeapons()){
            if(e.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(weapons.getName()))
                weapon = weapons;
        }

        if (b.getType().equals(Material.SIGN) && weapon != null) {
            Sign l = (Sign) b;
            l.setLine(1, "§4<Arme " + weapon.getName() + " >");
            l.setLine(2, "§4 " + e.getItemInHand().getItemMeta().getLore().get(0) + " $");

            p.sendMessage("§aPanneau de l'arme " + weapon.getName() + " créé, le coût est : " + e.getItemInHand().getItemMeta().getLore().get(0) + "$");
        }
    }

    public void onSignClick(PlayerInteractEvent e){

        Player player = e.getPlayer();

        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.SIGN)){
            Sign sign = (Sign) e.getClickedBlock();

            String type = sign.getLine(0);
            if(type.contains("Zone")){
                String name = Arrays.asList(type.split(" ")).get(1);

                for(Zone zone : inst.getCurrentMap().getZones()){
                    if(zone.getName().equalsIgnoreCase(name)){
                        if(inst.getPlayerManager().getMoney().get(e.getPlayer()) >= zone.getCost()){
                            zone.openZone(e.getPlayer());
                            inst.getPlayerManager().removeMoney(e.getPlayer(), zone.getCost());
                        }
                        else{
                            e.getPlayer().sendMessage("§cVous n'avez pas assez d'argent");
                        }
                    }
                }
            }
            else if(type.contains("Arme")){
                String name = Arrays.asList(type.split(" ")).get(1);
                int cost = Integer.parseInt(Arrays.asList(sign.getLine(1).split(" ")).get(1));

                for(AbstractWeapon weapon : inst.getWeaponManager().listWeapons()){
                    if(weapon.getName().equalsIgnoreCase(name)){
                        if(inst.getPlayerManager().getMoney().get(e.getPlayer()) >= cost){
                            e.getPlayer().openInventory(weaponBuyInventory(e.getPlayer()));
                            weaponWanted.put(player.getUniqueId(), weapon);
                            weaponPrize.put(weapon, cost);
                        }
                        else{
                            e.getPlayer().sendMessage("§cVous n'avez pas assez d'argent");
                        }
                    }
                }
            }
        }

        if(player.getInventory().equals(weaponBuyInventory(e.getPlayer()))) {

            AbstractWeapon weapon = null;

            for(AbstractWeapon weapons : inst.getWeaponManager().listWeapons()){
                if(weaponWanted.get(player.getUniqueId()).equals(weapons)){
                    weapon = weapons;
                    continue;
                }
            }

            if (e.getItem() == null || weapon == null)
                return;

            if (e.getItem().getType().equals(Material.STAINED_GLASS_PANE)) {
                e.setCancelled(true);
                return;
            } else if (e.getItem().getType().equals(Material.BARRIER)) {

                if (inst.getPlayerManager().getMoney().get(player.getUniqueId()) >= weaponPrize.get(weapon)) {
                    inst.getPlayerManager().getMoney().replace(player.getUniqueId(), inst.getPlayerManager().getMoney().get(player.getUniqueId()) - weaponPrize.get(weapon));
                    inst.getPlayerWeapon().get(player.getUniqueId()).add(weapon);
                    player.getInventory().addItem(weapon.getItem(1));
                    weaponWanted.remove(player.getUniqueId());
                }
            }
            else if(e.getItem().getType().equals(Material.PAPER)){
                weaponWanted.remove(player.getUniqueId());
                player.closeInventory();
            }
            else {

                AbstractWeapon previousWeapon = null;

                for(AbstractWeapon weapons : inst.getPlayerWeapon().get(player)){
                    if(weapons.getItem(1).isSimilar(e.getItem()))
                        previousWeapon = weapons;
                }

                if (inst.getPlayerManager().getMoney().get(player.getUniqueId()) >= weaponPrize.get(weapon)) {
                    inst.getPlayerManager().getMoney().replace(player.getUniqueId(), inst.getPlayerManager().getMoney().get(player.getUniqueId()) - weaponPrize.get(weapon));
                    inst.getPlayerWeapon().get(player.getUniqueId()).remove(previousWeapon);
                    inst.getPlayerWeapon().get(player.getUniqueId()).add(weapon);
                    player.getInventory().remove(previousWeapon.getItem(1).getType());
                    player.getInventory().addItem(weapon.getItem(1));
                    weaponWanted.remove(player.getUniqueId());
                }

            }

            player.closeInventory();
            e.setCancelled(true);
            return;
        }
    }



    public Inventory weaponBuyInventory(Player player){
        Inventory inv = Bukkit.createInventory(null, 9*3, "§eChoisir une place d'arme");

        ItemStack barrer = new ItemBuilder(Material.BARRIER)
                .name("Aucune arme")
                .build();

        for(ItemStack item : inv.getContents()){
            item = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name("")
                    .data(10)
                    .build();
        }

        int i = 0;
        List<Integer> places2 = Arrays.asList(12, 14);
        List<Integer> places3 = Arrays.asList(11, 13, 15);

        if(inst.getPlayerManager().hasThreeWeapons(player)){
            for(AbstractWeapon weapon : inst.getPlayerWeapon().get(player)){
                i++;
                inv.setItem(places3.get(i), weapon.getItem(1));
            }

            switch (i){
                case(0):
                    for(int t : places3){
                        inv.setItem(places3.get(t), barrer);
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
        }
        else{
            for(AbstractWeapon weapon : inst.getPlayerWeapon().get(player)){
                i++;
                inv.setItem(places2.get(i), weapon.getItem(1));
            }

            switch (i){
                case(0):
                    for(int t : places2){
                        inv.setItem(places2.get(i), barrer);
                    }
                    break;
                case(1):
                    inv.setItem(14, barrer);
                    break;
            }
        }

        inv.setItem(26, new ItemBuilder(Material.PAPER)
        .name("§cQuitter l'inventaire")
        .build());

        return inv;
    }
}
