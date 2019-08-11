package fr.lumin0u.vertix.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.commands.tfcommands.AddBifurcCommand;
import fr.lumin0u.vertix.commands.tfcommands.BSAPFDLDSCommand;
import fr.lumin0u.vertix.commands.tfcommands.EndRailsCommand;
import fr.lumin0u.vertix.commands.tfcommands.FinalTerminusCommand;
import fr.lumin0u.vertix.commands.tfcommands.RmRailsCommand;
import fr.lumin0u.vertix.commands.tfcommands.SafeZoneCommand;
import fr.lumin0u.vertix.commands.tfcommands.SetMinToStartCommand;
import fr.lumin0u.vertix.commands.tfcommands.SpawnpointCommand;
import fr.lumin0u.vertix.commands.tfcommands.StartCommand;
import fr.lumin0u.vertix.commands.tfcommands.StartRailsCommand;
import fr.lumin0u.vertix.commands.tfcommands.SuperPowerCommand;
import fr.lumin0u.vertix.commands.tfcommands.TeamCommand;
import fr.lumin0u.vertix.commands.tfcommands.TfArgCommand;
import fr.lumin0u.vertix.commands.tfcommands.TipsCommand;
import fr.lumin0u.vertix.commands.tfcommands.WorldsCommand;
import fr.lumin0u.vertix.commands.tfcommands.ZoneCommand;
import fr.lumin0u.vertix.managers.CartManager;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.WeaponManager;
import fr.lumin0u.vertix.weapons.guns.AbstractGun;
import fr.lumin0u.vertix.weapons.guns.Sniper;

public class TfCommand implements TabExecutor, CommandExecutor
{
	private List<TfArgCommand> commands;

	public TfCommand()
	{
		commands = new ArrayList<>();
		registerCommands();
	}

	public void registerCommands()
	{
		registerCommand(new StartCommand());
		registerCommand(new SpawnpointCommand());
		registerCommand(new StartRailsCommand());
		registerCommand(new SetMinToStartCommand());
		registerCommand(new SafeZoneCommand());
		registerCommand(new RmRailsCommand());
		registerCommand(new FinalTerminusCommand());
		registerCommand(new EndRailsCommand());
		registerCommand(new AddBifurcCommand());
		registerCommand(new BSAPFDLDSCommand());
		registerCommand(new ZoneCommand());

		registerCommand(new WorldsCommand());
		registerCommand(new TipsCommand());

		registerCommand(new TeamCommand());
		registerCommand(new SuperPowerCommand());
	}

