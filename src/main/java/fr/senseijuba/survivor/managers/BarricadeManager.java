package fr.senseijuba.survivor.managers;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Title;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.utils.config.ConfigEntries;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BarricadeManager implements Listener {

    Survivor inst;
    Cuboid barricade;

    public BarricadeManager(Survivor inst){
        this.inst = inst;
        new BukkitRunnable()
        {
            @Override
            public void run()//SAVES
            {
                if(inst.getCurrentMap() != null) {
                    for (Entity entity : inst.getCurrentMap().getW().getEntities()) {
                        if (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.WOLF)) {
                            Cuboid barricade = inst.getCurrentMap().getNearBarricade(entity);
                            for (Block block : barricade.blocksInside()) {
                                if (!block.getType().equals(Material.AIR)) {
                                    block.setType(Material.AIR);
                                    Utils.playSound(block.getLocation(), Sound.ZOMBIE_WOODBREAK, 5);
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer((Plugin) inst, 20, 20);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e){
        if(inst.gameState.equals(GameState.STARTED)) {
            if (e.isSneaking()) {
                Cuboid barricade = inst.getCurrentMap().getNearBarricade(e.getPlayer());
                if(barricade != null && barricade.isBuilding()){
                    this.barricade= barricade;
                    new BukkitRunnable()
                    {
                        Player p = e.getPlayer();
                        boolean finish = false;
                        Cuboid zone = barricade;

                        @Override
                        public void run()//SAVES
                        {
                            finish = true;

                            if(!p.isSneaking() || !barricade.isNearTo(p.getLocation())){
                                cancel();
                            }

                            Utils.playSound(zone.midpoint(), Sound.DIG_WOOD, 3);

                            for(Block block : barricade.blocksInside()){
                                if(block.getType().equals(Material.AIR)){
                                    block.setType(Material.WOOD_STEP);
                                    finish = false;
                                    continue;
                                }
                            }

                            if(finish){
                                cancel();
                            }
                        }
                    }.runTaskTimer((Plugin) inst, 20, 20);
                }
            }
        }
    }

    @EventHandler
    public void onUseAllBarricade(PlayerInteractEvent e){
        if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getItem().equals(Survivor.getInstance().getSpecialItem().get(ConfigEntries.ATOUT_BARRICADE))){
            Title.sendTitle(e.getPlayer(), 0, 20, 10, ConfigEntries.ATOUT_BARRICADE, ConfigEntries.ATOUT_USE);
            Utils.broadcastMessage(e.getPlayer().getWorld(), ConfigEntries.format(ConfigEntries.ATOUT_USE_ALL, e.getPlayer()));
            for(Player p : e.getPlayer().getWorld().getPlayers()){
                Utils.playSound(p, p.getLocation(), Sound.DIG_WOOD, 2);
            }
            for(Zone zone : Survivor.getInstance().getCurrentMap().getZones()){
                if(zone.isActive){
                    for(Cuboid barricade : zone.getBarricades()){
                        for(Block block : barricade.blocksInside()){
                            block.setType(Material.WOOD_STEP);
                        }
                    }
                }
            }
        }
    }
}
