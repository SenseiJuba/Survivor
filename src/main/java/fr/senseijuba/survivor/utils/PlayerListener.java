package ageofempire.listener;

import ageofempire.Main;
import ageofempire.ScoreboardSign;
import ageofempire.State;
import ageofempire.build.Build;
import ageofempire.packet.BlockBreakPacket;
import ageofempire.tasks.AttackCycle;
import ageofempire.tasks.Autostart;
import ageofempire.tasks.ShinyIp;
import ageofempire.tasks.TutoCycle;
import ageofempire.team.Teams;
import de.domedd.developerapi.messagebuilder.ActionbarBuilder;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;


public class PlayerListener implements Listener {

    private Main main;
    public Location spawn;


    public PlayerListener(Main main) {
        this.main = main;
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Location spawn = new Location(Bukkit.getWorlds().get(0), main.getConfig().getInt("spawn.hub.x"), main.getConfig().getInt("spawn.hub.y"), main.getConfig().getInt("spawn.hub.z"));

        Player player = event.getPlayer();
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16);
        player.setFoodLevel(20);
        player.setHealth(20);

        PotionEffect saturation = new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 1, false, false);
        player.addPotionEffect(saturation);

        //setup scoreboar

        ScoreboardSign sbhub = new ScoreboardSign((ageofempire.playerstats.Player) player, "§aAge Of Empire v1.0");
        sbhub.create();
        sbhub.setLine(12, "§6Time : §e40:00");
        sbhub.setLine(11, "§6Pvp : §e05:00");
        sbhub.setLine(10, "§6Objectif : §e15000 pts");
        sbhub.setLine(9, "§r");
        sbhub.setLine(8, ">Equipes : ");
        sbhub.setLine(7, "§9");
        sbhub.setLine(6, "§cRouge : §r" + main.getTeams().get(0).getSize());
        sbhub.setLine(5, "§9Bleu : §r" + main.getTeams().get(1).getSize());
        sbhub.setLine(4, "§eJaune : §r" + main.getTeams().get(2).getSize());
        sbhub.setLine(3, "§aGreen : §r" + main.getTeams().get(3).getSize());
        sbhub.setLine(2, "");
        sbhub.setLine(1, "§eplay.lostaria.net");

        main.lobbyboards.put((ageofempire.playerstats.Player) player, sbhub);

        if(main.getPlayers().isEmpty()){
            ShinyIp standwood = new ShinyIp(main);
            standwood.runTaskTimer(main, 0, 1);
            //command
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "gamerule doDaylightCycle false");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "gamerule doWeatherCycle false");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "time set day");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "weather clear");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kill @e");
        }

        //send tablist
        main.sendTablist((ageofempire.playerstats.Player) player);

        //setbossbar

        main.bossbar.createBar((ageofempire.playerstats.Player) player);

        main.bossbar.createBar((ageofempire.playerstats.Player) player);


        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(spawn);
        ItemStack customcompass = new ItemStack(Material.COMPASS, 1);
        ItemMeta customM = customcompass.getItemMeta();
        customM.setDisplayName("§cChoose a team");
        customM.setLore(Arrays.asList("§9right click to choose a team"));
        customcompass.setItemMeta(customM);
        player.getInventory().setItem(4, customcompass);


        if (!main.getPlayers().contains(player)) {
            main.getPlayers().add((ageofempire.playerstats.Player) player);
        }

        event.setJoinMessage("");

        if (main.isState(State.WAINTING) && main.getPlayers().size() == 1) {

            Autostart start = new Autostart(main);
            start.runTaskTimer(main, 0, 20);
            main.setState(State.STARTING);
        }

        for(Player pls : main.getPlayers()){
            ActionbarBuilder ab = new ActionbarBuilder("§4[Age Of Empire]§r " + player.getName() + "§a joined the game ! (" + main.getPlayers().size() + "/12)")
                .send(player);
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        main.removePlayer((ageofempire.playerstats.Player) player);

        if (main.getPlayers().contains(player)) {
            main.getPlayers().remove(player);
            event.setQuitMessage("");
            for (Entry<ageofempire.playerstats.Player, ScoreboardSign> boards : main.lobbyboards.entrySet()) {
                boards.getValue().setLine(4, "§a(§e" + Bukkit.getOnlinePlayers().size() + "§a/12)");
            }

            for(Player pls : main.getPlayers()){
                ActionbarBuilder ab = new ActionbarBuilder("§4[Age Of Empire]§r " + player.getName() + "§a left the game ! (" + main.getPlayers().size() + "/12)")
                    .send(player);
            }
        }


    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if(block.getType().equals(Material.TNT)){
            Bukkit.getWorlds().get(0).getBlockAt(block.getLocation()).setType(Material.AIR);
            TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        }
    }

    @EventHandler
    @Deprecated
    public void onBreak(BlockBreakEvent event) {

        Material blockbreak = event.getBlock().getType();
        byte data = event.getBlock().getData();
        System.out.println(event.getBlock() + "");
        Location blockloc = event.getBlock().getLocation();
        World world = Bukkit.getWorlds().get(0);
        Block block = event.getBlock();



        if (main.isState(State.PLAYING) || main.isState(State.PVP)) {


            if (blockbreak.equals(Material.LOG) && data == 13) {

                main.replaceBlock(blockloc, 10, block, (ageofempire.playerstats.Player) event.getPlayer());

            }
            else if(blockbreak.equals(Material.DIAMOND_ORE)){

                main.replaceBlock(blockloc, 10, block, (ageofempire.playerstats.Player) event.getPlayer());

            }
            else if(blockbreak.equals(Material.IRON_ORE)){

                main.replaceBlock(blockloc, 10, block, (ageofempire.playerstats.Player) event.getPlayer());
            }
            else if(blockbreak.equals(Material.EMERALD_ORE)){

                main.replaceBlock(blockloc, 10, block, (ageofempire.playerstats.Player) event.getPlayer());


            }
            else if(blockbreak.equals(Material.STONE) && data == 3){

                main.replaceBlock(blockloc, 10, block, (ageofempire.playerstats.Player) event.getPlayer());



            }

            event.setCancelled(true);
            return;

        }
        else{
            event.setCancelled(true);
            return;
        }
    }


    @Deprecated
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack it = event.getItem();

        if(event.getClickedBlock() != null) {

            Material id = event.getClickedBlock().getType();
            if (id != null && id.equals(Material.EMERALD_ORE)) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 3.0f, 1f);
                }
            }
            if(main.isTeamBuild((ageofempire.playerstats.Player) player, event.getClickedBlock()) && action.equals(Action.RIGHT_CLICK_BLOCK)) {
                main.getBuild(event.getClickedBlock()).getInventory((ageofempire.playerstats.Player) player);
            }
        }

        if (it != null && it.getType() == Material.COMPASS && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equalsIgnoreCase("§cChoose a team")) {

            Inventory inv = Bukkit.createInventory(null, 9, "§8Choose a team");

            int i = 1;

            for (Teams team : main.getTeams()) {
                inv.setItem(i, team.getIcon());

                i += 2;
            }

            player.openInventory(inv);

        }

        Block block = event.getClickedBlock();

        if(action.equals(Action.LEFT_CLICK_BLOCK) && action != null && player.getItemInHand() != null && main.isState(State.PVP)){
            if(!main.isTeamBuild((ageofempire.playerstats.Player) player, block) && main.isBuild(block)){
                System.out.println("ezezez");
                if(player.getItemInHand() == null){
                    event.setCancelled(true);
                    return;
                }
                else if(player.getItemInHand().getItemMeta().hasLore()){
                    System.out.println("bite");
                    Build build = main.getBuild(block);

                    AttackCycle isattack = new AttackCycle(main);
                    isattack.setBuild(build);
                    isattack.runTaskTimer(main, 0, 20);


                    int damage = Integer.parseInt(Arrays.asList(player.getItemInHand().getItemMeta().getLore().get(0).split(" ")).get(1));
                    int usure = Integer.parseInt(Arrays.asList(player.getItemInHand().getItemMeta().getLore().get(1).split(" ")).get(1));
                    int deblayer = build.getDeblayer();
                    int health = build.getHealth();
                    if (health - damage > 0) {
                        build.setHealth(health - damage);
                        build.getStand().setCustomName("§c" + build.getName() + " " + "§8[" + main.getProgressBar(build.getHealth(), build.getMaxhealth(), 40, "|", "§c", "§7") + "§8]");

                        BlockBreakPacket blockBreakPacket;

                        if(!build.getPacketbreaklist().containsKey(block)){
                            build.createPacketBreak(block);
                            blockBreakPacket = build.getBlockPacket(block);
                        }
                        else {
                            blockBreakPacket = build.getBlockPacket(block);
                        }

                        blockBreakPacket.addBreak(usure/5);
                        blockBreakPacket.sendPacket();

                    } else {
                        build.setHealth(0);
                        build.getStand().setCustomName("§c" + build.getName() + " " + "§8[" + main.getProgressBar(build.getHealth(), build.getMaxhealth(), 40, "|", "§c", "§7") + "§8]");
                        // Detruire
                    }

                    if(deblayer - usure > 0){
                        build.removeDeblayer(usure);
                    }
                    else{
                        build.setDeblayer(0);
                        if(build.getTeam().getPoints() > 0) {
                            build.getTeam().removePoints(1);
                        }
                    }
                }
            }
        }

        if (block != null && block.getType().equals(Material.DARK_OAK_DOOR)) {
            event.setCancelled(true);
        }

        if (block != null && block.getType().equals(Material.TRAP_DOOR)) {
            event.setCancelled(true);
        }

        if (block != null && block.getType().equals(Material.WOOD_BUTTON)) {
            event.setCancelled(true);
        }


    }


    @Deprecated
    @EventHandler
    public void onCLick(InventoryClickEvent event) {

        Inventory inv = event.getInventory();
        ageofempire.playerstats.Player player = (ageofempire.playerstats.Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        int foodlevel = player.getFoodLevel();

        if(main.bossbar.hasBossBar(player)){
            foodlevel += main.bossbar.getBossBar(player).getProgress() * 6 / 100;
        }

        boolean isbuild = false;
        Teams teams = null;

        if(inv.getName().equalsIgnoreCase("§8Menu du tutoriel")){

            System.out.println("bite");

            switch(current.getItemMeta().getDisplayName()){
                case "§cTuto dévastation":
                    TutoCycle standstone = new TutoCycle(main);
                    standstone.setTutoCycle(player);
                    standstone.runTaskTimer(main, 0, 1);
                    break;
            }

            event.setCancelled(true);
            return;
        }

        if (inv.getName().equalsIgnoreCase("§8Choose a team")) {

            for (Teams team : main.getTeams()) {
                if (team.getIcon().equals(current)) {

                    main.addPlayer(player, team);
                    continue;
                }

                if (team.getPlayers().contains(player)) {
                    team.getPlayers().remove(player);
                }
            }
            event.setCancelled(true);
            return;
        }

        if(main.bossbar.hasBossBar(player)){
            foodlevel += main.bossbar.getBossBar(player).getProgress()*6/100;
        }

        Build builde = null;
        for(Teams team : main.teams){
            if(team.getPlayers().contains(player)){
                teams = team;
                for(Build build : main.buildlist){
                    if(build.getTeam().equals(team)) {
                        if (build.getName().equals(inv.getName())) {
                            builde = build;
                            isbuild = true;
                        }
                    }
                }
            }
        }

        if(!isbuild){
            return;
        }

        if (current == null) return;

        if(!inv.getName().equalsIgnoreCase("§8Choose a team")) {
            int points = (int) (main.bossbar.getBossBar(player).getProgress() * 6);
            boolean isDeblayer = false;
            String names;
            Material name = current.getType();
            String progressbar;

            if (builde.isDeblayer()) {
                progressbar = " §8[§r" + main.getProgressBar(builde.getDeblayer(), 100, 40, "|", "§e", "§7") + "§8]";
            } else {
                progressbar = " §8[§r" + main.getProgressBar(builde.getLvl(), builde.getLvlmax(), 40, "|", "§e", "§7") + "§8]";
            }

            switch (name) {
                case BARRIER:
                case PAINTING:
                    event.setCancelled(true);
                    return;
                case TOTEM:
                    if (current.getType().equals(Material.TOTEM)) {
                        if (builde.getDeblayer() < 100) {
                            if (builde != null) {
                                if (foodlevel + builde.getDeblayer() >= 100) {



                                    if (teams == null) {
                                        return;
                                    }

                                    boolean passeage = false;

                                    int foodplayer = (int) (builde.getLvlmax() - builde.getLvl() - (main.bossbar.getBossBar(player).getProgress() * 6 / 100));
                                    int removepoints = (int) ((main.bossbar.getBossBar(player).getProgress() * 6 / 100) - builde.getLvlmax() + builde.getLvl());
                                    if (foodplayer > 0) {
                                        foodplayer = 0;
                                    }

                                    if (removepoints < 0) {
                                        removepoints = 0;
                                    }

                                    player.setFoodLevel(foodplayer);
                                    passeage = true;


                                    if (passeage) {
                                        main.bossbar.removePointBossBar(player, removepoints);
                                        builde.setDeblayer(100);
                                        player.playSound(player.getLocation(), Sound.ITEM_SHOVEL_FLATTEN, 2.0f, 2.0f);
                                        teams.addPoints(1);
                                        for(Player pls : teams.getPlayers()){
                                            main.title.sendTitle(pls, teams.getTags() + builde.getName(), "a été déblayer", 20);
                                        }
                                    }

                                } else {
                                    player.sendMessage("Vous n'avez pas assez de nourriture");
                                }

                            }
                        } else {
                            if (builde != null) {
                                if (foodlevel + builde.getLvl() >= builde.getLvlmax()) {


                                    if (teams == null) {
                                        return;
                                    }

                                    boolean passeage = false;

                                    int foodplayer = (int) (builde.getLvlmax() - builde.getLvl() - (main.bossbar.getBossBar(player).getProgress() * 6 / 100));
                                    int removepoints = (int) ((main.bossbar.getBossBar(player).getProgress() * 6 / 100) - builde.getLvlmax() + builde.getLvl());
                                    if (foodplayer > 0) {
                                        foodplayer = 0;
                                    }

                                    if (removepoints < 0) {
                                        removepoints = 0;
                                    }

                                    switch (builde.getAge()) {
                                        case 0:

                                            if (Integer.parseInt(builde.getResource1().get(0)) <= teams.resource.getWood() && Integer.parseInt(builde.getResource1().get(1)) <= teams.resource.getStone() && Integer.parseInt(builde.getResource1().get(2)) <= teams.resource.getIron() && Integer.parseInt(builde.getResource1().get(2)) <= teams.resource.getDiamond()) {
                                                player.setFoodLevel(foodplayer);
                                                //builde.loadSchematic();
                                                passeage = true;
                                            } else {
                                                player.sendMessage("Vous n'avez pas assez de ressources");
                                            }
                                            break;
                                        case 1:
                                            if (Integer.parseInt(builde.getResource2().get(0)) <= teams.resource.getWood() && Integer.parseInt(builde.getResource2().get(1)) <= teams.resource.getStone() && Integer.parseInt(builde.getResource2().get(2)) <= teams.resource.getIron() && Integer.parseInt(builde.getResource2().get(2)) <= teams.resource.getDiamond()) {
                                                player.setFoodLevel(foodplayer);
                                                //builde.loadSchematic();
                                                passeage = true;
                                            } else {
                                                player.sendMessage("Vous n'avez pas assez de ressources");
                                            }
                                            break;
                                        case 2:
                                            if (Integer.parseInt(builde.getResource3().get(0)) <= teams.resource.getWood() && Integer.parseInt(builde.getResource3().get(1)) <= teams.resource.getStone() && Integer.parseInt(builde.getResource3().get(2)) <= teams.resource.getIron() && Integer.parseInt(builde.getResource3().get(2)) <= teams.resource.getDiamond()) {
                                                player.setFoodLevel(foodplayer);
                                                //builde.loadSchematic();
                                                passeage = true;
                                            } else {
                                                player.sendMessage("Vous n'avez pas assez de ressources");
                                            }
                                            break;
                                        default:
                                            break;
                                    }

                                    if (passeage) {
                                        main.bossbar.removePointBossBar(player, removepoints);
                                        teams.addPoints(1);

                                        if (!builde.isDeblayer()) {
                                            builde.addAge();
                                            if (builde.getName().equalsIgnoreCase("forum")) {
                                                teams.setAge(teams.getAge() + 1);
                                                Bukkit.broadcastMessage("L'equipe " + teams.getTags() + teams.getName() + "§r vient de passer à l'age §6" + teams.getAge());
                                            }
                                            teams.addPoints(1);
                                            player.closeInventory();
                                        }
                                        builde.setHealth(builde.getMaxhealth());
                                    }

                                } else {
                                    player.sendMessage("Vous n'avez pas assez de nourriture");
                                }

                            }
                        }
                    }
                    inv.clear();

                case BEETROOT_SEEDS:


                    if (current.getType().equals(Material.BEETROOT_SEEDS)) {

                        int foodplayer = (int) (player.getFoodLevel() + (main.bossbar.getBossBar(player).getProgress() * 6 / 100));
                        int removepoints = (int) ((main.bossbar.getBossBar(player).getProgress() * 6 / 100));

                        if (removepoints < 0) {
                            removepoints = 0;
                        }

                        if (builde != null) {
                            if (foodplayer >= current.getAmount()) {
                                if (current.getItemMeta().hasEnchants()) {
                                    builde.addDeblayer(current.getAmount());
                                    main.bossbar.removePointBossBar(player, removepoints);
                                } else {
                                    builde.addLvl(current.getAmount());
                                    main.bossbar.removePointBossBar(player, removepoints);
                                }

                                player.setFoodLevel(player.getFoodLevel() - (current.getAmount() - removepoints));
                            } else {
                                player.sendMessage("Vous n'avez pas assez de nourriture");
                            }
                        }
                    }



                    inv.clear();

                case GOLD_SPADE:
                    inv.clear();
                    if(builde.isDeblayer()) {
                        for (int i = 0; i < 8; i++) {

                            ItemStack beetroot = new ItemStack(Material.BEETROOT_SEEDS, i);
                            ItemStack totem = new ItemStack(Material.TOTEM, i);
                            ItemStack barrer = new ItemStack(Material.BARRIER, i);
                            ItemMeta ibeetroot = beetroot.getItemMeta();
                            List<String> barrerlore = new ArrayList<>();

                            ibeetroot.setDisplayName("+" + i + " " + progressbar + " " + builde.getDeblayer() + "/100");
                            ibeetroot.setLore(Arrays.asList("§fLe batiment est endommagé", "§fet croule sous les gravats", "§fParticipez au déblayage", "§fconsommera votre §6barre de faim", " ", "§7§oCombien de points", "§7§osouhaitez-vous depenser ?"));
                            barrerlore = Arrays.asList("§fLe batiment est endommagé", "§fet croule sous les gravats", "§fParticipez au déblayage", "§fconsommera votre §6barre de faim", " ", "§7§oCombien de points", "§7§osouhaitez-vous depenser ?", " ", "§6§lVous devez manger pour", "§6§leffectuer cette action");

                            ibeetroot.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                            ibeetroot.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                            beetroot.setItemMeta(ibeetroot);
                            totem.setItemMeta(ibeetroot);
                            ibeetroot.setLore(barrerlore);
                            barrer.setItemMeta(ibeetroot);

                            if (builde != null) {
                                if (i > foodlevel) {
                                    inv.setItem(i + 9, barrer);
                                } else if (100 <= builde.getDeblayer() + i) {
                                    inv.setItem(i + 9, totem);
                                } else {
                                    inv.setItem(i + 9, beetroot);
                                }
                            }
                        }

                        event.setCancelled(true);
                        return;
                    }
                case PURPUR_PILLAR:
                    System.out.println("yousk4");

                    if(builde.getAge() == teams.getAge() && !builde.getName().equals("forum")){
                        event.setCancelled(true);
                        return;
                    }

                    inv.clear();
                    for(int i = 0; i < 8; i++) {
                        ItemStack beetroot = new ItemStack(Material.BEETROOT_SEEDS, i);
                        ItemStack totem = new ItemStack(Material.TOTEM, i);
                        ItemStack barrer = new ItemStack(Material.BARRIER, i);
                        ItemMeta ibeetroot = beetroot.getItemMeta();
                        List<String> barrerlore = new ArrayList<>();

                        if(isDeblayer) {
                            ibeetroot.setDisplayName("+" + i + " " + progressbar + " " + builde.getDeblayer() + "/100");
                            ibeetroot.setLore(Arrays.asList("§fLe batiment est endommagé", "§fet croule sous les gravats","§fParticipez au déblayage","§fconsommera votre §6barre de faim"," ","§7§oCombien de points","§7§osouhaitez-vous depenser ?"));
                            barrerlore = Arrays.asList("§fLe batiment est endommagé", "§fet croule sous les gravats","§fParticipez au déblayage","§fconsommera votre §6barre de faim"," ","§7§oCombien de points","§7§osouhaitez-vous depenser ?"," ","§6§lVous devez manger pour","§6§leffectuer cette action");
                        }
                        else{
                            ibeetroot.setDisplayName("+" + i + " " + progressbar + " " + builde.getLvl() + "/" + builde.getLvlmax());
                            ibeetroot.setLore(Arrays.asList("§fFaire évoluer le batiment","§fconsommera votre §6barre de faim"," ","§7§oCombien de points","§7§osouhaitez-vous depenser ?"));
                            barrerlore = Arrays.asList("§fFaire évoluer le batiment","§fconsommera votre §6barre de faim"," ","§7§oCombien de points","§7§osouhaitez-vous depenser ?"," ","§6§lVous devez manger pour","§6§leffectuer cette action");
                        }

                        beetroot.setItemMeta(ibeetroot);
                        totem.setItemMeta(ibeetroot);
                        ibeetroot.setLore(barrerlore);
                        barrer.setItemMeta(ibeetroot);

                        if(builde != null){
                            if(i > foodlevel){
                                inv.setItem(i+9, barrer);
                            }
                            else if(builde.getLvlmax() <= builde.getLvl() + i){
                                inv.setItem(i+9, totem);
                            }
                            else{
                                inv.setItem(i+9, beetroot);
                            }
                        }
                    }
                    event.setCancelled(true);
                    return;

                default:
                    event.setCancelled(true);
                    break;
            }

            if(current.getItemMeta() == null){
                return;
            }

            if(!current.getItemMeta().hasLore()){
                return;
            }

            event.setCancelled(true);

            String damage = null;
            String usure = null;

            if(builde.getName().equals("forge")){
                damage = Arrays.asList(current.getItemMeta().getLore().get(3).split(" ")).get(1);
                usure = Arrays.asList(current.getItemMeta().getLore().get(4).split(" ")).get(1);
            }

            String iM = Arrays.asList(current.getItemMeta().getLore().get(1).split(" ")).get(2);

            if(iM.equals("Emeraude")){
                for(Teams team : main.teams){
                    if(team.getPlayers().contains(player)){
                        if(team.getResource().getEmerald() >= Integer.parseInt(Arrays.asList(current.getItemMeta().getLore().get(1).split(" ")).get(1))){
                            ItemStack give = current;

                            if(give.getType().equals(Material.SPLASH_POTION)){
                                ItemMeta meta = current.getItemMeta();
                                List<String> array = new ArrayList<>();
                                String pot1 = current.getItemMeta().getLore().get(0);
                                array.add(pot1);
                                if(pot1.equals("§cBlindness V (0:10)")){
                                    array.add(current.getItemMeta().getLore().get(1));
                                }
                                meta.setLore(array);
                            }

                            if(damage != null && usure != null){
                                ItemMeta meta = current.getItemMeta();
                                meta.setLore(Arrays.asList("§f+ " + damage + " de damage" ,"§f+ " + usure + " d'usure"));
                                give.setItemMeta(meta);
                            }

                            int i = 0;

                            for(ItemStack item : player.getInventory().getContents()){
                                if(item == null){
                                    player.getInventory().setItem(i, current);
                                    continue;
                                }
                                i++;
                            }
                            team.getResource().removeEmerald(Integer.parseInt(Arrays.asList(current.getItemMeta().getLore().get(1).split(" ")).get(1)));
                        }
                        else{
                            player.sendMessage("§4Vous n'avez pas assez d'emeraudes");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {

        Player player = event.getPlayer();
        Item itemdrop = event.getItemDrop();

        if (main.isState(State.WAINTING) || main.isState(State.STARTING) || main.isState(State.FINISH)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String msg = event.getMessage();
        Player player = event.getPlayer();
        Teams teams = null;


        for(Teams team : main.teams){
            if(team.getPlayers().contains(player)){
                teams = team;
            }
        }

        if(!msg.startsWith("!") && teams != null){
            for(Player pls : main.getPlayers()){
                for(Teams team : main.teams){
                    if(team.getPlayers().contains(pls)){
                        if(team.equals(teams)){
                            pls.sendMessage("(Team) " + teams.getTags() + player.getName() + "§r : " + msg);
                        }
                    }
                }
            }
        }
        else if(teams != null){
            Bukkit.broadcastMessage("(All) " + teams.getTags() + player.getName() + "§r :" + msg.replaceFirst("!"," "));
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        event.setCancelled(true);
    }

    @EventHandler
    public void onFeed(PlayerItemConsumeEvent event){
        ageofempire.playerstats.Player player = (ageofempire.playerstats.Player) event.getPlayer();
        ItemStack item = event.getItem();

        if(main.bossbar.hasBossBar(player)){
            main.bossbar.addPointBossBar(player, item);
        }
        else{
            main.bossbar.createBar(player);
        }
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        event.blockList().clear();
        event.setCancelled(true);
    }

    @Deprecated
    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if(event.getEntity().getType() == EntityType.HORSE) {
            Horse horse = (Horse) event.getEntity();
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
            horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(0.5);
            horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            horse.setHealth(horse.getMaxHealth());
        }
        else if(event.getEntity().getType() == EntityType.MULE) {
            Mule horse = (Mule) event.getEntity();
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.25);
            horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(0.5);
            horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(25);
            horse.setHealth(horse.getMaxHealth());
        }
        else if(event.getEntity().getType() == EntityType.DONKEY) {
            Donkey horse = (Donkey) event.getEntity();
            horse.setTamed(true);
            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
            horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.2);
            horse.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(0.5);
            horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            horse.setHealth(horse.getMaxHealth());
        }
    }

    @EventHandler
    public void onPotion(PotionSplashEvent event){

        PotionEffect blindness = new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, Integer.MAX_VALUE, false, false);
        PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 20 * 20, 2, false, false);
        PotionEffect levitation = new PotionEffect(PotionEffectType.LEVITATION , 3 * 20, 1, false, false);

        for(LivingEntity entity : event.getAffectedEntities()) {
            for (String effect : event.getPotion().getItem().getItemMeta().getLore()) {
                switch(effect){
                    case "§cSlowness III (0:20)" :
                        entity.addPotionEffect(slow);
                        break;
                    case "§cBlindness V (0:10)" :
                        entity.addPotionEffect(blindness);
                        break;
                    case "§cLevitation II (0:03)":
                        entity.addPotionEffect(levitation);
                        break;

                }

            }
        }
    }

}