	public void registerCommand(TfArgCommand command)
	{
		commands.add(command);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args)
	{
		// TF.debug("----tab----");

		// for(String arg : args)
		// TF.debug(arg);

		if(!(sender instanceof Player))
			return null;

		Player p = (Player)sender;

		List<String> commandsNames = new ArrayList<>();

		if(args.length == 1)
		{
			for(TfArgCommand c : commands)
				if(Utils.startsLikely(args[0], c.getName()) && !c.isHidden())
					commandsNames.add(c.getName());
		}

		else
		{
			for(TfArgCommand c : commands)
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
	public static boolean bulles = false;
	public static boolean debug = false;
	public static boolean configDebug = false;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("§cSeuls les joueurs peuvent effectuer cette commande.");
			return true;
		}

		if(!sender.isOp())
		{
			sender.sendMessage("§cChuis vraiment désolé de te dire ça mais tu dois être op pour faire cette commande.");
			return true;
		}

		if(args.length < 1)
		{
			// sender.sendMessage(String.join("\n", help));

			List<String> realHelp = new ArrayList<>();

			realHelp.add("§b------------TF HELP------------");
			realHelp.add("§3§lREMPLACEZ <T> PAR LA PREMIERE LETTRE DU NOM DE LA TEAM CONCERNEE");
			realHelp.add("§f§l");

			for(TfArgCommand c : commands)
			{
				if(!c.isHidden())
					realHelp.add("§f§l/tf " + c.getName() + (c.getUse().equals("") ? "" : " " + c.getUse()) + " §7: " + c.getDef());
			}

			realHelp.add("§b------------TF HELP------------");

			sender.sendMessage(String.join("\n", realHelp));

			return true;
		}

		Player p = (Player)sender;

		GameManager gm = GameManager.getInstance(p.getWorld());

		try
		{
			for(TfArgCommand c : commands)
			{
				if(c == null)
					continue;

				if(c.isExecutableFrom(args[0]) && c.getMinArgs() + 1 <= args.length)
				{
					c.execute(p, gm, String.join("!t'g!", args).replaceFirst(args[0], "").replaceFirst("!t'g!", "").split("!t'g!"));

//					p.sendMessage("§aExecuted " + c.getName());

					return true;
				}

				else if(c.isExecutableFrom(args[0]) && c.getMinArgs() + 1 > args.length)
				{
					p.sendMessage("§cIl manque des arguments ! '/tf " + c.getName() + " " + c.getUse() + "'");

					return true;
				}
			}
		}catch(Exception e)
		{
			if(gm == null)
			{
				p.sendMessage("§cCe monde n'a pas été register. Utilisez '/tf worlds'");
				return true;
			}
			
			else
			{
				e.printStackTrace();
				p.sendMessage("§cUne erreur est survenue lors de l'éxécution de cette commande");
				return true;
			}
		}

		// p.sendMessage("§cNothing executed");

		if(args[0].equalsIgnoreCase("stop"))
		{
			if(gm.isGameStarted())
			{
				gm.setStarted(false);

				Utils.broadcastMessage(gm.getWorld(), "§aLa partie est arreté !");
			}

			else
				p.sendMessage("§cLa partie est déja arreté !");

			return true;
		}

		// else if(args.length > 1 && args[0].equalsIgnoreCase("event"))
		// {
		// NickNamerAPI.getNickManager().setNick(p.getUniqueId(), args[1]);
		//
		// return true;
		// }

		else if(args[0].equalsIgnoreCase("event"))
		{
			TF.debug(Sniper.class.getName());
			TF.debug(Sniper.class.getSimpleName());
			TF.debug(Sniper.class.getCanonicalName());
			TF.debug(Sniper.class.getTypeName());

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("victory"))
		{
			gm.victory(gm.getTeamByLetter(args[1]));

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("addTeam"))
		{
			gm.getTeams().add(new Team(args[1], args[2].replaceAll("&", "§"), p.getLocation(), PlayerManager.bodyCub(p).multiply(5), p.getWorld(), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS), Material.CARPET, DyeColor.valueOf(args[3]), 32, Material.ACACIA_DOOR_ITEM));

			return true;
		}

		else if(args[0].equalsIgnoreCase("createGun"))
		{
			AbstractGun gun = new AbstractGun(args[1], Material.valueOf(args[2]), Integer.parseInt(args[3]), Double.parseDouble(args[4]), Integer.parseInt(args[5]), Double.parseDouble(args[6]), Integer.parseInt(args[7]), Double.parseDouble(args[8]), Boolean.parseBoolean(args[9]), "guns." + args[10], Float.parseFloat(args[11]), 1)
			{};

			p.getInventory().addItem(gun.getItem(1));

			gun.shoot(p);

			return true;
		}

