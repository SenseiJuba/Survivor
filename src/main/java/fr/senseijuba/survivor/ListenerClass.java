package fr.senseijuba.survivor;

import fr.senseijuba.survivor.commands.GetWeapon;
import fr.senseijuba.survivor.commands.SurvivorCommand;
import fr.senseijuba.survivor.database.Mariadb;
import fr.senseijuba.survivor.database.player.PlayerData;
import fr.senseijuba.survivor.database.player.PlayerDataManager;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.mobs.Dog;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.ScoreboardSign;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class ListenerClass implements Listener {

    Survivor inst = Survivor.getInstance();
    PlayerDataManager dataManager = inst.dataManager;
    Mariadb db = inst.mariadb;

    public ListenerClass() throws ClassNotFoundException {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws SQLException {
        Survivor.getInstance().getPlayerManager().getMoney().putIfAbsent(e.getPlayer().getUniqueId(), 3000);
        Survivor.getInstance().getPlayerManager().getKills().putIfAbsent(e.getPlayer().getUniqueId(), 0);
        Survivor.getInstance().getPlayerManager().getDeaths().putIfAbsent(e.getPlayer().getUniqueId(), 0);

        db.registerPlayer(e.getPlayer());
        dataManager.loadPlayerData(e.getPlayer());
        e.getPlayer().teleport(inst.getLobbySpawn());
        Title.sendTitle(e.getPlayer(), 5, 20, 5, "§cSurvivor", "bienvenue");

        Player p = e.getPlayer();
        ScoreboardSign scoreboardSign = new ScoreboardSign(p, "§eSURVIVOR");

        PlayerData data = inst.dataPlayers.get(p);
        String xp = Utils.nbToK(data.getLvl());
        String xpmax = Utils.nbToK(data.getXptolvl());

        scoreboardSign.create();
        scoreboardSign.setLine(13, "§fVotre level: §7" + data.getLvl() + "☆");
        scoreboardSign.setLine(12, "");
        scoreboardSign.setLine(11, "§fProgression: §b" + xp + "§7/§e" + xpmax);
        scoreboardSign.setLine(10, Utils.percentToBar(data.getLvl(), data.getXptolvl()));
        scoreboardSign.setLine(9, "§7");
        scoreboardSign.setLine(8, "§fGrade: a faire");
        scoreboardSign.setLine(7, "§e");
        scoreboardSign.setLine(6, "Parties jouées:" + data.getGameplayed());
        scoreboardSign.setLine(5, "Vague max: " + data.getMaxwaves());
        scoreboardSign.setLine(4, "Total kills: " + data.getKills());
        scoreboardSign.setLine(3, "Total morts: " + data.getDeaths());
        scoreboardSign.setLine(2, "§8");
        scoreboardSign.setLine(1, "§6play.hellaria.fr");

        new BukkitRunnable()
        {

            ScoreboardSign sc = scoreboardSign;
            int timer = 0;
            String string;

            @Override
            public void run()
            {
                switch (timer){
                    case 0:
                        string = "§cp§elay.hellaria.fr";
                        break;
                    case 1:
                        string = "§4p§cl§eay.hellaria.fr";
                        break;
                    case 2:
                        string = "§cp§4l§ca§ey.hellaria.fr";
                        break;
                    case 3:
                        string = "§ep§cl§4a§cy§e.hellaria.fr";
                        break;
                    case 4:
                        string = "§epl§ca§4y§c.§ehellaria.fr";
                        break;
                    case 5:
                        string = "§epla§cy§4.§ch§eellaria.fr";
                        break;
                    case 6:
                        string = "§eplay§c.§4h§ce§ellaria.fr";
                        break;
                    case 7:
                        string = "§eplay.§ch§4e§cl§elaria.fr";
                        break;
                    case 8:
                        string = "§eplay.h§ce§4l§cl§earia.fr";
                        break;
                    case 9:
                        string = "§eplay.he§cl§4l§ca§eria.fr";
                        break;
                    case 10:
                        string = "§eplay.hel§cl§4a§cr§eia.fr";
                        break;
                    case 11:
                        string = "§eplay.hell§ca§4r§ci§ea.fr";
                        break;
                    case 12:
                        string = "§eplay.hella§cr§4i§ca§e.fr";
                        break;
                    case 13:
                        string = "§eplay.hellar§ci§4a§c.§efr";
                        break;
                    case 14:
                        string = "§eplay.hellari§ca§4.§cf§er";
                        break;
                    case 15:
                        string = "§eplay.hellaria§c.§4f§cr";
                        break;
                    case 16:
                        string = "§eplay.hellaria.§cf§4r";
                        break;
                    case 17:
                        string = "§eplay.hellaria.f§cr";
                        break;
                    case 18:
                        string = "§eplay.hellaria.fr";
                        break;
                    case 20*9:
                        timer = 0;
                        break;
                }

                scoreboardSign.setLine(1, string);

                if(!inst.gameState.equals(GameState.SPAWN)){
                    scoreboardSign.destroy();
                    cancel();
                }

                timer++;
            }
        }.runTaskLater(inst, 1);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws SQLException{
        dataManager.savePlayerData(e.getPlayer());
    }

    @Deprecated
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        if(e.getMessage().startsWith("/"))
            return;

        int lvl = inst.getDataPlayers().get(e.getPlayer()).getLvl();
        String suffix;

        if(lvl>=40)
            suffix = "§2";
        else if(lvl>=30)
            suffix = "§3";
        else if(lvl>=20)
            suffix = "§6";
        else if(lvl>=10)
            suffix = "§f";
        else
            suffix = "§7";

        if(lvl>50)
            suffix = "§4[§6" + lvl/10%10 + "§2" + lvl%10 + "§3☆§5]";
        else
            suffix = suffix + "[" + lvl + "☆]";

        String msg = suffix + "§f " + e.getPlayer().getDisplayName() + " > " + e.getMessage();

        for(Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(msg);
        }

        e.setCancelled(true);
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

    public void onDeath(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            if(!(e.getDamager() instanceof Player)){

            }
            else{
                e.setCancelled(true);
            }
        }
        else if(e.getEntity() instanceof Zombie){

            fr.senseijuba.survivor.mobs.Zombie mob = null;

            for(fr.senseijuba.survivor.mobs.Zombie zombie : inst.getMobManager().getZombies().keySet()){
                if(inst.getMobManager().getZombies().get(zombie).equals(e.getEntity())){
                    inst.getMobManager().getZombies().remove(zombie);
                    mob = zombie;
                }
            }

            if(e.getDamager() instanceof Player && mob != null){
                inst.getPlayerManager().addMoney((Player) e.getDamager(), mob.getMoney());
                inst.gameCycle.addKills((Player)e.getDamager());

                ArmorStand armorStand = (ArmorStand) e.getEntity().getLocation().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.ARMOR_STAND);

                armorStand.setCustomName("§e+" + mob.getMoney());
                armorStand.setCustomNameVisible(false);
                armorStand.setBasePlate(false);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setSmall(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        armorStand.remove();
                        cancel();
                    }
                }.runTaskLater(inst, 10);
            }
        }
        else if(e.getEntity() instanceof Wolf){

            Dog mob = null;

            for(Dog dog : inst.getMobManager().getDogs().keySet()){
                if(inst.getMobManager().getDogs().get(dog).equals(e.getEntity())){
                    inst.getMobManager().getDogs().remove(dog);
                    mob = dog;
                }
            }

            if(e.getDamager() instanceof Player && mob != null){
                inst.getPlayerManager().addMoney((Player) e.getDamager(), mob.getMoney());
                inst.gameCycle.addKills((Player)e.getDamager());

                ArmorStand armorStand = (ArmorStand) e.getEntity().getLocation().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.ARMOR_STAND);

                armorStand.setCustomName("§e+" + mob.getMoney());
                armorStand.setCustomNameVisible(false);
                armorStand.setBasePlate(false);
                armorStand.setGravity(false);
                armorStand.setVisible(false);
                armorStand.setSmall(true);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        armorStand.remove();
                        cancel();
                    }
                }.runTaskLater(inst, 10);
            }
        }
    }
}
