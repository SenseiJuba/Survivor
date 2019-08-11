package fr.senseijuba.survivor.database.player;

import fr.senseijuba.survivor.Survivor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class PlayerDataManager {

    Survivor inst = Survivor.getInstance();

    public void loadPlayerData(Player player) throws SQLException {
        if(!inst.getDataPlayers().containsKey(player)){
            PlayerData pData = inst.getMariadb().createPlayerData(player);
            inst.getDataPlayers().put(player, pData);
        }
    }

    public void savePlayerData(Player player) throws SQLException {
        if(inst.getDataPlayers().containsKey(player)){
            inst.getMariadb().updatePlayerData(player);
        }
    }
}
