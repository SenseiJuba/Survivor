package fr.lumin0u.vertix.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.GameType;
import fr.lumin0u.vertix.Kit;
import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.utils.Cuboid;
import fr.senseijuba.survivor.utils.Title;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.WeaponManager;

public class GameManager
{
	private boolean gameStarted;
	private List<Team> teams;
	private GameType gameType;
	private List<UUID> playersTF;
	private int attenteSec;
	private Location lobbySpawnPoint;
	private boolean forceStart;
	private int minToStart;
	private long startDate;
	private long endDate;
	private World w;
	private boolean delete;
	private int killsToWin;
	private CartManager cm;
	private ZoneManager zm;
	private int id;

	public GameManager(World w, GameType gameType)
	{
		id = new Random().nextInt();
		init(w, gameType);
	}

	@SuppressWarnings("deprecation")
	public void init(World w, GameType gameType)
	{
		this.w = w;
		this.gameType = gameType;
		killsToWin = 40;

		minToStart = 4;

		teams = new ArrayList<>();

		if(gameType.isCarts())
			cm = new CartManager(w);
		
		if(gameType.isKoth())
			zm = new ZoneManager(w);
		
		List<Material> cartsMat = new ArrayList<>();
		cartsMat.add(Material.DIAMOND_ORE);
		cartsMat.add(Material.LAPIS_ORE);
		cartsMat.add(Material.EMERALD_ORE);
		cartsMat.add(Material.GOLD_ORE);

		if(gameType.areTeamsActive())
		{
			for(int i = 0; i < gameType.nbTeams(); i++)
			{
				Material chosen = cartsMat.get(new Random().nextInt(cartsMat.size()));
				teams.add(new Team(i, chosen, null, null, w));
				cartsMat.remove(chosen);
			}
		}

		startDate = 0;
		forceStart = false;
		delete = false;

		for(Team t : teams)
			t.setSafeZone(new Cuboid(new Location(Bukkit.getWorlds().get(0), 0, 0, 0), new Location(Bukkit.getWorlds().get(0), 0, 0, 0)));

		attenteSec = 30;
		gameStarted = false;

		playersTF = new ArrayList<>();

		ConfigurationSection file = TF.getInstance().getConfig(w);

		if(file.contains("minToStart"))
			setMinToStart(file.getInt("minToStart"));

		if(file.contains("lobbySpawnPoint"))
			setLobbySpawnPoint(Utils.stringToLoc(file.getString("lobbySpawnPoint")));

		for(Team t : teams)
		{
			Cuboid safeZone = t.getSafeZone();

			if(file.contains(t.getName(false) + ".spawnpoint"))
				t.setSpawnPoint(Utils.stringToLoc(file.getString(t.getName(false) + ".spawnpoint")));

			if(file.contains(t.getName(false) + ".szun"))
				safeZone.setLoc1(Utils.stringToLoc(file.getString(t.getName(false) + ".szun")));

			if(file.contains(t.getName(false) + ".szdeux"))
				safeZone.setLoc2(Utils.stringToLoc(file.getString(t.getName(false) + ".szdeux")));

			t.setSafeZone(safeZone);
		}
		
		if(zm != null && zm.getZone() != null)
			for(Player p : w.getPlayers())
				p.setCompassTarget(zm.getZone().midpoint());
		
		else if(lobbySpawnPoint != null)
			for(Player p : w.getPlayers())
				p.setCompassTarget(lobbySpawnPoint);
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player p : w.getPlayers())
				{
					boolean ok = false;

					if(p.getInventory().getHelmet() != null)
						for(Kit k : Kit.values())
						{
							if(k.getBlockOnHead().isSimilar(p.getInventory().getHelmet()) && k.getBlockOnHead().getDurability() == p.getInventory().getHelmet().getDurability())
							{
								ok = true;
								TF.getInstance().getPlayerManager().setKitOf(p, k);
								break;
							}
						}

					else if(p.getInventory().getHelmet() == null || !ok)
						TF.getInstance().getPlayerManager().setKitOf(p, Kit.NOKIT);
					
					Kit k = TF.getInstance().getPlayerManager().kitOf(p);
					
					PlayerManager.getInstance().resetGame(p);
					
					PlayerManager.getInstance().changeKit(p, k);
					PlayerManager.getInstance().setArmorTo(p);
				}
			}
		}.runTaskLater(TF.getInstance(), 1);

		new BukkitRunnable()
		{
			@Override
			public void run()// TEST TOUT LE TEMPS
			{
				if(delete)
				{
					cancel();
					return;
				}

				for(Player p : w.getPlayers())
				{
					LumScoreBoard.refreshScoreBoard(p);

					p.setLevel(0);

					PlayerManager.getInstance().setArmorTo(p);
					p.setFoodLevel(20);
					p.setSaturation(10f);

					if(PlayerManager.getInstance().kitOf(p).equals(Kit.NOKIT) && !p.getOpenInventory().getTopInventory().equals(Kit.getChangeKitInventory(p)))
						p.openInventory(Kit.getChangeKitInventory(p));

					if(!gameType.isCarts() && gameType.areTeamsActive())
					{
						double minDist = Double.POSITIVE_INFINITY;
						Location target = p.getLocation();

						for(Player pl : w.getPlayers())
						{
							if(!sameTeam(pl, p) && pl.getLocation().distance(p.getLocation()) < minDist)
							{
								target = pl.getLocation();
								minDist = pl.getLocation().distance(p.getLocation());
							}
						}

						p.setCompassTarget(target);
					}

					// if(cm != null && PlayerManager.getInstance().getTeamCartDirection(p) != null && cm.getCart(PlayerManager.getInstance().getTeamCartDirection(p)) != null &&
					// cm.getCart(PlayerManager.getInstance().getTeamCartDirection(p)).getLocation() != null)
					// p.setCompassTarget(cm.getCart(PlayerManager.getInstance().getTeamCartDirection(p)).getLocation());
					//
					// TF.debug(p.getCompassTarget());
					//
					// if(cm != null && PlayerManager.getInstance().getTeamCartDirection(p) != null && cm.getCart(PlayerManager.getInstance().getTeamCartDirection(p)) != null &&
					// cm.getCart(PlayerManager.getInstance().getTeamCartDirection(p)).getLocation() != null)
					// Utils.explosionParticles(p.getCompassTarget(), 2, 50, Effect.FLAME);
				}

				if(gameType.isCarts())
					for(Team t : teams)
						for(Player p : t.getPlayersOnline())
							p.setExp(((float)cm.getPurcent(t)) / 100);

				if(gameType.isKoth())
					for(Team t : teams)
						for(Player p : t.getPlayersOnline())
							p.setExp(((float)zm.getPurcent(t)) / 100);

				if(gameType.isTDM())
					for(Team t : teams)
						for(Player p : t.getPlayersOnline())
							p.setExp((float)t.getKills()/killsToWin);
			}
		}.runTaskTimer(TF.getInstance(), 10l, 10l);

		new BukkitRunnable()
		{
			@Override
			public void run()// SECONDES AVANT START
			{
				if(delete)
				{
					cancel();
					return;
				}

				if(gameStarted)
				{
					attenteSec = 30;
					return;
				}

				for(Player p : w.getPlayers())
				{
					if(w.getPlayers().size() < minToStart && !forceStart)
					{
						Title.sendActionBar(p, "�eManque �6" + (minToStart - w.getPlayers().size()) + "�e joueur" + (minToStart - w.getPlayers().size() > 1 ? "s" : ""));
						attenteSec = 30;
						continue;
					}

					Title.sendActionBar(p, "�eLa partie commence dans �6" + attenteSec + "�e seconde" + (attenteSec > 1 ? "s" : ""));

					if(attenteSec < 6 && attenteSec != 0)
					{
						p.playSound(p.getLocation(), "jeu." + (attenteSec), 1, 1);
					}
				}

				if(attenteSec == 0 && (w.getPlayers().size() >= minToStart || forceStart))
					setStarted(true);

				attenteSec--;
			}
		}.runTaskTimer(TF.getInstance(), 20l, 20l);

		new BukkitRunnable()
		{
			@Override
			public void run()// TIPS
			{
				if(delete)
				{
					cancel();
					return;
				}

				// TF.debug(w.getName()+" tips");

				for(Player p : getPlayersOnline())
				{
					// TF.debug(p.getName());

					if(PlayerManager.getInstance().hasTipsActived(p) && isGameStarted() && TF.getInstance().getTips().size() > 0)
					{
						p.sendMessage("�8[�9TF2�8] �7(�bTIP�7) �r" + TF.getInstance().getTips().get(new Random().nextInt(TF.getInstance().getTips().size())));
						// TF.debug("tip launched");
					}
				}
			}
		}.runTaskTimer(TF.getInstance(), 1800, 1800);
	}

	public boolean isGameStarted()
	{
		return gameStarted;
	}

	@SuppressWarnings("deprecation")
	public void setStarted(boolean started)
	{
		gameStarted = started;

		if(started)
		{
			forceStart = false;

			if(w.getPlayers().size() < 1)
			{
				Utils.broadcastMessage(w, "�cPersonne n'est connect�, la partie ne se lance pas");
				gameStarted = false;
				return;
			}

			for(Player p : w.getPlayers())
			{
				playersTF.add(p.getUniqueId());
			}

			String cancelReason = "�cVeuillez d�finir ";

			try
			{

				if(lobbySpawnPoint == null)
					cancelReason = cancelReason + "le point d'apparition du lobby, ";

				// if(noTeamsSpawnPoints.size() < 1 && !gameType.areTeamsActive())
				// cancelReason = cancelReason + "des points d'apparitions pour les joueurs, ";

				if(gameType.areTeamsActive())
				{
					for(Team t : teams)
						if(t.getSpawnpoint() == null && gameType.areTeamsActive())
							cancelReason = cancelReason + "le point d'apparition " + t.getName(false) + ", ";

					for(Team t : teams)
					{
						if((t.getSafeZone().getLoc1().getX() == 0 && t.getSafeZone().getLoc1().getY() == 0 && t.getSafeZone().getLoc1().getZ() == 0) || (t.getSafeZone().getLoc2().getX() == 0 && t.getSafeZone().getLoc2().getY() == 0 && t.getSafeZone().getLoc2().getZ() == 0))
							cancelReason = cancelReason + "la SZ " + t.getName(true) + ", ";
					}

					for(Team t : teams)
						if(gameType.isCarts() && cm.getRails(t).size() < 1)
							cancelReason = cancelReason + "le chemin de rail " + t.getName(false) + ", ";
					
					if(gameType.isKoth() && zm.getZone() == null)
						cancelReason = cancelReason + "la zone de capture , ";
				}

				if(!cancelReason.equals("�cVeuillez d�finir "))
					cancelReason = cancelReason + "pour lancer la partie.";
			}catch(Exception e)
			{
				gameStarted = false;
				cancelReason = "�cUne erreur est survenue, la partie ne commence pas. (Avez vous tout bien configur� ?)";
				e.printStackTrace();
			}

			if(!cancelReason.equals("�cVeuillez d�finir "))
			{
				Utils.broadcastMessage(w, cancelReason);
				attenteSec = 60;

				gameStarted = false;
				return;
			}

			// START S�R

			if(gameType.isCarts())
				cm.onGameStart();

			if(gameType.isKoth())
				zm.onGameStart();

			Utils.broadcastMessage(w, "�8[�9TF2�8] �f" + "�aLa partie est lanc�e !");

			for(Player p : getPlayersOnline())
				giveTeam(p);

			startDate = System.currentTimeMillis();

			for(Player p : getPlayersOnline())
			{
				p.closeInventory();

				respawn(p);

				p.playSound(p.getLocation(), "jeu.fight", 1, 1);

				if(gameType.isSuperPowerMode())
				{
					PlayerManager.getInstance().giveRdPower(p);

					p.sendMessage("�8[�9TF2�8] �f" + "�eVotre pouvoir : �6" + PlayerManager.getInstance().getSP(p).getName());
				}
			}
		}

		else
		{
			endDate = System.currentTimeMillis();

			for(Player p : getPlayersOnline())
				StatsManager.getInstance().addTimePlayed(p, (int)(System.currentTimeMillis() - startDate) / 1000);

			StatsManager.getInstance().saveInFile();
		}
	}

	public List<Player> getPlayersOnline()
	{
		List<Player> players = new ArrayList<>();

		for(UUID uuid : playersTF)
			if(w.getPlayers().contains(Bukkit.getPlayer(uuid)))
				players.add(Bukkit.getPlayer(uuid));

		return players;
	}

	public List<UUID> getPlayers()
	{
		return playersTF;
	}

	public void setAttente(int attente)
	{
		attenteSec = attente;
	}

	public Team getTeamOf(Player p)
	{
		for(Team t : teams)
			if(t.getPlayers().contains(p.getUniqueId()))
				return t;

		return null;
	}

	public void giveTeam(Player p)
	{
//		TF.debug("--------"+p.getName()+"--------");
		
		if(getTeamOf(p) == null)
		{
			ligne: for(Team t : teams)
			{
				for(Team te : teams)
				{
					if(t.equals(te))
						continue;
					
//					TF.debug(t.getName(false)+" "+t.getPlayers().size());
//					TF.debug(te.getName(false)+" "+te.getPlayers().size());

					if(t.getPlayers().size() > te.getPlayers().size())
					{
						te.getPlayers().add(p.getUniqueId());
						break ligne;
					}
				}
			}
		}

		if(getTeamOf(p) == null)
		{
			teams.get(new Random().nextInt(teams.size())).getPlayers().add(p.getUniqueId());
		}

		for(Team t : teams)
		{
			for(Team te : teams)
			{
				if(t.getPlayers().size() - 1 > te.getPlayers().size())
				{
					Utils.broadcastMessage(w, "�8[�9TF2�8] �f" + "�eR��quilibrage des �quipes.");

					int rdIndex = new Random().nextInt(t.getPlayers().size());
					te.getPlayers().add(t.getPlayers().get(rdIndex));
					t.getPlayers().remove(rdIndex);
				}
			}
		}
	}

	public static GameManager getInstance(World w)
	{
		return TF.getInstance().getGM(w);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean damageTF(Player victim, Player damager, double damage, Vector kb)
	{
		return damageTF(victim, damager, damage, kb, victim.getLocation().clone().add(0, 1.9, 0));
	}

	public static boolean damageTF(Player victim, Player damager, double damage, Vector kb, Location damagePoint)
	{
		return damageTF(victim, damager, damage, kb, damagePoint, false);
	}

	@SuppressWarnings("deprecation")
	public static boolean damageTF(Player victim, Player damager, double damage, Vector kb, Location damagePoint, boolean thorns)
	{
		GameManager instance = getInstance(victim.getWorld());

		if(!instance.isGameStarted())
			return false;
		
		if(damager != null)
			if(PlayerManager.getInstance().isInvicible(victim) || !instance.playersTF.contains(victim.getUniqueId()) || victim.getGameMode().equals(GameMode.SPECTATOR) || victim.isDead() || instance.sameTeam(damager, victim))
				return false;

		if(instance.getTeamOf(victim).getSafeZone().hasInside(victim.getLocation().add(0, 0.6, 0)))
		{
			victim.getWorld().playEffect(damagePoint, Effect.HEART, 0);
			return false;
		}

		if(damager != null && PlayerManager.getInstance().getSP(damager) != null && PlayerManager.getInstance().getSP(damager).equals(SuperPower.DMG))
			damage *= 1.3;

		if(PlayerManager.getInstance().getSP(victim) != null && PlayerManager.getInstance().getSP(victim).equals(SuperPower.RESISTANCE))
			damage *= 0.7;

		if(damager != null && !thorns && PlayerManager.getInstance().getSP(victim) != null && PlayerManager.getInstance().getSP(victim).equals(SuperPower.CACTUS))
			damageTF(damager, victim, damage * 0.2, new Vector(), damager.getLocation().clone().add(0, 1.9, 0), true);

		victim.setVelocity(victim.getVelocity().add(kb));

		victim.damage(0);
		victim.setLastDamageCause(new EntityDamageEvent(victim, DamageCause.VOID, damage));

		Utils.playSound(victim.getLocation(), "player.shot", 20);

		if(damager != null)
			StatsManager.getInstance().addDamage(damager, damage);

		if(victim.getHealth() <= damage && !victim.isDead())
		{
			if(damager != null)
				PlayerManager.getInstance().addDamagerTo(victim, damager);

			if(instance.getGameType().isTDM() && damager != null)
			{
				instance.getTeamOf(damager).addKill(1);

				instance.recalculateKillsToWin();

				if(instance.getTeamOf(damager).getKills() >= instance.getKillsToWin())
				{
					instance.victory(instance.getTeamOf(damager));
					return true;
				}
			}

			doDeathEffect(victim, (damager == null ? victim : damager), instance);
		}

		else
		{
			if(damager != null)
				PlayerManager.getInstance().addDamagerTo(victim, damager);

			victim.setHealth(victim.getHealth() - damage);
		}

		return true;////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	public Location getLobbySpawnPoint()
	{
		return lobbySpawnPoint;
	}

	public void setLobbySpawnPoint(Location lobbySpawnPoint)
	{
		// ConfigurationSection f = TF.getInstance().getConfig(w);
		//
		// f.set("lobbySpawnPoint", Utils.locToString(lobbySpawnPoint));
		//
		// TF.getInstance().saveTheConfig(f, w.getName());

		this.lobbySpawnPoint = lobbySpawnPoint;
	}

	public void forceStart()
	{
		forceStart = true;
	}

	public int getMinToStart()
	{
		return minToStart;
	}

	public long getTimeStarted()
	{
		if(startDate == 0)
			return 0;

		else if(gameStarted == false)
			return endDate - startDate;

		else
			return(System.currentTimeMillis() - startDate);
	}

	public void setMinToStart(int minToStart)
	{
		// ConfigurationSection f = TF.getInstance().getConfig(w);
		//
		// f.set("minToStart", minToStart);
		//
		// TF.getInstance().saveTheConfig(f, w.getName());

		this.minToStart = minToStart;
	}

	public static void doDeathEffect(Player victim, Player killer, GameManager instance)
	{
		death(victim);
		Utils.playSound(victim.getLocation(), "player.mort", 30);

		victim.setGameMode(GameMode.SPECTATOR);
		victim.getInventory().clear();
		victim.updateInventory();

		Location deathLoc = victim.getLocation().clone();

		new BukkitRunnable()
		{
			int i = 0;

			@Override
			public void run()
			{
				if(i >= 60)
				{
					victim.setGameMode(GameMode.ADVENTURE);
					respawn(victim);
					victim.setHealth(victim.getMaxHealth());

					cancel();
					return;
				}

				Title.sendTitle(victim, 0, 2, 0, "�6" + (3 - i / 20));

				victim.teleport(deathLoc.setDirection(killer.getLocation().toVector().subtract(victim.getLocation().toVector())));
				i++;
			}
		}.runTaskTimer(TF.getInstance(), 1, 1);
	}

	public static void death(Player p)
	{
		WeaponManager.getInstance().deleteUltiFinished(p);

		List<Player> killers = PlayerManager.getInstance().getLastDamagersOf(p);

		p.closeInventory();

		PlayerManager.getInstance().setDisguise(p, null);
		PlayerManager.getInstance().setIsInvisible(p, false);

		if(killers == null || killers.isEmpty())
		{
			Utils.broadcastMessage(p.getWorld(), "�8[�9TF2�8] " + getInstance(p.getWorld()).getTeamOf(p).getPrefix() + p.getName() + " " + PlayerManager.getInstance().kitOf(p).getSymbole() + " �7est mort");
			return;
		}

		if(TF.getInstance().getGM(p.getWorld()).isGameStarted())
		{
			String deathMsg = "�8[�9TF2�8] " + getInstance(p.getWorld()).getTeamOf(p).getPrefix() + p.getName() + " " + PlayerManager.getInstance().kitOf(p).getSymbole() + " �7a �t� tu� par �f";

			for(Player killer : killers)
			{
				deathMsg = deathMsg + getInstance(p.getWorld()).getTeamOf(killer).getPrefix() + killer.getName() + " " + PlayerManager.getInstance().kitOf(killer).getSymbole() + "�7 , ";
			}

			deathMsg = deathMsg + "##";

			deathMsg = deathMsg.replaceAll(" \\, \\#\\#", "");
			deathMsg = deathMsg.replaceAll("a �t� tu� par �f\\#\\#", "est mort");

			Utils.broadcastMessage(p.getWorld(), deathMsg);

			for(Player killer : killers)
			{
				if(WeaponManager.getInstance().mustReloadUlti(killer))
					WeaponManager.getInstance().startReloadUlti(killer);

				StatsManager.getInstance().addKill(killer, 1);
			}

			StatsManager.getInstance().addDeath(p);

			PlayerManager.getInstance().rmDamagersTo(p);

			PlayerManager.getInstance().explodeThingsLikeMineOrTurretOf(p);
		}
	}

	public static void respawn(Player p)
	{
		if(TF.getInstance().getListener().randomKit.get(p) != null && TF.getInstance().getListener().randomKit.get(p))
		{
			Kit lastKit = PlayerManager.getInstance().kitOf(p);

			do
				PlayerManager.getInstance().setNextKit(p, Kit.values()[new Random().nextInt(Kit.values().length)]);
			while(!PlayerManager.getInstance().nextKit(p).isReal() && !lastKit.equals(PlayerManager.getInstance().nextKit(p)));
		}

		if(!PlayerManager.getInstance().nextKit(p).equals(Kit.NOKIT))
		{
			PlayerManager.getInstance().setKitOf(p, PlayerManager.getInstance().nextKit(p));
		}

		if(PlayerManager.getInstance().kitOf(p).equals(Kit.NOKIT))
		{
			do
			{
				PlayerManager.getInstance().setKitOf(p, Kit.values()[new Random().nextInt(Kit.values().length)]);
			}while(!PlayerManager.getInstance().kitOf(p).isReal());
		}

		PlayerManager.getInstance().changeKit(p, PlayerManager.getInstance().kitOf(p));

		PlayerManager.getInstance().setLooking(p, false);
		PlayerManager.getInstance().setLookingHeavy(p, false);

		TF.getInstance().getPlayerManager().setArmorTo(p);
		
		if(getInstance(p.getWorld()).getGameType().areTeamsActive())
			p.teleport(getInstance(p.getWorld()).getTeamOf(p).getSpawnpoint());

		else
			p.teleport(p);// TODO NO TEAMS

		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());

		for(Player pl : p.getWorld().getPlayers())
			pl.showPlayer(p);

		if(getInstance(p.getWorld()).getPlayersOnline().contains(p))
			p.setGameMode(GameMode.ADVENTURE);

		if(TF.getInstance().getListener().wentOutSZ.get(p) != null)
			for(Team t : TF.getInstance().getListener().wentOutSZ.get(p).keySet())
				TF.getInstance().getListener().wentOutSZ.get(p).put(t, false);

		TF.getInstance().getListener().reloadDoubleJump(p, true);
	}

	@SuppressWarnings("deprecation")
	public void victory(Team team)
	{
		if(!isGameStarted())
			return;

		for(Team t : teams)
		{
			PotionEffectType effect = new PotionEffectType(t.equals(team) ? PotionEffectType.SPEED.getId() : PotionEffectType.SLOW.getId())
			{
				@Override
				public boolean isInstant()
				{
					return false;
				}

				@Override
				public String getName()
				{
					return null;
				}

				@Override
				public double getDurationModifier()
				{
					return 0;
				}
			};

			for(Player p : t.getPlayersOnline())
			{
				p.addPotionEffect(new PotionEffect(effect, 99999, 10));
			}
		}

		if(gameType.isCarts())
		{
			Minecart cart = cm.getCart(team);

			for(Player p : cart.getWorld().getPlayers())
			{
				if(!getTeamOf(p).equals(team) && p.getLocation().distance(cart.getLocation()) < 50)
				{
					p.setVelocity(Utils.explosionVector(p, cart.getLocation(), 100).multiply(0.5));
				}
			}

			Utils.playSound(cart.getLocation(), "mortier.obus", 10000);

			for(int i = 0; i < 1300; i++)
				cart.getWorld().playEffect(cart.getLocation(), Effect.FLAME, 0);

			for(int i = 0; i < 1300; i++)
				cart.getWorld().playEffect(cart.getLocation().clone().add((((double)new Random().nextInt(200)) / 100.0) - 1, (((double)new Random().nextInt(200)) / 100.0) - 1, (((double)new Random().nextInt(200)) / 100.0) - 1), Effect.LARGE_SMOKE, 0);

			for(int i = 0; i < 1300; i++)
				cart.getWorld().playEffect(cart.getLocation().clone().add((((double)new Random().nextInt(200)) / 100.0) - 1, (((double)new Random().nextInt(200)) / 100.0) - 1, (((double)new Random().nextInt(200)) / 100.0) - 1), Effect.CLOUD, 0);

			for(int i = 0; i < 50; i++)
				cart.getWorld().playEffect(cart.getLocation().clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

			cart.remove();
		}

		List<Player> bestKillers = new ArrayList<>();
		Player bestPusher = null;
		Player bestCap = null;
		Player bestDamager = null;
		Player bestKDR = null;
		
		StatsManager sm = StatsManager.getInstance();

		for(Player p : w.getPlayers())
		{
			if(bestKillers.isEmpty())
				bestKillers.add(p);
			
			else if(sm.getKillsOf(p) > sm.getKillsOf(bestKillers.get(0)))
			{
				bestKillers.clear();
				bestKillers.add(p);
			}
			
			else if(sm.getKillsOf(p) == sm.getKillsOf(bestKillers.get(0)))
				bestKillers.add(p);
			
			if(bestKDR == null || sm.getKDR(p) > sm.getKDR(bestKDR))
				bestKDR = p;
			
			if(gameType.isCarts())
				if(bestPusher == null || sm.getPushSecOf(p) > sm.getPushSecOf(bestPusher))
					bestPusher = p;
			
			if(gameType.isKoth())
				if(bestCap == null || sm.getCapSecOf(p) > sm.getCapSecOf(bestCap))
					bestCap = p;

			if(bestDamager == null || sm.getDamageOf(p) > sm.getDamageOf(bestDamager))
				bestDamager = p;
		}

		String min = ((int)(System.currentTimeMillis() - startDate) / 1000) / 60 + " minute" + (((int)(System.currentTimeMillis() - startDate) / 1000) / 60 > 1 ? "s" : "");
		String sec = ((int)(System.currentTimeMillis() - startDate) / 1000) % 60 + " seconde" + (((int)(System.currentTimeMillis() - startDate) / 1000) % 60 > 1 ? "s" : "");

		Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�6Victoire de l'�quipe " + team.getPrefix() + team.getCharFR().toUpperCase() + team.getName(true).substring(1) + " �5F�licitations !");
		
		for(Player p : w.getPlayers())
			if(getTeamOf(p).equals(team))
				p.sendMessage("�dLegendaryCoins �7+�62.00 Victory");
		
		Utils.broadcastMessage(w, "�8[�9TF2�8] ");

		Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�9Dur�e de la partie : �6" + min + " �9et �6" + sec + " �9!");

		if(gameType.isCarts())
			Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�9Meilleur pusher : �6" + bestPusher.getName() + "�9 avec �6" + sm.getPushSecOf(bestPusher) + " secondes �9!");

		if(gameType.isKoth())
			Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�9Meilleur capture : �6" + bestCap.getName() + "�9 avec �6" + sm.getCapSecOf(bestCap) + " secondes �9!");

		String meilleursKillers = "�9Meilleur"+(bestKillers.size() > 1 ? "s" : "")+" killer"+(bestKillers.size() > 1 ? "s" : "")+" : ";
		
		for(Player p : bestKillers)
			meilleursKillers = meilleursKillers + "�6"+p.getName()+"�9, ";
		
		meilleursKillers = meilleursKillers + "##";
		
		meilleursKillers = meilleursKillers.replaceAll(", \\#\\#", "");
		
		Utils.broadcastMessage(w, "�8[�9TF2�8] " + meilleursKillers + "�9 avec �6" + sm.getKillsOf(bestKillers.get(0)) + " kills �9!");
		Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�9Meilleur damager : �6" + bestDamager.getName() + "�9 avec �6" + Math.round(sm.getDamageOf(bestDamager)) + "�9 d�gats inflig�s !");
		Utils.broadcastMessage(w, "�8[�9TF2�8] " + "�9Meilleur KDR : �6" + bestKDR.getName() + "�9 avec �6" + sm.getKDR(bestKDR) + "�9 !");

		Utils.broadcastMessage(w, "�8[�9TF2�8] ");

		for(Player p : w.getPlayers())
		{
			if(gameType.isCarts())
				p.sendMessage("\n" + "�8[�9TF2�8] " + "�9Votre push : �6" + sm.getPushSecOf(p) + " secondes");
			
			if(gameType.isKoth())
				p.sendMessage("\n" + "�8[�9TF2�8] " + "�9Votre capture : �6" + sm.getCapSecOf(p) + " secondes");

			p.sendMessage("�8[�9TF2�8] " + "�9Vos kills : �6" + sm.getKillsOf(p) + " kills");
			p.sendMessage("�8[�9TF2�8] " + "�9Vos d�gats : �6" + Math.round(sm.getDamageOf(p)) + " d�gats inflig�s");
		}

		setStarted(false);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player p : getPlayersOnline())
				{
					if(getTeamOf(p).equals(team))
					{
						Title.sendTitle(p, 10, 20, 200, "�aVICTOIRE");
						p.playSound(p.getLocation(), "jeu.victoire", 1, 1);
					}

					else
					{
						Title.sendTitle(p, 10, 20, 200, "�cDEFAITE");
						p.playSound(p.getLocation(), "jeu.defaite", 1, 1);
					}
				}
			}
		}.runTaskLater(TF.getInstance(), 5);

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				TF.getInstance().reinitializeGameManager(getInstance(w));
			}
		}.runTaskLater(TF.getInstance(), 200);
	}

	public World getWorld()
	{
		return w;
	}

	public void delete()
	{
		for(Player p : w.getPlayers())
			PlayerManager.getInstance().deleteThingsLikeMineOrTurretOf(p);

		// for(Team t : teams)
		// t.delete();

		delete = true;

		if(cm != null)
			cm.delete();

		if(zm != null)
			zm.delete();
		
		w = null;
		cm = null;
		zm = null;
	}

	public GameType getGameType()
	{
		return gameType;
	}

	public void setType(GameType gt)
	{
		gameType = gt;

		TF.getInstance().reinitializeGameManager(this);
	}

	public Team getTeamByName(String name)
	{
		for(Team t : teams)
			if(t.getName(false).equalsIgnoreCase(name))
				return t;

		return null;
	}

	public Team getTeamByLetter(String letter)
	{
		for(Team t : teams)
			if(t.getCharFR().equalsIgnoreCase(letter))
				return t;

		return null;
	}

	public List<Team> getTeams()
	{
		return teams;
	}

	public boolean sameTeam(Player p1, Player p2)
	{
		if(getTeamOf(p1) != null && getTeamOf(p2) != null)
			return getTeamOf(p1).equals(getTeamOf(p2));

		else
			return false;
	}

	public void recalculateKillsToWin()
	{
		for(Team t : teams)
		{
			for(Team te : teams)
			{
				if(t.equals(te))
					continue;

				if(t.getKills() - 5 < te.getKills() && t.getKills() + 5 > killsToWin)
				{
					Utils.broadcastMessage(w, "�8[�9TF2�8] �f" + "�eLes scores �tants trop serr�s, le maximum de kills � r�aliser augmente de 5 !");

					killsToWin += 5;
				}
			}
		}
	}

	public int getKillsToWin()
	{
		return killsToWin;
	}

	public void saveInConfig()
	{
		if(w == null)
			return;
		
		ConfigurationSection f = TF.getInstance().getConfig(w);

		if(cm != null)
			cm.saveInConfig();

		if(zm != null)
			zm.saveInConfig();

		for(Team t : teams)
			t.saveInConfig();

		if(lobbySpawnPoint != null)
			f.set("lobbySpawnPoint", Utils.locDirToString(lobbySpawnPoint));

		if(gameType != null)
			f.set("gametype", gameType.getName());

		TF.getInstance().saveTheConfig(f, w);
	}

	public CartManager getCartManager()
	{
		return cm;
	}

	public ZoneManager getZoneManager()
	{
		return zm;
	}
	
	public int getId()
	{
		return id;
	}
}
