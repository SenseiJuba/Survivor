package fr.senseijuba.survivor.commands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.commands.survivorcommands.*;
import fr.senseijuba.survivor.map.Map;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.guns.AbstractGun;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SurvivorCommand implements TabExecutor, CommandExecutor
{

    private List<SurvivorArgCommand> commands;

    public SurvivorCommand()
    {
        commands = new ArrayList<>();
        registerCommands();
    }

    public void registerCommands()
    {
        registerCommand(new MapsCommand());
        registerCommand(new SpawnpointCommand());
        registerCommand(new StartCommand());
        registerCommand(new TipsCommand());
        registerCommand(new ZoneCommand());
        registerCommand(new WeaponBuyCommand());
    }

    public void registerCommand(SurvivorArgCommand command)
    {
        commands.add(command);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return null;

        Player p = (Player)sender;

        List<String> commandsNames = new ArrayList<>();

        if(args.length == 1)
        {
            for(SurvivorArgCommand c : commands)
                if(Utils.startsLikely(args[0], c.getName()) && !c.isHidden())
                    commandsNames.add(c.getName());
        }

        else
        {
            for(SurvivorArgCommand c : commands)
            {
                List<String> possibles = c.getPossibleArgs(p, args);

                if(c.isExecutableFrom(args[0]) && !possibles.isEmpty())
                {
                    for(String possible : possibles)
                    {
                        if(Utils.startsLikely(args[args.length - 1], possible))
                            commandsNames.add(possible);
                    }
                }
            }
        }

        if(commandsNames.isEmpty())
            for(Player pl : p.getWorld().getPlayers())
                if(Utils.startsLikely(args[args.length - 1], pl.getName()))
                    commandsNames.add(pl.getName());

        return commandsNames;
    }

    public static List<Player> cheaters = new ArrayList<>();
    public static boolean debug = false;
    public static boolean configDebug = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
        {
            sender.sendMessage("§cSeuls les joueurs peuvent effectuer cette commande.");
            return true;
        }

        if(!sender.isOp() && !sender.hasPermission("survivor.cmd"))
        {
            sender.sendMessage("§cChuis vraiment désolé de te dire ça mais tu dois être op pour faire cette commande.");
            return true;
        }

        if(args.length < 1)
        {
            // sender.sendMessage(String.join("\n", help));

            List<String> realHelp = new ArrayList<>();

            realHelp.add("§b--------SURVIVOR HELP----------");
            realHelp.add("§3§lListe des commandes du Survivor");
            realHelp.add("§f§l");

            for(SurvivorArgCommand c : commands)
            {
                if(!c.isHidden())
                    realHelp.add("§f§l/survivor " + c.getName() + (c.getUse().equals("") ? "" : " " + c.getUse()) + " §7: " + c.getDef());
            }

            realHelp.add("§b--------SURVIVOR HELP----------");


            sender.sendMessage(String.join("\n", realHelp));

            return true;
        }

        Player p = (Player)sender;


        try
        {
            for(SurvivorArgCommand c : commands)
            {
                if(c == null)
                    continue;

                if(c.isExecutableFrom(args[0]) && c.getMinArgs() + 1 <= args.length)
                {
                    c.execute(p, String.join("!t'g!", args).replaceFirst(args[0], "").replaceFirst("!t'g!", "").split("!t'g!"));

//					p.sendMessage("§aExecuted " + c.getName());

                    return true;
                }

                else if(c.isExecutableFrom(args[0]) && c.getMinArgs() + 1 > args.length)
                {
                    p.sendMessage("§cIl manque des arguments ! '/survivor " + c.getName() + " " + c.getUse() + "'");

                    return true;
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            p.sendMessage("§cUne erreur est survenue lors de l'exécution de cette commande");
            return true;
        }

        if(args.length > 1 && args[0].equalsIgnoreCase("victory")) {
            Survivor.getInstance().gameOver();

            return true;
        } else if (args.length > 2 && args[0].equalsIgnoreCase("tp")) {
            try {
                ((Player) sender).teleport(Bukkit.getWorld(args[2]).getSpawnLocation());
            } catch (Exception e) {
                sender.sendMessage("Le monde n'existe pas, vérifiez l'orthographe");
            }

            return true;
        } else if(args[0].equalsIgnoreCase("createGun")) {
            AbstractGun gun = new AbstractGun(args[1], Material.valueOf(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Double.parseDouble(args[5]), Integer.parseInt(args[6]), Double.parseDouble(args[7]), Integer.parseInt(args[8]), Double.parseDouble(args[9]), Boolean.parseBoolean(args[10]), "guns." + args[11], Float.parseFloat(args[12]), 1) {};

            p.getInventory().addItem(gun.getItem(1));

            gun.shoot(p);

            return true;
        } else if(args[0].equalsIgnoreCase("kill")) {
            if(args.length == 1)
                Survivor.getInstance().getL().killPlayer(p);

            else {
                Survivor.getInstance().getL().killPlayer(Bukkit.getPlayer(args[1]));
            }

            return true;
        } else if(args[0].equalsIgnoreCase("spawn")) {
            Location coin = p.getLocation().clone().add(-50, -50, -50);

            for (int x = 0; x < 100; x++) {
                for (int y = 0; y < 100; y++) {
                    for (int z = 0; z < 100; z++) {
                        Location point = coin.clone().add(x, y, z);

                        if (point.getBlock().getType().equals(Material.AIR) && !point.clone().add(0, -1, 0).getBlock().isLiquid() && !point.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) && new Random().nextInt(40) == 1) {
                            List<EntityType> normalEntities = new ArrayList<>();
                            normalEntities.add(EntityType.COW);
                            normalEntities.add(EntityType.SHEEP);
                            normalEntities.add(EntityType.PIG);
                            normalEntities.add(EntityType.CHICKEN);
                            normalEntities.add(EntityType.RABBIT);

                            point.getWorld().spawnEntity(point, normalEntities.get(new Random().nextInt(normalEntities.size())));
                        }

                        if (point.getBlock().getType().equals(Material.STATIONARY_WATER) && new Random().nextInt(100) == 1) {
                            point.getWorld().spawnEntity(point, EntityType.SQUID);
                        }
                    }
                }
            }
            return true;
        } else if(args.length > 1 && args[0].equalsIgnoreCase("wshjeveuxcopiercemonde")) {
            File source = new File(p.getWorld().getName());
            File target = new File(String.join(" ", args).replaceFirst(args[0] + " ", ""));

            Survivor.copyWorld(source, target);

            p.sendMessage("création ...");
            Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
            p.sendMessage("Monde créé");

            return true;
        } else if(args.length > 1 && args[0].equalsIgnoreCase("wshjeveuxcreerunmonde")) {
            p.sendMessage("création ...");
            Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
            p.sendMessage("Monde créé");

            return true;
        } else if(args[0].equalsIgnoreCase("wshjeveuxcopiercemonde")) {
            File source = new File(p.getWorld().getName());
            File target = new File(p.getWorld().getName() + "_copie");

            Survivor.copyWorld(source, target);

            p.sendMessage("création ...");
            Bukkit.createWorld(new WorldCreator(p.getWorld().getName() + "_copie"));
            p.sendMessage("Monde créé");

            return true;
        } else if(args.length > 1 && args[0].equalsIgnoreCase("deleteworld")) {
            Map m = null;

            for(Map map : Survivor.getInstance().maps){
                if(map.getW().equals(p.getWorld())){
                    m = map;
                    continue;
                }
            }

            if(m != null){
                Survivor.getInstance().rmMap(m);
            } else{
                p.sendMessage("§cVous n'êtes pas dans un monde");
            }
            return true;
        } else if(args.length > 1 && args[0].equalsIgnoreCase("gotoworld")) {
            p.sendMessage(String.join(" ", args).replaceFirst(args[0] + " ", ""));

            if(new File(String.join(" ", args).replaceFirst(args[0] + " ", "")).exists()) {
                p.sendMessage("création ...");
                Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
                p.sendMessage("Monde créé");
            }

            if(Bukkit.getWorld(String.join(" ", args).replaceFirst(args[0] + " ", "")) != null) {
                p.teleport(new Location(Bukkit.getWorld(String.join(" ", args).replaceFirst(args[0] + " ", "")), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
            } else
                p.sendMessage("Action impossible");

            return true;
        } else if(args[0].equalsIgnoreCase("wshjeveuxsavoirlesmondes")) {
            for(World w : Bukkit.getWorlds())
                p.sendMessage(w.getName());

            return true;
        } else if(args[0].equalsIgnoreCase("wshjeveuxsavoirlesmondestout")) {
            for(File f : Survivor.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().listFiles())
                if(f.isDirectory() && new File(f.getAbsolutePath() + "/level.dat").exists())
                    p.sendMessage(f.getName());

            return true;
        } else if(args[0].equalsIgnoreCase("cheat")) {
            if(args.length == 1)
                for(Player pl : p.getWorld().getPlayers())
                    changeCheat(pl);

            else if(Bukkit.getPlayer(args[1]) != null)
                changeCheat(Bukkit.getPlayer(args[1]));

            return true;
        } else if(args[0].equalsIgnoreCase("debug")) {
            debug = !debug;

            return true;
        } else if(args[0].equalsIgnoreCase("debugconfig")) {
            configDebug = !configDebug;

            if(configDebug)
                Survivor.getInstance().saveTheConfig(Survivor.getInstance().getConfig());

            else if(!configDebug)
                Survivor.getInstance().reloadConfig();

            return true;
        } else if(args[0].equalsIgnoreCase("reload")) {
            Survivor.getInstance().gameOver();

            return true;
        }

        List<String> realHelp = new ArrayList<>();

        realHelp.add("§b--------SURVIVOR HELP----------");
        realHelp.add("§3§lListe des commandes du Survivor");
        realHelp.add("§f§l");

        for(SurvivorArgCommand c : commands)
        {
            if(!c.isHidden())
                realHelp.add("§f§l/survivor " + c.getName() + (c.getUse().equals("") ? "" : " " + c.getUse()) + " §7: " + c.getDef());
        }

        realHelp.add("§b--------SURVIVOR HELP----------");


        sender.sendMessage(String.join("\n", realHelp));

        return true;
    }

    public void changeCheat(Player p)
    {
        if(cheaters.contains(p))
        {
            cheaters.remove(p);
            p.sendMessage("§aVous arrêtez de tricher, c'est bien.");
            p.setMaxHealth(20);
            p.setHealth(20);
        }

        else
        {
            cheaters.add(p);
            p.sendMessage("§cVous commencez à tricher, c'est mal.");
            p.setMaxHealth(20 * 5);
            p.setHealth(20 * 5);
        }
    }
}
