package fr.senseijuba.survivor.utils.config;

import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Prefix;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigEntries {

    public static String INFO_PREFIX, ERROR_PREFIX, CHAT_PREFIX;

    public static String E_PERM, E_404, E_KICK, E_NOTINARENA;

    public static String PING_RESULT;

    public static String STARTIN;

    public static String DEATH_MSG, DEATH_MSG_ALL;

    public static String INVENTORY_VOTEMAP, INVENTORY_VOTEMAP_PANE, INVENTORY_VOTEMAP_LORE;

    public static String ATOUT_BARRICADE, ATOUT_USE, ATOUT_USE_ALL;

    public static String REPORT_NOTIFICATION, REPORT_COOLDOWN_MESSAGE, REPORT_SUCCESSFUL;
    public static long REPORT_COOLDOWN;

    public static String STATS_OVERVIEW;

    public static String SCOREBOARD_LOBBY;
    public static String SCOREBOARD_MATCH;
    public static String SCOREBOARD_PARTY;
    public static String SCOREBOARD_QUEUE;

    public static void formatAndSend(CommandSender sendTo, String in, Object... values) {
        sendTo.sendMessage(String.format(in, values));
    }

    public static String format(String in, Object... values) {
        return String.format(in, values);
    }

    private static String c(String in) { return in.replace('&', '§');
    }

    public static void init(FileConfiguration config) {
        INFO_PREFIX = c(config.getString("prefix.info"));
        ERROR_PREFIX = c(config.getString("prefix.error"));
        CHAT_PREFIX = c(config.getString("prefix.chat"));

        Prefix.INFO = INFO_PREFIX;
        Prefix.ERROR = ERROR_PREFIX;

        E_PERM = ERROR_PREFIX + c(config.getString("error.permission"));
        E_404 = ERROR_PREFIX + c(config.getString("error.notfound"));
        E_KICK = ERROR_PREFIX + c(config.getString("error.arena.kick"));
        E_NOTINARENA = ERROR_PREFIX + c(config.getString("error.arena.playernotfound"));

        PING_RESULT = INFO_PREFIX + c(config.getString("ping.result"));

        STARTIN = INFO_PREFIX + c(config.getString("startin"));

        DEATH_MSG = c(config.getString("death.msg"));
        DEATH_MSG_ALL = INFO_PREFIX + c(config.getString("death.msg.all"));

        INVENTORY_VOTEMAP = c(config.getString("inventory.votemap"));
        INVENTORY_VOTEMAP_LORE = c(config.getString("inventory.votemap.lore"));
        INVENTORY_VOTEMAP_PANE = c(config.getString("inventory.votemap.pane"));

        ATOUT_BARRICADE = c(config.getString("atout.barricade.name"));
        ATOUT_USE = c(config.getString("atout.use"));
        ATOUT_USE_ALL = INFO_PREFIX + c(config.getString("atout.use.all"));

        REPORT_NOTIFICATION = INFO_PREFIX + c(config.getString("report.notification"));
        REPORT_COOLDOWN_MESSAGE = INFO_PREFIX + c(config.getString("report.cooldown.error"));
        REPORT_SUCCESSFUL = INFO_PREFIX + c(config.getString("report.successful"));

        REPORT_COOLDOWN = config.getInt("report.cooldown.amount");

        STATS_OVERVIEW = INFO_PREFIX + c(config.getString("stats.overview"));

        SCOREBOARD_LOBBY = c(config.getString("scoreboard.lobby"));
        SCOREBOARD_MATCH = c(config.getString("scoreboard.match"));
        SCOREBOARD_PARTY = c(config.getString("scoreboard.party"));
        SCOREBOARD_QUEUE = c(config.getString("scoreboard.queue"));


            FileConfiguration file = null;

            File check = new File(Survivor.getInstance().getDataFolder(), "config.yml");

            if(!Survivor.getInstance().getDataFolder().exists())
                Survivor.getInstance().getDataFolder().mkdir();

            if(!check.exists())
            {
                try
                {
                    check.createNewFile();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                file = YamlConfiguration.loadConfiguration(check);
            }catch(NullPointerException ex)
            {
                ex.printStackTrace();
            }

            if(file.contains("tips")) {
                for (String tip : file.getStringList("tips")) {
                    Survivor.getInstance().tips.add(tip);
                }
            }

            for(String worldName : file.getKeys(false)) {
                String name = worldName;

                try {
                    if (Bukkit.getWorld(name) == null && !name.equals("tips"))
                        Bukkit.createWorld(new WorldCreator(name));

                    if (file.getConfigurationSection(name).contains("enable") && file.getBoolean(name + ".enable")) {
                        Location spawn;
                        int place;
                        Material mapIcon;

                        if (file.getString(name + ".spawnpoint") != null) {
                            spawn = Utils.stringToLoc(file.getString(name + ".spawnpoint"));
                        } else {
                            spawn = null;
                        }

                        if (file.getInt(name + ".place") != 0) {
                            place = file.getInt(name + ".place");
                        } else {
                            place = 0;
                        }

                        if (Material.getMaterial(file.getString(name + ".mapIcon")) != null) {
                            mapIcon = Material.getMaterial(file.getString(name + ".mapIcon"));
                        } else {
                            mapIcon = Material.INK_SACK;
                        }

                        Map map = new Map(name, "§f", spawn, Bukkit.getWorld(name), place, mapIcon);

                        for (String zone : file.getConfigurationSection(name + ".zones").getKeys(false)) {

                            String zonename = zone;
                            zone = name + "." + zone;
                            int cost = 0;
                            Cuboid door = null;
                            Location sign = null;


                            if (file.getInt(zone + ".cost") != 0) {
                                cost = file.getInt(zone + ".cost");
                            }

                            if (Utils.stringToLoc(file.getString(zone + ".sign")) != null) {
                                sign = Utils.stringToLoc(file.getString(zone + ".sign"));
                            }

                            if (Utils.stringToLoc(file.getString(zone + ".door.Z1")) != null && Utils.stringToLoc(file.getString(zone + ".door.Z2")) != null) {
                                door = new Cuboid(Utils.stringToLoc(file.getString(zone + ".door.Z1")), Utils.stringToLoc(file.getString(zone + ".door.Z2")));
                            }

                            Zone zonefini = new Zone(zonename, cost, sign, door, false);

                            for (String mobzone : file.getConfigurationSection(zone + ".mobzone").getKeys(false)) {
                                if (Utils.stringToLoc(mobzone) != null) {
                                    zonefini.addSpawnMobZone(Utils.stringToLoc(mobzone));
                                }
                            }

                            for (String barricade : file.getConfigurationSection(zone + ".barricades").getKeys(false)) {
                                if (Utils.stringToLoc(file.getString(barricade + ".Z1")) != null && Utils.stringToLoc(file.getString(barricade + ".Z2")) != null) {
                                    zonefini.addBarricades(new Cuboid(Utils.stringToLoc(file.getString(barricade + ".Z1")), Utils.stringToLoc(file.getString(barricade + ".Z2"))));
                                }
                            }
                            map.addZone(zonefini);
                        }


                        Survivor.getInstance().maps.add(map);
                    }
                } catch (Exception e) {
                    Survivor.getInstance().maps.remove(null);
                }

            }

    }
}
