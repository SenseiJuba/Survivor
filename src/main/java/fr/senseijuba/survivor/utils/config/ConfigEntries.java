package fr.senseijuba.survivor.utils.config;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.map.Map;
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

public class ConfigEntries {

    public static String INFO_PREFIX, ERROR_PREFIX, CHAT_PREFIX;

    public static String STARTIN;

    public static String DEATH_MSG, DEATH_MSG_ALL;

    public static String INVENTORY_VOTEMAP, INVENTORY_VOTEMAP_PANE, INVENTORY_VOTEMAP_LORE;

    public static String ATOUT_BARRICADE, ATOUT_USE, ATOUT_USE_ALL;

    public static void formatAndSend(CommandSender sendTo, String in, Object... values) {
        sendTo.sendMessage(String.format(in, values));
    }

    public static String format(String in, Object... values) {
        return String.format(in, values);
    }

    private static String c(String in) { return in.replace('&', '§');
    }

    public static void init(FileConfiguration config) {
        INFO_PREFIX = c("&a&lSurvivor §8»§7 ");
        ERROR_PREFIX = c("&c&lSurvivor §8»§7 ");
        CHAT_PREFIX = c("&c[&lSurvivor&f&c]");

        Prefix.INFO = INFO_PREFIX;
        Prefix.ERROR = ERROR_PREFIX;

        STARTIN = INFO_PREFIX + c("La partie va commencé dans : %1$s second(s)");

        DEATH_MSG = c("Vous êtes mort par un %2$s");
        DEATH_MSG_ALL = INFO_PREFIX + c("%1$s est mort par un %2$s, allez le réssucitez");

        INVENTORY_VOTEMAP = c("Votez pour la map");
        INVENTORY_VOTEMAP_LORE = c("Votez pour la map %1$s");
        INVENTORY_VOTEMAP_PANE = c("Clicker sur l'item pour/Voter pour la map");

        ATOUT_BARRICADE = c("Barricade");
        ATOUT_USE = c("");
        ATOUT_USE_ALL = INFO_PREFIX + c("%1$s a utilisé l'atout barricade");


        FileConfiguration file = config;

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
                    System.out.println("check check check");
                }

            }

    }
}
