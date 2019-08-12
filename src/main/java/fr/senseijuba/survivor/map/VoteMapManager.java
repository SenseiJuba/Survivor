package fr.senseijuba.survivor.map;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.cycle.GameCycle;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.spawn.item.SpawnItem;
import fr.senseijuba.survivor.spawn.items.SimpleSpawnItem;
import fr.senseijuba.survivor.utils.ItemBuilder;
import fr.senseijuba.survivor.utils.config.ConfigEntries;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VoteMapManager implements Listener {

    Survivor inst;
    Inventory voteInv = Bukkit.createInventory(null, 9*6, ConfigEntries.INVENTORY_VOTEMAP);
    ItemStack voteItem;
    int votant = 0;

    public VoteMapManager(Survivor inst){
        this.inst = inst;

        voteItem = new ItemBuilder(Material.NAME_TAG)
                .name(ConfigEntries.INVENTORY_VOTEMAP)
                .enchantment(Enchantment.DEPTH_STRIDER)
                .addflag(ItemFlag.HIDE_ENCHANTS)
                .build();

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                int i = 0;
                if(inst.timer <= 3)
                    for(Player player : Bukkit.getOnlinePlayers()){
                        player.getInventory().remove(voteItem);
                    }
                    cancel();

                int test = inst.getVoteAleatoire();

                for(int value : inst.getVote().values()){
                    if(value > test){
                        test = value;
                    }
                }

                for(Map map : inst.getVote().keySet()){
                    voteInv.setItem(i, map.getMapIcon());
                    i++;


                    for(ItemStack item : voteToPane(map.getPrefix() + map.getName(), votant != 0 ? inst.getVote().get(map)/votant : 0 , inst.getVote().get(map) == test ? true : false)){
                        voteInv.setItem(i, item);
                        i++;
                    }
                }

                for(ItemStack item : voteToPane("Vote Aléatoire", votant != 0 ? inst.getVoteAleatoire()/votant : 0 , inst.getVoteAleatoire() == test ? true : false)){
                    voteInv.setItem(i, item);
                    i++;
                }
            }
        }.runTaskTimer(inst, 0, 10);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(inst.gameState.equals(GameState.SPAWN)){
            e.getPlayer().getInventory().setItem(0, voteItem);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();

        if(inst.gameState.equals(GameState.SPAWN)){
            votant--;
            inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player)) - 1);
            inst.getHasVoted().remove(player);
            inst.getPlayervote().remove(player);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getItem().isSimilar(voteItem) && inst.gameState.equals(GameState.SPAWN)){
            e.getPlayer().openInventory(voteInv);
        }
        e.setCancelled(true);
    }

    @Deprecated
    @EventHandler
    public void onClick(InventoryClickEvent e){

        ItemStack item = e.getCurrentItem();

        if(!(e.getWhoClicked() instanceof Player))
            e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();

        if(e.getClickedInventory().getName().equalsIgnoreCase(ConfigEntries.INVENTORY_VOTEMAP)){

            if(inst.getHasVoted().contains(player)) {
                if (inst.getPlayervote().containsKey(player)) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(inst.getPlayervote().get(player).getName())) {
                        votant--;
                        inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player)) - 1);
                        inst.getHasVoted().remove(player);
                        inst.getPlayervote().remove(player);
                    } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("Vote Aléatoire")) {
                        inst.getPlayervote().remove(player);
                        inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player)) - 1);
                        inst.addVoteAleatoire(1);
                    } else {
                        if (inst.mapByName(item.getItemMeta().getDisplayName()) != null) {
                            Map map = inst.mapByName(item.getItemMeta().getDisplayName());
                            inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player)) - 1);
                            inst.getPlayervote().replace(player, map);
                            inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player)) + 1);
                        }
                    }
                } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("Vote Aléatoire")) {
                    inst.removeVoteAleatoire(1);
                    votant--;
                }

            } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase("Vote Aléatoire")) {
                inst.addVoteAleatoire(1);
                inst.getHasVoted().add(player);
                votant++;
            }
            else{
                if(inst.mapByName(item.getItemMeta().getDisplayName()) != null){
                    Map map = inst.mapByName(item.getItemMeta().getDisplayName());
                    inst.getHasVoted().add(player);
                    inst.getPlayervote().put(player, map);
                    inst.getVote().replace(inst.getPlayervote().get(player), inst.getVote().get(inst.getPlayervote().get(player)), inst.getVote().get(inst.getPlayervote().get(player))+1);
                    votant++;
                }
            }

            player.closeInventory();
            player.openInventory(voteInv);
            e.setCancelled(true);

        }
    }

    public Inventory getVoteInv() {
        return voteInv;
    }

    public void setVoteInv(Inventory voteInv) {
        this.voteInv = voteInv;
    }

    public List<ItemStack> voteToPane(String mapname, double vote, boolean isMax){

        ItemStack graypane;
        ItemStack greenpane;

        if(isMax) {
            graypane = new ItemBuilder(Material.STAINED_GLASS)
                    .name(ConfigEntries.INVENTORY_VOTEMAP_PANE)
                    .lore(Arrays.asList(ConfigEntries.INVENTORY_VOTEMAP_LORE.split("/")))
                    .data(8)
                    .enchantment(Enchantment.SILK_TOUCH)
                    .addflag(ItemFlag.HIDE_ENCHANTS)
                    .build();

            greenpane = new ItemBuilder(Material.STAINED_GLASS)
                    .name(ConfigEntries.INVENTORY_VOTEMAP_PANE)
                    .lore(Arrays.asList(ConfigEntries.INVENTORY_VOTEMAP_LORE.split("/")))
                    .data(5)
                    .enchantment(Enchantment.SILK_TOUCH)
                    .addflag(ItemFlag.HIDE_ENCHANTS)
                    .build();
        }
        else{
            graypane = new ItemBuilder(Material.STAINED_GLASS)
                    .name(ConfigEntries.INVENTORY_VOTEMAP_PANE)
                    .lore(Arrays.asList(ConfigEntries.INVENTORY_VOTEMAP_LORE.split("/")))
                    .data(8)
                    .build();

            greenpane = new ItemBuilder(Material.STAINED_GLASS)
                    .name(ConfigEntries.INVENTORY_VOTEMAP_PANE)
                    .lore(Arrays.asList(ConfigEntries.INVENTORY_VOTEMAP_LORE.split("/")))
                    .data(5)
                    .build();
        }

        if(vote < 100/8)
            return addXtimes(graypane, 8);

        else if(vote < 100/8*2)
            return addXtimes(graypane, greenpane, 7, 1);

        else if(vote < 100/8*3)
            return addXtimes(graypane, greenpane, 6, 2);

        else if(vote < 100/8*4)
            return addXtimes(graypane, greenpane, 5, 3);

        else if(vote < 100/8*5)
            return addXtimes(graypane, greenpane, 4, 4);

        else if(vote < 100/8*6)
            return addXtimes(graypane, greenpane, 3, 5);

        else if(vote < 100/8*7)
            return addXtimes(graypane, greenpane, 2, 6);

        else if(vote < 100/8*8)
            return addXtimes(graypane, greenpane, 1, 7);

        else
            return addXtimes(greenpane, 8);

    }

    public List<ItemStack> addXtimes(ItemStack item, int times){
        List<ItemStack> items = new ArrayList<>();
        for(int i=0;i<times;i++){
            items.add(item);
        }
        return items;
    }

    public List<ItemStack> addXtimes(ItemStack item1, ItemStack item2, int times1, int times2){
        List<ItemStack> items = new ArrayList<>();
        for(int i=0;i<times1;i++){
            items.add(item1);
        }
        for(int i=0;i<times2;i++){
            items.add(item2);
        }
        return items;
    }
}
