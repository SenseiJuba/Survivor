package fr.senseijuba.survivor.spawn;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.spawn.item.SpawnItemAction;
import fr.senseijuba.survivor.spawn.items.SimpleSpawnItem;
import fr.senseijuba.survivor.utils.ItemBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Spawn implements Listener {

    public Spawn(Survivor instance) {

        Bukkit.getServer().getPluginManager().registerEvents(this, instance);

        //Normal Items
        registerItem(new SimpleSpawnItem(0, new ItemBuilder(Material.BOOK)
                .name(ChatColor.BLUE + "Kit Editeur"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                new LadderSelect(player) {
                    @Override
                    public void onSelect(Ladder ladder) {
                        if (ladder != null) {
                            if (ladder.isEditable()) {
                                new KitBuilder(ladder, player.getPlayer()).init();
                            } else {
                                player.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas éditer ce kit : "
                                        + ChatColor.GOLD + ladder.getName() + ChatColor.RED + " ladder.");
                            }
                        }
                    }
                };
            }
        }));

        registerItem(new SimpleSpawnItem(2, new ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.BLUE + "Rejoindre une Queue"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (!PracticePVP.getQueueManager().inQueue(player)) {
                    new QueueSelect(player) {
                        @Override
                        public void onSelect(final QueueType type) {
                            new LadderSelect(player, type) {
                                @Override
                                public void onSelect(final Ladder ladder) {
                                    Queue queue = PracticePVP.getQueueManager().getQueues().get(type);
                                    if (queue != null) {
                                        if (queue.canJoin(player)) {
                                            queue.addToQueue(player, ladder);
                                            if(queue.getType() == QueueType.RANKED){
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "Vous avez rejoint la " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue avec"+
                                                        ChatColor.GOLD+player.getElo(ladder)+" ELO"+ ChatColor.BLUE+".");
                                            }
                                            else if (queue.getType() == QueueType.PING){
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "Vous avez rejoint la " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue avec"+
                                                        ChatColor.GOLD+((CraftPlayer)player.getPlayer()).getHandle().ping+" ping"+ ChatColor.BLUE+".");
                                            }
                                            else{
                                                player.getPlayer().sendMessage(ChatColor.BLUE + "Vous avez rejoint la " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue.");
                                            }
                                            player.getPlayer().getInventory().clear();
                                            player.getPlayer().getInventory().setArmorContents(null);
                                            player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Partir de la queue").build());
                                            player.getPlayer().updateInventory();
                                            player.getScoreboard().update();
                                        } else {
                                            player.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas rejoindre la queue.");
                                        }
                                    } else {
                                        player.getPlayer().sendMessage(ChatColor.RED + "Cette queue n'est pas encore rejoignable.");
                                    }
                                }
                            };
                        }
                    };
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "Vous êtes déjà dans une queue!");
                }
            }
        }));

        /*registerItem(new SimpleSpawnItem(4, new ItemBuilder(Material.EYE_OF_ENDER)
                .name(ChatColor.BLUE + "Hoster un event"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(player.getPlayer().hasPermission("practice.events.host")){
                    player.getPlayer().sendMessage(ChatColor.GOLD+"Events seront possible bientôt!");
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.GOLD+"Seul "+ChatColor.LIGHT_PURPLE+"les donators"+ChatColor.GOLD+" peuvent host des events");
                }
            }
        }));

         */

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(Material.FIREWORK_CHARGE)
                .name(ChatColor.BLUE + "Kite PracticePVP"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if(PracticePVP.getArenaManager().getNewestArena(ArenaType.KITE) == null){
                    player.getPlayer().sendMessage(ChatColor.RED+"Kite PracticePVP n'a pas d'arena");
                    return;
                }
                new KiteSelect(player.getPlayer());
            }
        }));


        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, DyeColor.LIME.getDyeData()))
                .name(ChatColor.BLUE + "Créer une Party"), new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party create");
            }
        }));

        //Party items

        registerItem(new SimpleSpawnItem(0, new ItemBuilder(new ItemStack(Material.NETHER_STAR))
                .name(ChatColor.BLUE + "Party Membres"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    player.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Party de " + player.getParty().getLeader() +
                            ChatColor.GOLD);
                    for (Player p : player.getParty().getAllPlayers()) {
                        player.getPlayer().sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + p.getName());
                    }
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "Vous n'êtes pas dans une party");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(2, new ItemBuilder(new ItemStack(Material.DIAMOND_SWORD))
                .name(ChatColor.BLUE + "Rejoindre une Party Queue"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    if (player.getParty().getLeader().equals(player.getName())) {
                        if (!PracticePVP.getQueueManager().inQueue(player)) {
                            if (player.getParty().getAllPlayers().size() == 2) {
                                new QueueSelect(player) {
                                    @Override
                                    public void onSelect(final QueueType type) {
                                        new LadderSelect(player) {
                                            @Override
                                            public void onSelect(Ladder ladder) {
                                                Queue queue = PracticePVP.getQueueManager().getQueues().get(type);
                                                queue.addToQueue(player, ladder);
                                                player.getParty().msg(ChatColor.AQUA + "" + ChatColor.BOLD + "(PARTY) " + ChatColor.RESET + "" +
                                                        ChatColor.BLUE + "Votre party a rejoint " + ChatColor.GREEN +
                                                        WordUtils.capitalizeFully(queue.getType().toString().replaceAll("_", " "))
                                                        + ChatColor.BLUE + " queue.");
                                                player.getPlayer().getInventory().clear();
                                                player.getPlayer().getInventory().setArmorContents(null);
                                                player.getPlayer().getInventory().setItem(0, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Partir de la queue").build());
                                                player.getPlayer().updateInventory();
                                                player.getScoreboard().update();
                                            }
                                        };
                                    }
                                };
                            }
                            else{
                                player.getPlayer().sendMessage(ChatColor.RED+"Votre party doit avoir au minimum deux joueurs");
                            }
                        } else {
                            player.getPlayer().sendMessage(ChatColor.RED + "Vous êtes déjà dans une queue");
                        }
                    } else {
                        player.getPlayer().sendMessage(ChatColor.RED + "Seul le leader peut faire ça");
                    }
                } else {
                    player.getPlayer().sendMessage(ChatColor.RED + "Vous n'êtes pas dans une party");
                }
            }
        }));

        /*registerItem(new SimpleSpawnItem(4, new ItemBuilder(new ItemStack(Material.FIREWORK_CHARGE))
                .name(ChatColor.BLUE + "Party Kite PracticePVP"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().sendMessage(ChatColor.GOLD + "Party Kite n'est pas encore disponible");
            }
        }));

         */

        registerItem(new SimpleSpawnItem(4, new ItemBuilder(new ItemStack(Material.ENDER_CHEST))
                .name(ChatColor.BLUE + "Party à affronter"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                PartiesInv.open(player.getPlayer());
            }
        }));

        registerItem(new SimpleSpawnItem(6, new ItemBuilder(new ItemStack(Material.REDSTONE_TORCH_ON))
                .name(ChatColor.BLUE + "Party Events"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                if (player.getParty() != null) {
                    if (player.getParty().getLeader().equals(player.getName())) {
                        if(player.getParty().getAllPlayers().size() >= 2) {
                            new PartyEventSelect(player.getPlayer()) {
                                @Override
                                public void onSelect(final PartyEvent event) {
                                    new LadderSelect(player) {
                                        @Override
                                        public void onSelect(Ladder ladder) {
                                            MatchBuilder mb = PracticePVP.getMatchManager().matchBuilder(ladder);
                                            if (event == PartyEvent.FFA) {
                                                int x = 0;
                                                for (Player pl : player.getParty().getAllPlayers()) {
                                                    if (x % 2 == 0) {
                                                        mb.registerTeam(new PracticeTeam(pl.getName(), Team.ALPHA));
                                                    } else {
                                                        mb.registerTeam(new PracticeTeam(pl.getName(), Team.BRAVO));
                                                    }
                                                    mb.withPlayer(pl, pl.getName());
                                                    x++;
                                                }
                                                mb.build().startMatch(PracticePVP.getMatchManager());
                                            } else if (event == PartyEvent.TWO_TEAMS) {
                                                mb.registerTeam(new PracticeTeam("Team A", Team.ALPHA));
                                                mb.registerTeam(new PracticeTeam("Team B", Team.BRAVO));
                                                int x = 0;
                                                for(Player pl : player.getParty().getAllPlayers()){
                                                    if(x % 2 == 0){
                                                        mb.withPlayer(pl, "Team A");
                                                        pl.sendMessage(ChatColor.GOLD + "Vous êtes dans la " + ChatColor.AQUA + "Team A");
                                                    }
                                                    else{
                                                        mb.withPlayer(pl, "Team B");
                                                        pl.sendMessage(ChatColor.GOLD+"Vous êtes dans la "+ChatColor.AQUA+"Team B");
                                                    }
                                                    x++;
                                                }
                                                mb.build().startMatch(PracticePVP.getMatchManager());
                                            } else {
                                                player.getPlayer().sendMessage(ChatColor.RED + "Cette event n'est pas disponible");
                                            }
                                        }
                                    };
                                }
                            };
                        }
                        else{
                            player.getPlayer().sendMessage(ChatColor.RED+"Vous devez avoir au moins deux joueurs dans votre party");
                        }
                    } else {
                        player.getPlayer().sendMessage(ChatColor.RED + "Seul le leader de la party peut faire ça");
                    }
                }
                else{
                    player.getPlayer().sendMessage(ChatColor.RED+"Vous n'êtes pas dans une party");
                }
            }
        }));

        registerItem(new SimpleSpawnItem(8, new ItemBuilder(new ItemStack(Material.FIREBALL))
                .name(ChatColor.BLUE + "Leave the Party"), SpawnItemType.PARTY, new SpawnItemAction() {
            @Override
            public void onClick(final IPlayer player) {
                player.getPlayer().performCommand("party leave");
            }
        }));

        registerItem(new SimpleSpawnItem(8, new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.RED + "Retour au spawn")
                , SpawnItemType.SPECTATOR, new SpawnItemAction() {
            @Override
            public void onClick(IPlayer player) {
                player.setState(PlayerState.AT_SPAWN);
                player.sendToSpawn();
            }
        }));

    }

    private final Set<SpawnItem> items = new HashSet<>();

    public void registerItem(SpawnItem item) {
        items.add(item);
    }

    public void giveItems(IPlayer player) {
        if (player.getState() == PlayerState.SPECTATING_MATCH) {
            giveItems(player, SpawnItemType.SPECTATOR);
        }
        else if (player.getParty() != null) {
            giveItems(player, SpawnItemType.PARTY);
        }
        else{
            giveItems(player, SpawnItemType.NORMAL);
        }
    }

    public void giveItems(IPlayer player, SpawnItemType type) {
        for (SpawnItem i : items) {
            if (i.getType() == type) {
                i.give(player.getPlayer());
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final IPlayer iPlayer = PracticePVP.getCache().getIPlayer(p);

        if (iPlayer.getState() != PlayerState.AT_SPAWN) return;

        if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.BLAZE_POWDER && p.getItemInHand().hasItemMeta()
                && p.getItemInHand().getItemMeta().getDisplayName() != null) {
            if (PracticePVP.getQueueManager().inQueue(iPlayer)) {
                PracticePVP.getQueueManager().removeFromQueue(iPlayer);
                iPlayer.sendToSpawnNoTp();
                e.setCancelled(true);
            }
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {

            for (SpawnItem item : items) {
                if (item.getItem().equals(p.getItemInHand())) {
                    if (item.getType() == SpawnItemType.NORMAL && iPlayer.getParty() == null) {
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    } else if (item.getType() == SpawnItemType.PARTY && iPlayer.getParty() != null) {
                        e.setCancelled(true);
                        item.getAction().onClick(iPlayer);
                        break;
                    }
                }
            }

        }
    }

}