		else if(args[0].equalsIgnoreCase("showrails") && gm.getCartManager() != null)
		{
			Team t = gm.getTeamByLetter(args[1]);

			if(t == null)
			{
				p.sendMessage("§cIl n'existe pas de team commençant par la lettre '" + args[1] + "'");
				return true;
			}

			else
			{
				CartManager cm = gm.getCartManager();

				HashMap<Location, Material> types = new HashMap<>();
				HashMap<Location, MaterialData> states = new HashMap<>();

				for(Location l : cm.getRails(t))
				{
					types.put(l, l.getBlock().getType());
					states.put(l, l.getBlock().getState().getData().clone());

					l.getBlock().setType(Material.WOOL);
					BlockState blockState = l.getBlock().getState();
					blockState.setData(new Wool(t.getDyeColor()));
					blockState.update();
				}

				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						for(Location l : types.keySet())
							l.getBlock().setType(types.get(l));

						for(Location l : states.keySet())
						{
							BlockState blockState = l.getBlock().getState();
							blockState.setData(states.get(l));
							blockState.update();
						}
					}
				}.runTaskLater(TF.getInstance(), 40);
			}

			return true;
		}

		else if(args[0].equalsIgnoreCase("dj"))
		{
			TF.getInstance().getListener().reloadDoubleJump(p, true);

			return true;
		}

		else if(args[0].equalsIgnoreCase("kill"))
		{
			if(args.length == 1)
				GameManager.doDeathEffect(p, p, GameManager.getInstance(p.getWorld()));

			else
			{
				PlayerManager.getInstance().addDamagerTo(Bukkit.getPlayer(args[1]), p);
				GameManager.doDeathEffect(Bukkit.getPlayer(args[1]), p, GameManager.getInstance(p.getWorld()));
			}

			return true;
		}

		else if(args[0].equalsIgnoreCase("getUlti"))
		{
			p.getInventory().setItem(6, PlayerManager.getInstance().kitOf(p).getSpecial().getItem(1));

			WeaponManager.getInstance().startReloadUlti(p);
			WeaponManager.getInstance().stopUlti(p);

			return true;
		}

		else if(args[0].equalsIgnoreCase("spawn"))
		{
			Location coin = p.getLocation().clone().add(-50, -50, -50);

			for(int x = 0; x < 100; x++)
			{
				for(int y = 0; y < 100; y++)
				{
					for(int z = 0; z < 100; z++)
					{
						Location point = coin.clone().add(x, y, z);

						if(point.getBlock().getType().equals(Material.AIR) && !point.clone().add(0, -1, 0).getBlock().isLiquid() && !point.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR) && new Random().nextInt(40) == 1)
						{
							List<EntityType> normalEntities = new ArrayList<>();
							normalEntities.add(EntityType.COW);
							normalEntities.add(EntityType.SHEEP);
							normalEntities.add(EntityType.PIG);
							normalEntities.add(EntityType.CHICKEN);
							normalEntities.add(EntityType.RABBIT);

							point.getWorld().spawnEntity(point, normalEntities.get(new Random().nextInt(normalEntities.size())));
						}

						if(point.getBlock().getType().equals(Material.STATIONARY_WATER) && new Random().nextInt(100) == 1)
						{
							point.getWorld().spawnEntity(point, EntityType.SQUID);
						}
					}
				}
			}

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("herbe"))
		{
			int rayon;

			try
			{
				rayon = Integer.parseInt(args[1]);
			}catch(NumberFormatException e)
			{
				p.sendMessage("§c" + args[1] + " n'est pas un nombre");
				return true;
			}

			for(int x = 0; x < rayon; x++)
			{
				for(int y = 0; y < rayon; y++)
				{
					for(int z = 0; z < rayon; z++)
					{
						Location point = p.getLocation().clone().add(x, y, z);

						if(point.getBlock().getType().isTransparent())
						{
							point.getBlock().setType(Material.AIR);
						}
					}
				}
			}

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("wshjeveuxcopiercemonde"))
		{
			File source = new File(p.getWorld().getName());
			File target = new File(String.join(" ", args).replaceFirst(args[0] + " ", ""));

			TF.copyWorld(source, target);

			p.sendMessage("création ...");
			Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
			p.sendMessage("oui");

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("wshjeveuxcreerunmonde"))
		{
			p.sendMessage("création ...");
			Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
			p.sendMessage("oui");

			return true;
		}

		else if(args[0].equalsIgnoreCase("wshjeveuxcopiercemonde"))
		{
			File source = new File(p.getWorld().getName());
			File target = new File(p.getWorld().getName() + "_copie");

			TF.copyWorld(source, target);

			p.sendMessage("création ...");
			Bukkit.createWorld(new WorldCreator(p.getWorld().getName() + "_copie"));
			p.sendMessage("oui");

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("deleteworld"))
		{
			TF.getInstance().rmWorld(p.getWorld());

			return true;
		}

		else if(args.length > 1 && args[0].equalsIgnoreCase("gotoworld"))
		{
			p.sendMessage(String.join(" ", args).replaceFirst(args[0] + " ", ""));

			if(new File(String.join(" ", args).replaceFirst(args[0] + " ", "")).exists())
			{
				p.sendMessage("création ...");
				Bukkit.createWorld(new WorldCreator(String.join(" ", args).replaceFirst(args[0] + " ", "")));
				p.sendMessage("oui");
			}

			if(Bukkit.getWorld(String.join(" ", args).replaceFirst(args[0] + " ", "")) != null)
			{
				p.teleport(new Location(Bukkit.getWorld(String.join(" ", args).replaceFirst(args[0] + " ", "")), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
			}

			else
				p.sendMessage("non");

			return true;
		}

		else if(args[0].equalsIgnoreCase("wshjeveuxsavoirlesmondes"))
		{
			for(World w : Bukkit.getWorlds())
				p.sendMessage(w.getName());

			return true;
		}

		else if(args[0].equalsIgnoreCase("wshjeveuxsavoirlesmondestout"))
		{
			for(File f : TF.getInstance().getDataFolder().getAbsoluteFile().getParentFile().getParentFile().listFiles())
				if(f.isDirectory() && new File(f.getAbsolutePath() + "/level.dat").exists())
					p.sendMessage(f.getName());

			return true;
		}

		else if(args[0].equalsIgnoreCase("cheat"))
		{
			if(args.length == 1)
				for(Player pl : p.getWorld().getPlayers())
					changeCheat(pl);

			else if(Bukkit.getPlayer(args[1]) != null)
				changeCheat(Bukkit.getPlayer(args[1]));

			return true;
		}

		else if(args[0].equalsIgnoreCase("bulles"))
		{
			bulles = !bulles;

			return true;
		}

		else if(args[0].equalsIgnoreCase("debug"))
		{
			debug = !debug;

			return true;
		}

		else if(args[0].equalsIgnoreCase("debugconfig"))
		{
			configDebug = !configDebug;

			if(configDebug)
				TF.getInstance().saveTheConfig(TF.getInstance().getConfig());

			else if(!configDebug)
				TF.getInstance().getWhatsInConfig();

			return true;
		}

		else if(args[0].equalsIgnoreCase("reload"))
		{
			TF.getInstance().reinitializeGameManager(gm);

			return true;
		}

		else if(args[0].equalsIgnoreCase("rmbifurcs"))
		{
			gm.getCartManager().setBifurcs(gm.getTeamByLetter(args[1]), new LinkedList<>());

			return true;
		}

		List<String> realHelp = new ArrayList<>();

		realHelp.add("§b------------TF HELP------------");
		realHelp.add("§3§lREMPLACEZ <T> PAR LA PREMIERE LETTRE DU NOM DE LA TEAM CONCERNEE");
		realHelp.add("§f§l");

		for(TfArgCommand c : commands)
		{
			if(!c.isHidden())
				realHelp.add("§f§l/tf " + c.getName() + (c.getUse().equals("") ? "" : " " + c.getUse()) + " §7: " + c.getDef());
		}

		realHelp.add("§b------------TF HELP------------");

		sender.sendMessage(String.join("\n", realHelp));

		return true;
	}

	public void changeCheat(Player p)
	{
		if(cheaters.contains(p))
		{
			cheaters.remove(p);
			p.sendMessage("§aVous arretez de tricher, c'est bien.");
			p.setMaxHealth(PlayerManager.getInstance().kitOf(p).getMaxHealth());
			p.setHealth(PlayerManager.getInstance().kitOf(p).getMaxHealth());
		}

		else
		{
			cheaters.add(p);
			p.sendMessage("§cVous commencez a tricher, c'est mal.");
			p.setMaxHealth(PlayerManager.getInstance().kitOf(p).getMaxHealth() * 5);
			p.setHealth(PlayerManager.getInstance().kitOf(p).getMaxHealth() * 5);
		}
	}
}
