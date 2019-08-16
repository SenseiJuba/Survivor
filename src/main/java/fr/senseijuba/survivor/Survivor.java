package fr.senseijuba.survivor;

import fr.senseijuba.survivor.atouts.Atout;
import fr.senseijuba.survivor.atouts.AtoutListener;
import fr.senseijuba.survivor.commands.GetWeapon;
import fr.senseijuba.survivor.commands.SurvivorCommand;
import fr.senseijuba.survivor.cycle.AutoStart;
import fr.senseijuba.survivor.cycle.GameCycle;
import fr.senseijuba.survivor.database.Mariadb;
import fr.senseijuba.survivor.database.player.PlayerData;
import fr.senseijuba.survivor.database.player.PlayerDataManager;
import fr.senseijuba.survivor.managers.*;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.map.VoteMapManager;
import fr.senseijuba.survivor.mobs.MobManager;
import fr.senseijuba.survivor.utils.*;
import fr.senseijuba.survivor.utils.config.ConfigEntries;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import fr.senseijuba.survivor.weapons.WeaponManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Survivor extends JavaPlugin {

    private static Survivor instance;
    @Getter private ListenerClass l;
    private AtoutListener atoutListener;
    private SignManager signManager;
    public int vague = 0;
    public Location bossSpawn;
    public Location lobbySpawn;

    @Getter @Setter
    Mariadb mariadb;
    @Getter @Setter
    PlayerDataManager dataManager;
    @Getter @Setter
    HashMap<Player, PlayerData> dataPlayers = new HashMap<>();

    @Getter
    HashMap<UUID, List<Atout>> playerAtout = new HashMap<>();
    @Getter
    HashMap<UUID, List<AbstractWeapon>> playerWeapon = new HashMap<>();


    private HashMap<String, ItemStack> specialItems;
    private HashMap<Map, Integer> vote;
    private HashMap<Player, Map> playervote;
    private int voteAleatoire = 0;
    private List<Player> hasVoted;
    public List<Map> maps;
    public List<String> tips;
    @Getter @Setter public GameState gameState;
    private Map currentMap;
    private WeaponManager weaponManager;
    private PlayerManager playerManager;
    private MobManager mobManager;
    private BarricadeManager barricadeManager;
    public GameCycle gameCycle;
    public AutoStart autoStart;
    public int timer = 60;

    @Override
    public void onEnable() {

        ConfigEntries.init(getConfig());
        instance = this;
        registerNMSClasses();
        maps = new ArrayList<Map>();
        tips = new ArrayList<String>();

        atoutListener = new AtoutListener();
        signManager = new SignManager(instance);

        try {
            mariadb = new Mariadb();
            mariadb.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dataManager = new PlayerDataManager();
        gameCycle = new GameCycle(instance);
        autoStart = new AutoStart(instance);
        this.autoStart.runTaskTimer(this, 0, 1);

        weaponManager = new WeaponManager();
        playerManager = new PlayerManager();
        mobManager = new MobManager();
        barricadeManager = new BarricadeManager(instance);
        gameState = GameState.SPAWN;

        for(int i=0;i<5;i++){
            vote.put(maps.get(new Random().nextInt(maps.size()-1)), 0);
        }

        getServer().getPluginManager().registerEvents(new BarricadeManager(instance), instance);
        getServer().getPluginManager().registerEvents(new MobManager(), instance);
        getServer().getPluginManager().registerEvents(new VoteMapManager(instance), instance);



        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                SurvivorCommand survivorcommand = new SurvivorCommand();

                try {
                    l = new ListenerClass();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                getServer().getPluginManager().registerEvents(l, instance);
                getCommand("getWeapon").setExecutor(new GetWeapon());
                getCommand("survivor").setExecutor(survivorcommand);
                getCommand("survivor").setTabCompleter(survivorcommand);

                for(Player p : Bukkit.getOnlinePlayers())
                {
                    p.setNoDamageTicks(20);

                }
            }
        }.runTaskLater(this, 10);

        new BukkitRunnable()
        {
            @Override
            public void run()//SAVES
            {
                ConfigEntries.init(getConfig());
            }
        }.runTaskTimer(this, 1200, 1200);
    }

    public static void copyWorld(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        target.mkdirs();
                    String[] files = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void registerNMSClasses()
    {
        try
        {
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutPlayerListHeaderFooter");
            NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent");
            NMSUtils.register("net.minecraft.server._version_.IChatBaseComponent$ChatSerializer");
            NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutChat");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
            NMSUtils.register("net.minecraft.server._version_.PlayerConnection");
            NMSUtils.register("org.bukkit.craftbukkit._version_.entity.CraftPlayer");
            NMSUtils.register("net.minecraft.server._version_.EntityPlayer");
            NMSUtils.register("net.minecraft.server._version_.EntityHuman");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle");
            NMSUtils.register("net.minecraft.server._version_.PacketPlayOutTitle$EnumTitleAction");
            NMSUtils.register("net.minecraft.server._version_.Packet");
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public static Survivor getInstance(){ return instance;}

    //Map
    public void addMap(Map m)
    {
        maps.add(m);

        FileConfiguration file = getConfig();

        if(!file.contains(m.getName()))
            file.createSection(m.getName()).set("enable", true);

        else
            file.set(m.getName()+".enable", true);

        saveTheConfig(file);
        saveConfig();
    }

    public void rmMap(Map m)
    {
        maps.remove(m);

        FileConfiguration file = getConfig();

        file.set(m.getName(), null);

        saveTheConfig(file);
        saveConfig();
    }

    public void disableMap(Map m)
    {
        maps.remove(m);

        FileConfiguration file = getConfig();

        file.set(m.getName()+".enable", false);

        saveTheConfig(file);
        saveConfig();
    }

    //Tips
    public void saveTips()
    {
        FileConfiguration f = getConfig();

        f.set("tips", tips);

        saveTheConfig(f);
    }

    //Vague
    public int getVague(){ return vague; }

    public void setVague(int vague){ this.vague = vague; }

    //bossSpawn
    public Location getBossSpawn(){ return bossSpawn; }

    public void setBossSpawn(Location loc){ this.bossSpawn = loc; }

    //lobbySpawn
    public Location getLobbySpawn(){ return lobbySpawn; }

    public void setLobbySpawn(Location loc){ this.lobbySpawn = loc; }


    //config

    public void saveTheConfig(FileConfiguration f)
    {
        try
        {
            f.save("config.yml");
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        saveConfig();
    }

    public void saveTheConfig(ConfigurationSection f, World w)
    {
        FileConfiguration file = getConfig();

        file.set(w.getName(), f);

        try
        {
            file.save("config.yml");
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        saveConfig();
    }

    public ConfigurationSection getConfig(World w)
    {
        if(getConfig().getConfigurationSection(w.getName()) != null)
            return getConfig().getConfigurationSection(w.getName());

        else
            return getConfig().createSection(w.getName());
    }

    public void onDisable() {

        Utils.restoreWorld(currentMap.getW());

        try {
            mariadb.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getTips()
    {
        return tips;
    }

    public WeaponManager getWeaponManager()
    {
        return weaponManager;
    }

    public List<Map> getMaps(){ return maps; }

    public Map worldtoMap(World world){
        for(Map map : maps){
            if(map.getW().equals(world)){
                return map;
            }
        }
        return null;
    }

    public Map emptyMap(World world, String name){
        return new Map(name, "§e", null, world, 0, null);
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
    }

    public HashMap<String, ItemStack> getSpecialItem() {
        return specialItems;
    }

    public void addSpecialItem(String name, ItemStack item) {
        specialItems.put(name, item);
    }

    public HashMap<Map, Integer> getVote() {
        return vote;
    }

    public HashMap<Player, Map> getPlayervote() {
        return playervote;
    }

    public List<Player> getHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(List<Player> hasVoted) {
        this.hasVoted = hasVoted;
    }

    public Map mapByName(String name){
        for(Map map : maps){
            if(map.getName().equalsIgnoreCase(name)){
                return map;
            }
        }
        return null;
    }

    public void gameOver() {

        gameState = GameState.FINISH;

        for (Entity en : currentMap.getW().getEntities()) {
            if (!(en instanceof Player)) {
                en.remove();
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Title.sendTitle(player, 0, 60, 10, "§cGame Over");
            Utils.playSound(player, player.getLocation(), Sound.ENDERMAN_DEATH, 3);
            player.sendMessage("§f┼──────§b──────§3────────────§b──────§f──────┼"
                    + "\n"
                    + "   §7» §l§3Survivor : Partie terminée\n"
                    + "\n"
                    + "   §7■ §fRevenez jouer quand vous le souhaitez \n"
                    + "   §7■ §fLe serveur va se redémarrer dans quelque instant\n"
                    + "   §7■ §fet vous allez être téléporté au spawn\n"
                    + "\n§f┼──────§b──────§3────────────§b──────§f──────┼");

            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().clear();
            player.setHealth(20);
            playerAtout.remove(player.getUniqueId());
            player.getInventory().setItem(8, new ItemBuilder(Material.WATCH)
                    .name("§6Revenir au lobby")
                    .lore("§fCLicker droit pour revenir au menu")
                    .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
                    .addflag(ItemFlag.HIDE_ENCHANTS)
                    .build());
            player.teleport(currentMap.getSpawnpoint());
            player.setAllowFlight(true);
            player.setFlying(true);

            dataPlayers.get(player).setDeaths(dataPlayers.get(player).getDeaths() + getPlayerManager().getDeaths().get(player));
            dataPlayers.get(player).setKills(dataPlayers.get(player).getKills() + getPlayerManager().getKills().get(player));
            dataPlayers.get(player).setMaxwaves(dataPlayers.get(player).getMaxwaves() > vague ? dataPlayers.get(player).getMaxwaves() : vague);
            dataPlayers.get(player).setGameplayed(dataPlayers.get(player).getGameplayed() + 1);
            dataPlayers.get(player).setXp(dataPlayers.get(player).getXp() + vague * 10);

            try {
                mariadb.updatePlayerData(player);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Player p = player;
            ScoreboardSign stats = new ScoreboardSign(p, "§eSURVIVOR");

            PlayerData data = this.dataPlayers.get(p);

            stats.create();
            stats.setLine(13, "§7");
            stats.setLine(12, "§6§nStats de la partie:");
            stats.setLine(11, "§eVague max: " + vague);
            stats.setLine(10, "§eMorts: " + getPlayerManager().getDeaths().get(player));
            stats.setLine(9, "§eKills: " + getPlayerManager().getKills().get(player));
            stats.setLine(8, "");
            stats.setLine(7, "§6§nStats Totales:");
            stats.setLine(6, "Parties jouées:" + data.getGameplayed());
            stats.setLine(5, "Vague max: " + data.getMaxwaves());
            stats.setLine(4, "Total kills: " + data.getKills());
            stats.setLine(3, "Total morts: " + data.getDeaths());
            stats.setLine(2, "§8");
            stats.setLine(1, "§6play.hellaria.fr");

            new BukkitRunnable() {

                ScoreboardSign sc = stats;
                int timer = 0;
                String string;

                @Override
                public void run() {
                    switch (timer) {
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
                        case 20 * 9:
                            timer = 0;
                            break;
                    }

                    stats.setLine(1, string);
                    timer++;
                }
            }.runTaskLater(this, 1);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                //TODO tp player on spawn
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer("Redemmarage");
                }
                Bukkit.getServer().reload();
            }
        }.runTaskLater(this, 20 * 20);
    }

    public int getVoteAleatoire() {
        return voteAleatoire;
    }

    public void setVoteAleatoire(int voteAleatoire) {
        this.voteAleatoire = voteAleatoire;
    }

    public void addVoteAleatoire(int i){
        voteAleatoire += i;
    }

    public void removeVoteAleatoire(int i){
        voteAleatoire -= i;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }
}
