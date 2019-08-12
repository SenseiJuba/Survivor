package fr.senseijuba.survivor.mobs;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractMob {

    protected EntityType entity;
    protected int minVague;
    protected int pv;
    protected int money;

    public AbstractMob(EntityType entity, int minVague, int pv, int money) {

        this.entity = entity;
        this.minVague = minVague;
        this.pv = pv;
        this.money = money;
    }

    public void spawnMob(List<Location> loctableau, int nb){

    }

    //entity
    public EntityType getEntity() {
        return entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    //minVague
    public int getMinVague() {
        return minVague;
    }

    public void setMinVague(int minVague) {
        this.minVague = minVague;
    }

    //pv
    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public void addPv(int pv) {
        this.pv += pv;
    }

    public void removePv(int pv) {
        this.pv -= pv;
    }

    //money
    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

}