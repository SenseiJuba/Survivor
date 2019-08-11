package fr.senseijuba.survivor.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.database.player.PlayerData;
import org.bukkit.entity.Player;

public class Mariadb {

    Survivor inst = Survivor.getInstance();

    // JDBC driver name and database URL
    static String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
    static String DB_URL = "jdbc:mariadb://127.0.0.1/survivordb";

    //  Database credentials
    static String USER = "root";
    static String PASS = "root";

    private static Connection conn = null;
    private static Statement stmt = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;

    public Mariadb() throws ClassNotFoundException {
        // JDBC driver name and database URL
        JDBC_DRIVER = "org.mariadb.jdbc.Driver";
        DB_URL = "jdbc:mariadb://127.0.0.1/survivordb";

        //  Database credentials
        USER = "root";
        PASS = "root";

        Class.forName(JDBC_DRIVER);
    }



    public void connect() throws SQLException {

        if(!isConnected()) {
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(
                    DB_URL, USER, PASS);
            System.out.println("Connected database successfully...");

            stmt = conn.createStatement();

            String sql = " CREATE TABLE IF NOT EXISTS `playerdata` (" +
                    "`id` INT(11) NOT NULL AUTO_INCREMENT, " +
                    "`uuid` CHAR(255) NULL DEFAULT NULL, " +
                    "`gameplayed` INT(11) NULL DEFAULT '0', " +
                    "`maxwaves` INT(11) NULL DEFAULT '0', " +
                    "`kills` INT(11) NULL DEFAULT '0', " +
                    "`deaths` INT(11) NULL DEFAULT '0', " +
                    "PRIMARY KEY (`id`)" +
                    ") " +
                    "COLLATE='latin1_swedish_ci' " +
                    "ENGINE=InnoDB" +
                    "; ";

            stmt.executeUpdate(sql);
        }
    }

    public void disconnect() throws SQLException {

        if(isConnected()) {
            System.out.println("Closing database...");
            conn.close();
            System.out.println("Closed database successfully...");
        }
    }

    public boolean isConnected(){ return conn != null; }

    public void registerPlayer(Player player) throws SQLException {
        if(!isRegister(player)){
            PreparedStatement query = conn.prepareStatement("INSERT INTO playerdata(uuid) VALUES (?)");
            query.setString(1, player.getUniqueId().toString());
            query.execute();
            query.close();
        }
    }

    public boolean isRegister(Player player) throws SQLException {

        PreparedStatement query = conn.prepareStatement("SELECT uuid FROM playerdata WHERE uuid = ?");
        query.setString(1, player.getUniqueId().toString());
        ResultSet resultat = query.executeQuery();
        boolean isRegister = resultat.next();
        query.close();

        return isRegister;
    }

    //game
    public int getGamePlayed(Player player){ return inst.getDataPlayers().containsKey(player) ? inst.getDataPlayers().get(player).getGameplayed() : 0; }

    public void addGamePlayed(Player player, int amount){

        if(inst.getDataPlayers().containsKey(player)){
            PlayerData data = inst.getDataPlayers().get(player);
            int i = data.getGameplayed() + amount;
            data.setGameplayed(i);
            inst.getDataPlayers().replace(player, data);
        }
    }

    //wave
    public int getMaxWave(Player player){ return inst.getDataPlayers().containsKey(player) ? inst.getDataPlayers().get(player).getMaxwaves() : 0; }
    public void setMaxWave(Player player, int amount) {
        if(inst.getDataPlayers().containsKey(player)){
            PlayerData data = inst.getDataPlayers().get(player);
            int i = data.getMaxwaves() + amount;
            data.setMaxwaves(i);
            inst.getDataPlayers().replace(player, data);
        }
    }

    //kill
    public int getKills(Player player){ return inst.getDataPlayers().containsKey(player) ? inst.getDataPlayers().get(player).getKills() : 0; }
    public void addKills(Player player, int amount){
        if(inst.getDataPlayers().containsKey(player)){
            PlayerData data = inst.getDataPlayers().get(player);
            int i = data.getKills() + amount;
            data.setKills(i);
            inst.getDataPlayers().replace(player, data);
        }
    }

    //death
    public int getDeaths(Player player){ return inst.getDataPlayers().containsKey(player) ? inst.getDataPlayers().get(player).getDeaths() : 0; }
    public void addGDeaths(Player player, int amount){
        if(inst.getDataPlayers().containsKey(player)){
            PlayerData data = inst.getDataPlayers().get(player);
            int i = data.getDeaths() + amount;
            data.setDeaths(i);
            inst.getDataPlayers().replace(player, data);
        }
    }

    public PlayerData createPlayerData(Player player) throws SQLException {

        if(!inst.getDataPlayers().containsKey(player)) {
            PreparedStatement query = conn.prepareStatement("SELECT gameplayed, maxwaves, kills, deaths FROM playerdata WHERE uuid = ?");
            query.setString(1, player.getUniqueId().toString());
            ResultSet result = query.executeQuery();

            int gameplayed = 0;
            int maxwaves = 0;
            int kills = 0;
            int deaths = 0;

            while (result.next()) {
                gameplayed = result.getInt("gameplayed");
                maxwaves = result.getInt("maxwaves");
                kills = result.getInt("kills");
                deaths = result.getInt("deaths");
            }

            PlayerData data = new PlayerData();
            data.setGameplayed(gameplayed);
            data.setMaxwaves(maxwaves);
            data.setKills(kills);
            data.setDeaths(deaths);
            return data;
        }

        return inst.getDataPlayers().get(player);
    }

    public void updatePlayerData(Player player) throws SQLException {

        if(inst.getDataPlayers().containsKey(player)){

            PlayerData data = inst.getDataPlayers().get(player);
            List<Integer> datas = new ArrayList<>();
            datas.add(data.getGameplayed());
            datas.add(data.getMaxwaves());
            datas.add(data.getKills());
            datas.add(data.getDeaths());
            PreparedStatement query = conn.prepareStatement("UPDATE playerdata SET gameplayed = ?, maxwaves = ?, kills = ?, deaths = ? WHERE uuid = ?");

            int i = 1;
            for(int d : datas){
                query.setInt(i, d);
                i++;
            }

            query.setString(i, player.getUniqueId().toString());
            query.executeUpdate();
            query.close();
        }
    }

    private void setSqlThing(Player player, String column, int total) throws SQLException {
        PreparedStatement query = conn.prepareStatement("UPDATE playerdata SET ? = ? WHERE uuid = ?");
        query.setString(1, column);
        query.setInt(2, total);
        query.setString(3, player.getUniqueId().toString());
        query.executeUpdate();
        query.close();
    }
}