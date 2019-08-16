package fr.senseijuba.survivor.database.player;

import lombok.Getter;
import lombok.Setter;

public class PlayerData {

    @Getter @Setter private int gameplayed;
    @Getter @Setter private int maxwaves;
    @Getter @Setter private int kills;
    @Getter @Setter private int deaths;
    @Getter @Setter private int lvl;
    @Getter @Setter private double xp;
    @Getter @Setter private double xptolvl;

    public void addXp(int xp) {
        this.xp += xp;
        updateXp();
    }

    public boolean updateXp(){

        xptolvl = lvl >= 50 ? 5000 : lvl * 100;

        if(xp > xptolvl){
            xp = xp - xptolvl;
            lvl++;
            xptolvl = lvl >= 50 ? 5000 : lvl * 100;

            return true;
        }
        return false;
    }
}
