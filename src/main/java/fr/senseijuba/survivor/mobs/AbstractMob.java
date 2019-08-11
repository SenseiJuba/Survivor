package fr.senseijuba.survivor.mobs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;

public abstract class AbstractMob implements Entity {

    protected EntityType entity;
    protected int minVague;
    protected int pv;
    protected Material weapon;
    protected Item armor;
    protected int money;

    public AbstractMob(EntityType entity, int minVague, int pv, Material weapon, Item armor, int money){

        this.entity = entity;
        this.minVague = minVague;
        this.pv = pv;
        this.weapon = weapon;
        this.armor = armor;
        this.money = money;
    }

    //entity
    public EntityType getEntity(){return entity; }

    public void setEntity(EntityType entity){this.entity = entity; }

    //minVague
    public int getMinVague(){return minVague; }

    public void setMinVague(int minVague){this.minVague = minVague; }

    //pv
    public int getPv(){return pv; }

    public void setPv(int pv){this.pv = pv; }

    public void addPv(int pv){this.pv += pv; }

    public void removePv(int pv){this.pv -= pv; }

    //weapon
    public Material getWeapon(){return weapon; }

    public void setWeapon(Material weapon){this.weapon = weapon; }

    //armor
    public Item getArmor(){return armor; }

    public void setArmor(Item armor){this.armor = armor; }

    //money
    public int getMoney(){return money; }

    public void setMoney(int money){this.money = money; }


}
