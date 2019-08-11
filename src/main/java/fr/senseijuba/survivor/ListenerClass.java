package fr.senseijuba.survivor;

import fr.senseijuba.survivor.database.Mariadb;
import fr.senseijuba.survivor.database.player.PlayerDataManager;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class ListenerClass implements Listener {



    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Survivor.getInstance().getPlayerManager().getMoney().putIfAbsent(e.getPlayer().getUniqueId(), 3000);
        db.registerPlayer(e.getPlayer());
        dataManager.loadPlayerData(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws SQLException{
        dataManager.updatePlayerData(e.getPlayer());
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e)
    {
        if(!Survivor.getInstance().getMaps().contains(Survivor.getInstance().worldtoMap(e.getPlayer().getWorld())))
            return;

        Player p = e.getPlayer();
        Block b = e.getBlockPlaced();
        Map m = Survivor.getInstance().worldtoMap(p.getWorld());



        if(b.getType().equals(Material.BARRIER) && m.getZoneModifyingBarricade(p) != null) {
            if (m.getZoneModifyingBarricade(p).getModifyBarricade2().equals(p)) {
                m.getZoneModifyingBarricade(p).setBarricades2(b.getLocation());
                p.sendMessage("§aSecond bloc posé à : " + Utils.locToString(b.getLocation()) + ", barricade créée");
            } else {
                m.getZoneModifyingBarricade(p).setBarricades1(b.getLocation());
                p.sendMessage("§aPremier bloc posé à : " + Utils.locToString(b.getLocation()) + ", placer un second bloc");
            }
            e.setCancelled(true);
            return;
        }
        else if(b.getType().equals(Material.EMERALD_BLOCK) && m.getZoneModifyingDoor(p) != null){
            if(m.getZoneModifyingDoor(p).getModifyDoor2().equals(p)){

                m.getZoneModifyingDoor(p).setDoor2(b.getLocation());
                p.sendMessage("§aSecond bloc posé à : " + Utils.locToString(b.getLocation()) + ", porte créée");
            }
            else{
                m.getZoneModifyingDoor(p).setDoor1(b.getLocation());
                p.sendMessage("§aPremier bloc posé à : " + Utils.locToString(b.getLocation()) + ", placer un second bloc");
            }
            e.setCancelled(true);
            return;
        }
        else if(b.getType().equals(Material.SIGN) && m.getZoneModifyingSign(p) != null){

            Zone zone = m.getZoneModifyingSign(p);
            zone.setSignModifier(null);
            zone.setBuyingSign(b.getLocation());

            Sign l = (Sign) b;
            l.setLine(1, "§4<Zone" + zone.getName() + ">");
            l.setLine(2, "§4" + zone.getCost() + "$");

            p.sendMessage("§aPanneau de la zone " + zone.getName() + " créé, le coût est : " + zone.getCost() + "$");
        }

        if(!Survivor.getInstance().getMaps().contains(m)){
            return;
        }
        else{
            p.sendMessage("§cN'oubliez pas, vous ne pouvez pas construire sur une map enable, faites §6/survivor maps unregister §cpour pouvoir la modifier ");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){

        if(!Survivor.getInstance().getMaps().contains(Survivor.getInstance().worldtoMap(e.getPlayer().getWorld())))
            return;

        Player p = e.getPlayer();
        Map m = Survivor.getInstance().worldtoMap(p.getWorld());

        if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getPlayer().getItemInHand().getType().equals(Material.BARRIER) && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§6Destruction d'une barricade")) {
                for(Zone zone : m.getZones()){
                    for(Cuboid cube : zone.getBarricades()){
                        if(cube.blocksInside().contains(e.getClickedBlock())){
                            zone.getBarricades().remove(cube);

                            p.sendMessage("§aBarricade supprimée");

                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                p.sendMessage("§cCeci n'est pas une barricades");
            }
            else if (e.getPlayer().getItemInHand().getType().equals(Material.BARRIER) && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§6Destruction d'un point de spawn de mob")) {
                for(Zone zone : m.getZones()){
                    for(Location spawnmob : zone.getSpawnMobZones()){
                        if(spawnmob.equals(e.getClickedBlock().getLocation())){
                            zone.getSpawnMobZones().remove(spawnmob);

                            p.sendMessage("§aEndroit de spawn de mob supprimée");

                            e.setCancelled(true);
                            return;
                        }
                    }
                }
                p.sendMessage("§cCeci n'est pas un endroit de spawn de mob");
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){

        if(!Survivor.getInstance().getMaps().contains(Survivor.getInstance().worldtoMap(e.getPlayer().getWorld())))
            return;

        ItemStack itemDrop = e.getItemDrop().getItemStack();
        Player p = e.getPlayer();

        if(itemDrop.getType().equals(Material.BARRIER)){
            if(itemDrop.getItemMeta().getDisplayName().equalsIgnoreCase("§6Destruction d'un point de spawn de mob") && Survivor.getInstance().worldtoMap(p.getWorld()).getZoneDestructMobZone(p) != null){
                Survivor.getInstance().worldtoMap(p.getWorld()).getZoneDestructMobZone(p).resetDestructMob();
                p.sendMessage("§aDestruction d'un point de spawn de mob fini");
            }
            else if(itemDrop.getItemMeta().getDisplayName().equalsIgnoreCase("§6Création d'une barricade") && Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingBarricade(p) != null){
                Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingBarricade(p).resetBarricadesCréa();
                p.sendMessage("§aCréation d'une barricade arrêtée");
            }
            else if(itemDrop.getItemMeta().getDisplayName().equalsIgnoreCase("§6Destruction d'une barricade") && Survivor.getInstance().worldtoMap(p.getWorld()).getZoneDestructBarricade(p) != null){
                Survivor.getInstance().worldtoMap(p.getWorld()).getZoneDestructBarricade(p).setDestructBarricades(null);
                p.sendMessage("§aDestruction d'une barricade finie");
            }
        }
        else if(itemDrop.getType().equals(Material.EMERALD_BLOCK) && Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingDoor(p) != null){
            Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingDoor(p).resetDoor();
            p.sendMessage("§aConstruction d'une porte annulée");

        }
        else if(itemDrop.getType().equals(Material.SIGN) && Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingSign(p) != null){
            Survivor.getInstance().worldtoMap(p.getWorld()).getZoneModifyingSign(p).resetSign();
            p.sendMessage("§aConstruction d'un panneau annulée");
        }
        else{
            return;
        }

        e.setCancelled(true);
        p.getInventory().removeItem(itemDrop);
    }
}
