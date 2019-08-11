package fr.senseijuba.survivor.map;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Map {

    private String name;
    private String prefix;
    private List<UUID> players;
    private Location spawnpoint;
    private Zone startZone;
    private List<Zone> zones;
    private World w;
    private int kills;
    private ItemStack mapIcon;
    private int place = 0;

    public Map(String name, String prefix, Location spawnpoint, World w, int place, Material mapIcon){
        construct(name, prefix, spawnpoint, w, place, mapIcon);
    }

    public void construct(String name, String prefix, Location spawnpoint, World w, int place, Material mapIcon){
        this.name = name;
        this.prefix = prefix;
        this.spawnpoint = spawnpoint;
        this.w = w;
        this.place = place;

        buildMapIcon(mapIcon);
    }

    private ItemStack buildMapIcon(Material mat)
    {
        ItemStack item;

        if(mat != null)
            item = new ItemStack(mat);
        else
            item = new ItemStack(Material.INK_SACK, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Choisir la Map "+getPrefix()+getName().toUpperCase());
        item.setItemMeta(itemMeta);

        mapIcon = item;

        return item;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<Player> getPlayersOnline()
    {
        List<Player> players = new ArrayList<>();

        for(UUID uuid : this.players)
            if(w.getPlayers().contains(Bukkit.getPlayer(uuid)))
                players.add(Bukkit.getPlayer(uuid));

        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }

    public Location getSpawnpoint() {
        return spawnpoint;
    }

    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    public Zone getStartZone() {
        return startZone;
    }

    public void setStartZone(Zone startZone) {
        this.startZone = startZone;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public World getW() {
        return w;
    }

    public void setW(World w) {
        this.w = w;
    }

    public int getKills() {
        return kills;
    }

    public void addKill(int kills)
    {
        this.kills += kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public ItemStack getMapIcon() {
        return mapIcon;
    }

    public void setMapIcon(Material mapIcon) {
        buildMapIcon(mapIcon);
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public void addZone(Zone zone){
        zones.add(zone);
    }

    public void createZone(String name){

        zones.add(new Zone(name));

    }

    public void saveInConfig(){

        ConfigurationSection f = Survivor.getInstance().getConfig(w);

        if(startZone != null){
            startZone.saveInConfig(f, "startzone");
        }

        if(zones != null){
            for(Zone z : zones){
                z.saveInConfig(f, "zones");
            }
        }

        if(spawnpoint != null){
            f.set("spawnpoint", Utils.locDirToString(spawnpoint));
        }

        if(place != 0){
            f.set("place", place);
        }

        if(mapIcon != null){
            f.set("mapIcon", mapIcon.toString());
        }

        Survivor.getInstance().saveTheConfig(f, w);
    }

    public Zone getZoneByName(String name){
        for(Zone zone : zones){
            if(zone.getName().equalsIgnoreCase(name)){
                return zone;
            }
        }
        return null;
    }

    public Zone getZoneModifyingBarricade(Player p){
        for(Zone zone : zones){
            if(zone.isModifyingBarricades(p)){
                return zone;
            }
        }
        return null;
    }

    public Zone getZoneDestructBarricade(Player p){
        for(Zone zone : zones){
            if(zone.isDestructBarricades(p)){
                return zone;
            }
        }

        return null;
    }

    public Zone getZoneModifyingDoor(Player p){
        for(Zone zone : zones){
            if(zone.isModifyingDoor(p)){
                return zone;
            }
        }
        return null;
    }

    public Zone getZoneModifyingSign(Player p){
        for(Zone zone : zones){
            if(zone.isModifyingSign(p)){
                return zone;
            }
        }

        return null;
    }

    public Zone getZoneDestructMobZone(Player p){
        for(Zone zone : zones){
            if(zone.isDestructMobZones(p)){
                return zone;
            }
        }

        return null;
    }

    public Cuboid getNearBarricade(Player p){
        for(Zone zone : getZones()){
            if(zone.isActive){
                return zone.getNearBarricade(p);
            }
        }
        return null;
    }

    public Cuboid getNearBarricade(Entity p){
        for(Zone zone : getZones()){
            if(zone.isActive){
                return zone.getNearBarricade(p);
            }
        }
        return null;
    }
}
