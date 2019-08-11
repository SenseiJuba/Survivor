package fr.senseijuba.survivor.database.player;

import lombok.Getter;
import lombok.Setter;

public class PlayerData {

    @Getter @Setter private int gameplayed;
    @Getter @Setter private int maxwaves;
    @Getter @Setter private int kills;
    @Getter @Setter private int deaths;

}
