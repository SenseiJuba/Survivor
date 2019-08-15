package fr.senseijuba.survivor.cycle;

import com.mysql.jdbc.Util;
import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.atouts.Atout;
import fr.senseijuba.survivor.atouts.AtoutListener;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.mobs.AbstractMob;
import fr.senseijuba.survivor.mobs.Dog;
import fr.senseijuba.survivor.utils.ScoreboardSign;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameCycle extends BukkitRunnable {

    Survivor inst;
    double timer = 0;
    int count = 10;
    int spawned = 0;
    int tospawn = new Random(3).nextInt() + 6;
    int zombies = 0;
    @Getter HashMap<UUID, Integer> kills;
    @Getter HashMap<UUID, Integer> deaths;
    String mobname;
    VagueState vagueState;
    HashMap<Player, ScoreboardSign> scoreboardSignHashMap;

    public GameCycle(Survivor instance) {
        this.inst = instance;
        vagueState = VagueState.WAITING;

        for(Player player : Bukkit.getOnlinePlayers()){
            ScoreboardSign sb = new ScoreboardSign(player, "§cSurvivor");
            sb.create();
            sb.setLine(13, "§eTimer: " + getTimer(timer));
            sb.setLine(12, "");
            sb.setLine(11, "§eVague: " + inst.getVague());
            sb.setLine(10, "§7");
            sb.setLine(9, "§a>" + player.getDisplayName() + ": §a" + inst.getPlayerManager().getMoney().get(player.getUniqueId()) + "$");

            int i = 8;

            for(UUID uuid : inst.getPlayerManager().getMoney().keySet()){

                Player l = null;

                for(Player p : Bukkit.getOnlinePlayers()){
                    if(p.getUniqueId().equals(uuid))
                        l = p;
                }

                if(!l.equals(player)){

                    String prefix;

                    if(l.getGameMode().equals(GameMode.SPECTATOR))
                        prefix = "§4";
                    else
                        prefix = "§a";

                    sb.setLine(i, prefix + l.getDisplayName() + ": §a" + inst.getPlayerManager().getMoney().get(uuid) + "$");

                    i--;
                }
            }

            sb.setLine(i, "§4");
            i--;
            sb.setLine(i, "§6Total kill: " + inst.getPlayerManager().getKills().get(player.getUniqueId()));
            i--;
            sb.setLine(i, "§6Total mort: " + inst.getPlayerManager().getDeaths().get(player.getUniqueId()));
            i--;
            sb.setLine(i,"§8");

            new BukkitRunnable()
            {

                ScoreboardSign sc = sb;
                int timer = 0;
                String string;

                @Override
                public void run()
                {
                    switch (timer){
                        case 0:
                            string = "§cp§elay.hellaria.fr";
                            break;
                        case 1:
                            string = "§4p§cl§eay.hellaria.fr";
                            break;
                        case 2:
                            string = "§cp§4l§ca§ey.hellaria.fr";
                            break;
                        case 3:
                            string = "§ep§cl§4a§cy§e.hellaria.fr";
                            break;
                        case 4:
                            string = "§epl§ca§4y§c.§ehellaria.fr";
                            break;
                        case 5:
                            string = "§epla§cy§4.§ch§eellaria.fr";
                            break;
                        case 6:
                            string = "§eplay§c.§4h§ce§ellaria.fr";
                            break;
                        case 7:
                            string = "§eplay.§ch§4e§cl§elaria.fr";
                            break;
                        case 8:
                            string = "§eplay.h§ce§4l§cl§earia.fr";
                            break;
                        case 9:
                            string = "§eplay.he§cl§4l§ca§eria.fr";
                            break;
                        case 10:
                            string = "§eplay.hel§cl§4a§cr§eia.fr";
                            break;
                        case 11:
                            string = "§eplay.hell§ca§4r§ci§ea.fr";
                            break;
                        case 12:
                            string = "§eplay.hella§cr§4i§ca§e.fr";
                            break;
                        case 13:
                            string = "§eplay.hellar§ci§4a§c.§efr";
                            break;
                        case 14:
                            string = "§eplay.hellari§ca§4.§cf§er";
                            break;
                        case 15:
                            string = "§eplay.hellaria§c.§4f§cr";
                            break;
                        case 16:
                            string = "§eplay.hellaria.§cf§4r";
                            break;
                        case 17:
                            string = "§eplay.hellaria.f§cr";
                            break;
                        case 18:
                            string = "§eplay.hellaria.fr";
                            break;
                        case 20*9:
                            timer = 0;
                            break;
                    }

                    sb.setLine(1, string);

                    if(!inst.gameState.equals(GameState.STARTED)){
                        sb.destroy();
                        cancel();
                    }

                    timer++;
                }
            }.runTaskLater(inst, 1);
        }
    }

    @Deprecated
    @Override
    public void run(){

        //spawn
        List<Location> spawnmob = new ArrayList<>();

        if(timer == 0 && !vagueState.equals(VagueState.FINISH)){
            vagueState = VagueState.WAITING;
            initVagueDeathKills();
        }

        if(timer == 10){
            vagueState = VagueState.SPAWNING;
            for(Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage(vagueState.getMsg());
                Title.sendTitle(player, 0, 20, 5, vagueState.getName(), "Nombre de monstres :" + tospawn);
                Utils.playSound(player, player.getLocation(), Sound.ENDERDRAGON_GROWL, 3);
            }
        }

        if(new Random(40).nextInt() == 0){
            for(Player player : Bukkit.getOnlinePlayers()){
                Utils.playSound(player, player.getLocation(), Sound.AMBIENCE_CAVE, 3);
            }
        }

        //start vague
        if(zombies == 0){

            vagueState = VagueState.WAITING;
            timer = 0;
            inst.setVague(inst.getVague() + 1);

            tospawn += new Random(2).nextInt();

            zombies += tospawn;

            Sound mobsound = Sound.CAT_HISS;

            switch(inst.vague/10%10){
                case(0):
                    //TODO BOSS
                    break;
                case(5):
                    mobname = "Dog";
                    mobsound = Sound.WOLF_GROWL;
                    break;
                default:
                    mobname = "Zombie";
                    mobsound = Sound.ZOMBIE_IDLE;
                    break;
            }

            for(Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage(vagueState.getMsg());
                Title.sendTitle(player, 0, 20, 5, "Vague " + inst.getVague(), "Nombre de monstres :" + tospawn);
                Utils.playSound(player, player.getLocation(), mobsound, 3);
            }

            //spawn
            for(Zone zone : inst.getCurrentMap().getZones()){
                if(zone.isActive){
                    for(Location loc : zone.getSpawnMobZones()){
                        spawnmob.add(loc);
                    }
                }
            }
        }

        //vague over
        if(zombies == 0){
            vagueState = VagueState.FINISH;
            count = 0;
            updateStats();

            for(Player player : Bukkit.getOnlinePlayers()){
                if(inst.getPlayerManager().isDead(player)){
                    if(!inst.getPlayerManager().hasGrave(player)){
                        inst.getPlayerManager().getDeadbodies().get(player).destroyDeadBodies(player);
                        inst.getPlayerWeapon().get(player).clear();

                        for(AbstractWeapon weapon : inst.getWeaponManager().listWeapons()){
                            if(weapon.getName().equals("M1911"))
                                inst.getPlayerWeapon().put(player.getUniqueId(), Arrays.asList(weapon));
                            player.getInventory().addItem(weapon.getItem(1));
                        }
                    }
                    else{
                        inst.getPlayerManager().setGrave(player, false);
                        inst.getPlayerAtout().get(player).remove(Atout.GRAVE);
                    }

                    player.teleport(inst.getCurrentMap().getSpawnpoint());
                    player.setHealth(player.getMaxHealth());
                    player.getInventory().clear();

                    AtoutListener.updateAtout(player);

                }
            }


            for(Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage("§f┼──────§b──────§3────────────§b──────§f──────┼"
                        + "\n"
                        + "   §7» §l§3Survivor : Vague terminée\n"
                        + "\n"
                        + "   §7■ §f§lStats: \n"
                        + "        §7● §fKills: §6" + kills.get(player.getUniqueId()) + "\n"
                        + "        §7● §fMorts: §6" + deaths.get(player.getUniqueId()) + "\n"
                        + "\n§f┼──────§b──────§3────────────§b──────§f──────┼");
                Utils.playSound(player, player.getLocation(), Sound.ANVIL_LAND, 3);
                Title.sendTitle(player, 0, 20, 5, "Vague terminée", "Bonus: ");
            }
        }

        //scoreboard
        for(Player player : Bukkit.getOnlinePlayers()) {
            ScoreboardSign sb = scoreboardSignHashMap.get(player);
            sb.setLine(13, "§eTimer: " + getTimer(timer));
            sb.setLine(12, "");
            sb.setLine(11, "§eVague: " + inst.getVague());
            sb.setLine(10, "§7");
            sb.setLine(9, "§a>" + player.getDisplayName() + ": §a" + inst.getPlayerManager().getMoney().get(player.getUniqueId()) + "$");

            int i = 8;

            for (UUID uuid : inst.getPlayerManager().getMoney().keySet()) {

                Player l = null;

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getUniqueId().equals(uuid))
                        l = p;
                }

                if (!l.equals(player)) {

                    String prefix;

                    if (l.getGameMode().equals(GameMode.SPECTATOR))
                        prefix = "§4";
                    else
                        prefix = "§a";

                    sb.setLine(i, prefix + l.getDisplayName() + ": §a" + inst.getPlayerManager().getMoney().get(uuid) + "$");

                    i--;
                }
            }

            sb.setLine(i, "§4");
            i--;

            if(vagueState.equals(VagueState.SPAWNING)) {
                sb.setLine(i, "§6Zombies restants: " + inst.getPlayerManager().getKills().get(player.getUniqueId()));
                i--;
                sb.setLine(i, "§6Total kill: " + inst.getPlayerManager().getKills().get(player.getUniqueId()));
                i--;
            }
            else if(vagueState.equals(VagueState.WAITING)){
                sb.setLine(i, "§6» Préparation «");
                i--;
                sb.setLine(i, "§0");
                i--;
            }

            sb.setLine(i, "§8");
        }


        //spawn
        if(new Random(3).nextInt() == 1 && spawned<tospawn && vagueState.equals(VagueState.SPAWNING)){

            String mobname = null;

            for(AbstractMob mob : inst.getMobManager().listMobs()){
                if(mob.getEntity().getName().equals(mobname)){
                    mob.spawnMob(spawnmob, 1);
                    tospawn++;
                }
            }
        }

        if(count<5)
            count++;
        else
            timer++;
    }

    public String getTimer(double timer){
        String chrono = "§6";

        int min = (int) timer/60;
        int sec = (int) (timer - min * 60);

        if(min < 10)
            chrono = chrono + "0" + min;
        else
            chrono = chrono + min;

        chrono = chrono + ":";

        if(sec < 10)
            chrono = chrono + "0" + sec;
        else
            chrono = chrono + sec;

        return chrono;
    }

    public enum VagueState{

        WAITING("La préparation",
                "§f┼──────§a──────§2────────────§a──────§f──────┼"
                        + "\n"
                        + "   §7» §l§2Survivor : Préparation\n"
                        + "\n"
                        + "   §7■ §f§lPhase de Préparation : §r§fVous avez 10 secondes \n"
                        + "   §7■ §fPour vous préparer à la prochaine vague.\n"
                        + "   §7■ §fBonne chance ! \n"
                        + "\n§f┼──────§a──────§2────────────§a──────§f──────┼"),
        SPAWNING("L'attaque",
                "§f┼──────§c──────§4────────────§c──────§f──────┼"
                        + "\n"
                        + "   §7» §l§4Survivor : Attaque\n"
                        + "\n"
                        + "   §7■ §f§lPhase d'attaque : §r§fUn certain nombre de zombies \n"
                        + "   §7■ §fvous attaque, survivez et tuez les tous.\n"
                        + "   §7■ §fBonne chance ! \n"
                        + "\n§f┼──────§c──────§4────────────§c──────§f──────┼"),

        FINISH("Fin", "");

        @Getter private final String name;
        @Getter private final String msg;

        VagueState(String name, String msg){
            this.name = name;
            this.msg = msg;
        }
    }

    public void addKills(Player player){
        this.kills.put(player.getUniqueId(), this.kills.get(player.getUniqueId()) + 1);
    }

    public void addDeaths(Player player){
        this.deaths.put(player.getUniqueId(), this.deaths.get(player.getUniqueId()) + 1);
    }

    public void initVagueDeathKills(){
        for(Player player : Bukkit.getOnlinePlayers()){
            kills.put(player.getUniqueId(), 0);
            deaths.put(player.getUniqueId(), 0);
        }
    }

    public void updateStats(){
        for(UUID uuid : kills.keySet()){
            inst.getPlayerManager().addKills(uuid, count);
        }
    }
}
