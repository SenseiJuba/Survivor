package fr.senseijuba.survivor;

import fr.senseijuba.survivor.commands.GetWeapon;
import fr.senseijuba.survivor.commands.SurvivorCommand;
import fr.senseijuba.survivor.cycle.AutoStart;
import fr.senseijuba.survivor.cycle.GameCycle;
import fr.senseijuba.survivor.database.Mariadb;
import fr.senseijuba.survivor.database.player.PlayerData;
import fr.senseijuba.survivor.database.player.PlayerDataManager;
import fr.senseijuba.survivor.managers.BarricadeManager;
import fr.senseijuba.survivor.managers.GameManager;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.managers.PlayerManager;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.map.VoteMapManager;
import fr.senseijuba.survivor.mobs.MobManager;
import fr.senseijuba.survivor.utils.NMSUtils;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.config.ConfigEntries;
import fr.senseijuba.survivor.weapons.WeaponManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Survivor extends JavaPlugin {

    private static Survivor instance;
    private ListenerClass l;
    public int vague = 0;
    public Location bossSpawn;
    public Location lobbySpawn;

    @Getter @Setter
    Mariadb mariadb;
    @Getter @Setter
    PlayerDataManager dataManager;
    @Getter @Setter
    HashMap<Player, PlayerData> dataPlayers = new HashMap<>();

    private HashMap<World, GameManager> gameManagers;
    private HashMap<String, ItemStack> atouts;
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
        gameManagers = new HashMap<World, GameManager>();
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
                if(!SurvivorCommand.configDebug)
                    for(World w : gameManagers.keySet())
                        gameManagers.get(w).saveInConfig();

                else
                    ConfigEntries.init(getConfig());
            }
        }.runTaskTimer(this, 1200, 1200);
    }

    public void onDisable(){
        try {
            mariadb.disconnect();
        } catch (SQLException e) {
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

    public static void copyWorld(File source, File target)
    {
        try
        {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName()))
            {
                if(source.isDirectory())
                {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for(String file : files)
                    {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                }
                else
                {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getTips()
    {
        return tips;
    }

    public GameManager reinitializeGameManager(GameManager gm)
    {
        World w = gm.getWorld();

        gm.delete();
        gm = new GameManager(w, gt);

        gameManagers.replace(w, gm);

        return gm;
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

    public HashMap<String, ItemStack> getAtouts() {
        return atouts;
    }

    public void addAtout(String name, ItemStack item) {
        atouts.put(name, item);
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
