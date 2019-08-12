package fr.senseijuba.survivor.cycle;

import com.mysql.jdbc.Util;
import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.mobs.AbstractMob;
import fr.senseijuba.survivor.mobs.Dog;
import fr.senseijuba.survivor.utils.ScoreboardSign;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
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
    int spawned = 0;
    int tospawn = new Random(3).nextInt() + 6;
    HashMap<Player, ScoreboardSign> scoreboardSignHashMap;

    public GameCycle(Survivor instance) {
        this.inst = instance;

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
    public void run() {

        if(timer == 2*60+30)
            timer = 0;


        //start vague
        if(timer == 0){
            for(Player player : Bukkit.getOnlinePlayers()){
                Title.sendTitle(player, 0, 20, 5, "Vague " + inst.getVague(), "Nombre de monstres :" + tospawn);
                Utils.playSound(player, player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 3);
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
            sb.setLine(i, "§6Total kill: " + inst.getPlayerManager().getKills().get(player.getUniqueId()));
            i--;
            sb.setLine(i, "§6Total mort: " + inst.getPlayerManager().getDeaths().get(player.getUniqueId()));
            i--;
            sb.setLine(i, "§8");
        }

        //spawn
        List<Location> spawnmob = new ArrayList<>();

        for(Zone zone : inst.getCurrentMap().getZones()){
            if(zone.isActive){
                for(Location loc : zone.getSpawnMobZones()){
                    spawnmob.add(loc);
                }
            }
        }

        if(new Random(3).nextInt() == 1 && spawned<tospawn){

            String mobname = null;

            switch(inst.vague/10%10){
                case(0):
                    //TODO BOSS
                    break;
                case(5):
                    mobname = "Dog";
                    break;
                default:
                    mobname = "Zombie";
                    break;
            }

            for(AbstractMob mob : inst.getMobManager().listMobs()){
                if(mob.getEntity().getName().equals(mobname)){
                    mob.spawnMob(spawnmob, 1);
                    tospawn++;
                }
            }
        }

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
}
