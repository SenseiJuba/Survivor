package fr.senseijuba.survivor.map;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Zone {

    private String name;
    private List<Location> spawnMobZones;
    private List<Cuboid> barricades;
    private Location buyingSign;
    private Cuboid door;
    private int cost = 0;
    public boolean isStartZone;
    public boolean isActive;

    private Player modifySign;

    private Player destructMobZones;

    private Player modifyDoor;
    private Player modifyDoor2;
    private Location modifeDoor1;
    private Location modifeDoor2;

    private Player modifyBarricade;
    private Player modifyBarricade2;
    private Location modifeBarricade1;
    private Location modifeBarricade2;
    private Player destructBarricades;

    public Zone(String name){
        construct(name, 0, null, null, false);
    }

    public Zone(String name, int cost, Location buyingSign, Cuboid door, boolean isStartZone){
        construct(name, cost, buyingSign, door, isStartZone);
    }

    public void construct(String name, int cost, Location buyingSign, Cuboid door, boolean isStartZone){
        this.name = name;
        this.cost = cost;
        this.buyingSign = buyingSign;
        this.door = door;
        this.isStartZone = isStartZone;
        if(isStartZone){
            isActive = true;
        }
        else{
            isActive = false;
        }
    }

    public void saveInConfig(ConfigurationSection f, String n){


            if(!isStartZone){
                f.set(n + "." + name + ".cost", cost);

                if(buyingSign != null) {
                    f.set(n + "." + name + ".sign", Utils.locToString(buyingSign));
                }

                if(door != null){
                    f.set(n+"."+name+".door.Z1", Utils.locToString(door.getLoc1()));
                    f.set(n+"."+name+".door.Z2", Utils.locToString(door.getLoc2()));
                }
            }

            if(spawnMobZones != null) {

                int i = 0;

                for (Location loc : spawnMobZones) {

                    i++;
                    f.set(n+"."+name+".mobzone."+i, Utils.locToString(loc));
                }
            }

            if(barricades != null) {

                int i = 0;

                for (Cuboid cube : barricades) {

                    i++;
                    f.set(n+"."+name+".barricades."+i+".Z1", Utils.locToString(cube.getLoc1()));
                    f.set(n+"."+name+".barricades."+i+".Z2", Utils.locToString(cube.getLoc2()));
                }
            }

    }

    public void setCost(int cost){ this.cost = cost; }

    public int getCost(){ return cost; }

    public void setBuyingSign(Location loc){ this.buyingSign = loc; }

    public Location getBuyingSign(){ return buyingSign; }

    public List<Location> getSpawnMobZones() {
        return spawnMobZones;
    }

    public void addSpawnMobZone(Location loc){
        spawnMobZones.add(loc);
    }

    public List<Cuboid> getBarricades(){
        return barricades;
    }

    public void addBarricades(Cuboid cube){
        barricades.add(cube);
    }

    public String getName(){ return name; }

    public void setName(String name){ this.name = name; }

    public boolean isModifyingDoor(Player p){
        if(modifyDoor != null && modifyDoor.equals(p)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSomeoneModifyingDoor(){
        if(modifyDoor == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void setDoorModifier(Player p){ this.modifyDoor = p; }

    public Cuboid getDoor(){ return door; }

    public void setDoor(Cuboid door){ this.door = door; }

    public boolean isModifyingBarricades(Player p){
        if(modifyBarricade != null && modifyBarricade.equals(p)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSomeoneModifyingBarricades(){
        if(modifyBarricade == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void setBarricadesModifier(Player p){ this.modifyBarricade = p; }

    public void setBarricades1(Location loc){ this.modifeBarricade1 = loc; }

    public void setBarricades2(Location loc){
        this.modifeBarricade2 = loc;

        addBarricades(new Cuboid(modifeBarricade1, modifeBarricade2));

        modifyBarricade = null;
        modifyBarricade2 = null;
    }

    public void setDoor1(Location loc){ this.modifeDoor1 = loc; }

    public void setDoor2(Location loc){
        this.modifeDoor2 = loc;

        setDoor(new Cuboid(modifeDoor1, modifeDoor2));

        modifyDoor = null;
        modifeDoor1 = null;
        modifyDoor2 = null;
    }

    public Player getModifyDoor2() {
        return modifyDoor2;
    }

    public void setModifyDoor2(Player modifyDoor2) {
        if(modifyDoor == modifyDoor2) {
            this.modifyDoor2 = modifyDoor2;
        }
        else {
            modifyDoor2.sendMessage("§cUne erreur est survenue");
        }
    }

    public Player getModifyBarricade2() {
        return modifyBarricade2;
    }

    public void setModifyBarricade2(Player modifyBarricade2) {
        if(modifyBarricade == modifyBarricade2) {
            this.modifyBarricade2 = modifyBarricade2;
        }
        else {
            modifyBarricade2.sendMessage("§cUne erreur est survenue");
        }
    }

    public boolean isModifyingSign(Player p){
        if(modifySign != null && modifySign.equals(p)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSomeoneModifyingSign(){
        if(modifySign == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void setSignModifier(Player p){ this.modifySign = p; }

    public Player getSignModifier(Player p){ return modifySign; }

    public Player getDestructMobZones() {
        return destructMobZones;
    }

    public void setDestructMobZones(Player destructMobZones) {
        this.destructMobZones = destructMobZones;
    }

    public boolean isDestructMobZones(Player p){
        if(destructMobZones != null && destructMobZones.equals(p)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSomeoneDestructMobZones(){
        if(destructMobZones == null){
            return false;
        }
        else{
            return true;
        }
    }

    public Player getDestructBarricades() {
        return destructBarricades;
    }

    public void setDestructBarricades(Player destructBarricades) {
        this.destructBarricades = destructBarricades;
    }

    public boolean isDestructBarricades(Player p){
        if(destructBarricades != null && destructBarricades.equals(p)){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSomeoneDestructBarricades(){
        if(destructBarricades == null){
            return false;
        }
        else{
            return true;
        }
    }

    public void resetDoor(){
        this.door = null;
        this.modifyDoor = null;
        this.modifyDoor2 = null;
        this.modifeDoor1 = null;
        this.modifeDoor2 = null;
    }

    public void resetBarricadesCréa(){
        modifyBarricade = null;
        modifyBarricade2 = null;
        modifeBarricade1 = null;
        modifeBarricade2 = null;
    }

    public void resetSign(){
        modifySign = null;
        buyingSign = null;
        cost = 0;
    }

    public void resetDestructMob(){
        setDestructMobZones(null);
        for(Location mobspawn : getSpawnMobZones()){
            mobspawn.getWorld().getBlockAt(mobspawn).setType(Material.AIR);
        }
    }

    public Cuboid getNearBarricade(Player p){
        for(Cuboid barri : barricades){
            barri.isNearTo(p.getLocation());
            return barri;
        }
        return null;
    }

    public Cuboid getNearBarricade(Entity p){
        for(Cuboid barri : barricades){
            barri.isNearTo(p.getLocation());
            return barri;
        }
        return null;
    }

    public void openZone(Player opener){
        isActive = true;

        for(Player pls : Bukkit.getOnlinePlayers()){
            Title.sendTitle(pls, 2, 20, 2, "§eZone §6" + getName() + " §eest ouverte", "§epar " + opener.getName());
            Utils.playSound(pls, getDoor().midpoint(), Sound.LEVEL_UP, 100);
        }

        new BukkitRunnable(){

            List<Block> blockList = getDoor().blocksInside();
            int i = 0;

            @Override
            public void run() {

                if(blockList.get(i) == null){
                    return;
                }

                blockList.get(i).setType(Material.AIR);
                for(Player pls : Bukkit.getOnlinePlayers()) {
                    Utils.playSound(pls, getDoor().midpoint(), Sound.ITEM_BREAK, 5);
                }

                i++;
            }
        }.runTaskTimer(Survivor.getInstance(), 10, 5);

    }
}
