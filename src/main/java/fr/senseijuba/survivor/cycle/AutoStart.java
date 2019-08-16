package fr.senseijuba.survivor.cycle;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameState;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoStart extends BukkitRunnable {

    Survivor inst;
    int timer;

    public AutoStart(Survivor instance) {
        this.inst = instance;
        this.timer = instance.timer;
    }


    @Override
    public void run() {

        if(Bukkit.getOnlinePlayers().size() < 3) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Title.sendTitle(player, 0, 15, 5, "§cWARNING", "Not enough player");
            }
            timer = inst.timer;
        }

        for(Player pls : Bukkit.getOnlinePlayers()) {
            pls.setLevel(timer);
        }

        switch (timer) {

            case 10:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§c➓", "until start");
                }
                break;

            case 5:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§c❺", "until start");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 3.0f, 0.5f);
                }
                break;

            case 4:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§c❹", "until start");
                }
                break;

            case 3:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§6❸", "until start");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 3.0f, 1f);
                }
                break;

            case 2:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§e❷", "until start");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 3.0f, 1f);
                }

                break;

            case 1:
                for(Player player : Bukkit.getOnlinePlayers()) {
                    Title.sendTitle(player, 0, 15, 5, "§2❶", "until start");
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 3.0f, 1f);
                }
                break;
            case 0:
                GameCycle cycle = new GameCycle(inst);
                cycle.runTaskTimer(inst, 0, 20);

                int test = inst.getVoteAleatoire();

                for(int value : inst.getVote().values()){
                    if(value > test){
                        test = value;
                    }
                }

                Map mapvoted = null;
                List<Map> voted = new ArrayList<>();

                for(Map map : inst.getVote().keySet()){
                    if(inst.getVote().get(map).equals(test)){
                        mapvoted = map;
                        return;
                    }

                    voted.add(map);
                }

                if(mapvoted == null){
                    mapvoted = voted.get(new Random(4).nextInt());
                }

                inst.setCurrentMap(mapvoted);

                try {
                    Utils.saveWorld(mapvoted.getW());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for(Player player : Bukkit.getOnlinePlayers())
                    player.teleport(mapvoted.getSpawnpoint());


                inst.getPlayerManager().initDeathKills();
                inst.setGameState(GameState.STARTED);

                cancel();
                break;

            default:
                break;
        }

        timer--;

    }
}