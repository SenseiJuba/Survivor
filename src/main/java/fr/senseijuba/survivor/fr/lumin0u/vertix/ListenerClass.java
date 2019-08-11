package fr.lumin0u.vertix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Button;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.commands.TfCommand;
import fr.lumin0u.vertix.managers.CartManager;
import fr.lumin0u.vertix.managers.CartRunnable;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.managers.ZoneManager;
import fr.lumin0u.vertix.utils.Cuboid;
import fr.senseijuba.survivor.utils.Title;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.AbstractWeapon;
import fr.lumin0u.vertix.weapons.Barbecue;
import fr.lumin0u.vertix.weapons.Something;
import fr.lumin0u.vertix.weapons.WeaponManager;
import fr.lumin0u.vertix.weapons.blocks.AbstractBlock;
import fr.lumin0u.vertix.weapons.blocks.C4;
import fr.lumin0u.vertix.weapons.blocks.CanonMontable;
import fr.lumin0u.vertix.weapons.blocks.Mine;
import fr.lumin0u.vertix.weapons.blocks.Trampoline;
import fr.lumin0u.vertix.weapons.corpsACorps.AbstractCorpsACorpsWeapon;
import fr.lumin0u.vertix.weapons.corpsACorps.Batte;
import fr.lumin0u.vertix.weapons.corpsACorps.CleMolette;
import fr.lumin0u.vertix.weapons.corpsACorps.Poignard;
import fr.lumin0u.vertix.weapons.guns.FuseeDeDetresse;
import fr.lumin0u.vertix.weapons.guns.LaTornade;
import fr.lumin0u.vertix.weapons.guns.Sniper;
import fr.lumin0u.vertix.weapons.launchableItem.AbstractLaunchableItem;
import fr.lumin0u.vertix.weapons.notDangerous.Manette;
import fr.lumin0u.vertix.weapons.notDangerous.MedecinePortable;
import fr.lumin0u.vertix.weapons.thingsLauncher.AbstractThingLauncher;
import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.corpsACorps.AbstractUltimateCorpsACorpsWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.Disguise;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.AbstractUltimateThingLauncher;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.CoktailMolotov;

public class ListenerClass implements Listener
{
	public HashMap<UUID, Long> rClickingPlayersLast;
	public HashMap<UUID, BukkitRunnable> rClickingPlayersTask;
	public HashMap<UUID, Long> timeLastClick;
	public HashMap<UUID, Integer> rClickingPlayersTaskId;
	private HashMap<Player, Boolean> canDoubleJump;
	public HashMap<Player, Boolean> randomKit;
	public HashMap<Player, HashMap<Team, Boolean>> wentOutSZ;
	private PlayerManager pm = PlayerManager.getInstance();

	public ListenerClass()
	{
		rClickingPlayersLast = new HashMap<>();
		rClickingPlayersTask = new HashMap<>();
		timeLastClick = new HashMap<>();
		rClickingPlayersTaskId = new HashMap<>();
		canDoubleJump = new HashMap<>();
		randomKit = new HashMap<>();
		wentOutSZ = new HashMap<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onAnimation(PlayerAnimationEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(e.getAnimationType().equals(PlayerAnimationType.ARM_SWING) && !e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
		{
			if(e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new MedecinePortable().getItem(1)) && e.getPlayer().getItemInHand().getAmount() > 0 && GameManager.getInstance(e.getPlayer().getWorld()).isGameStarted() && !GameManager.getInstance(e.getPlayer().getWorld()).getTeamOf(e.getPlayer()).getSafeZone().hasInside(e.getPlayer()))
			{
				new MedecinePortable().effect(e.getPlayer(), e.getPlayer());
				WeaponManager.getInstance().reload(e.getPlayer(), new MedecinePortable());
				Utils.playSound(e.getPlayer().getLocation(), new MedecinePortable().getSound(), new MedecinePortable().getMaxDistance());
			}

			for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
			{
				if(w instanceof AbstractCorpsACorpsWeapon && Utils.areSimilar(w.getItem(1), e.getPlayer().getItemInHand()) && !pm.getTFPlayer(e.getPlayer()).isInvisible())
				{
					Utils.playSound(e.getPlayer().getLocation(), w.getSound(), w.getMaxDistance());
				}
			}

			for(AbstractUltimateWeapon w : TF.getInstance().getWeaponManager().listUltimateWeapons())
			{
				if(w instanceof AbstractUltimateCorpsACorpsWeapon && Utils.areSimilar(w.getItem(1), e.getPlayer().getItemInHand()))
				{
					Utils.playSound(e.getPlayer().getLocation(), w.getSound(), w.getMaxDistance());
				}
			}

			PotionEffectType pet = new PotionEffectType(PotionEffectType.SLOW.getId())
			{

				@Override
				public boolean isInstant()
				{
					return false;
				}

				@Override
				public String getName()
				{
					return "Lenteur";
				}

				@Override
				public double getDurationModifier()
				{
					return 0;
				}
			};

			if(!pm.getTFPlayer(e.getPlayer()).isLooking() && e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new Sniper().getItem(1)))
			{
				e.getPlayer().getInventory().setHelmet(new ItemStack(Material.PUMPKIN));

				e.getPlayer().addPotionEffect(new PotionEffect(pet, 254, 4, true, false));

				pm.getTFPlayer(e.getPlayer()).setLooking(true);
				
				e.getPlayer().updateInventory();

				return;
			}

			else if(pm.getTFPlayer(e.getPlayer()).isLooking() && e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new Sniper().getItem(1)))
			{
				e.getPlayer().getInventory().setHelmet(pm.getTFPlayer(e.getPlayer()).getKit().getBlockOnHead());
				e.getPlayer().removePotionEffect(pet);
				pm.getTFPlayer(e.getPlayer()).setLooking(false);
				
				e.getPlayer().updateInventory();
			}

			if(!pm.getTFPlayer(e.getPlayer()).isLookingHeavy() && e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new LaTornade().getItem(1)))
			{
				e.getPlayer().addPotionEffect(new PotionEffect(pet, 10000, 1, false, false));

				e.getPlayer().getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				e.getPlayer().updateInventory();

				pm.getTFPlayer(e.getPlayer()).setLookingHeavy(true);

				return;
			}

			else if(pm.getTFPlayer(e.getPlayer()).isLookingHeavy() && e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new LaTornade().getItem(1)))
			{
				e.getPlayer().removePotionEffect(pet);

				e.getPlayer().getItemInHand().removeEnchantment(Enchantment.DAMAGE_ALL);
				e.getPlayer().updateInventory();

				pm.getTFPlayer(e.getPlayer()).setLookingHeavy(false);
			}

			if(e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new Manette().getItem(1)))
			{
				new CanonMontable().shoot(e.getPlayer());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void interactEvent(PlayerInteractEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		CartManager cm = CartManager.getInstance(e.getPlayer().getWorld());
		GameManager gm = GameManager.getInstance(e.getPlayer().getWorld());

		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			for(Team t : gm.getTeams())
			{
				if(!t.isModifyingFinalTerminus(e.getPlayer()))
					continue;

				if(cm.setFinalTerminus(t, e.getClickedBlock().getLocation()) < 0)
				{
					e.getPlayer().sendMessage("�cCe rail ne fait pas partie du chemin de rail " + t.getName(false));
				}

				else
				{
					t.setFinalTerminusModifier(null);
					e.getPlayer().sendMessage("�aFinal terminus " + t.getName(false) + " d�fini !");
				}
			}
		}

		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			if(e.getItem() != null)
			{
				if(Utils.areSimilar(e.getItem(), Kit.boussole(e.getPlayer())) && cm != null)
				{
					e.getPlayer().setItemInHand(null);
					e.getPlayer().updateInventory();

					Team cartDirection = pm.getTFPlayer(e.getPlayer()).getTeamCartDirection();

					if(gm.getTeams().size() < gm.getTeams().indexOf(cartDirection) + 2)
						pm.getTFPlayer(e.getPlayer()).setTeamCartDirection(gm.getTeams().get(0));

					else
						pm.getTFPlayer(e.getPlayer()).setTeamCartDirection(gm.getTeams().get(gm.getTeams().indexOf(cartDirection) + 1));

					e.getPlayer().setItemInHand(Kit.boussole(e.getPlayer()));

					if(CartManager.getInstance(e.getPlayer().getWorld()) != null && CartManager.getInstance(e.getPlayer().getWorld()).areCartsCreated())
						e.getPlayer().setCompassTarget(CartManager.getInstance(e.getPlayer().getWorld()).getCart(pm.getTFPlayer(e.getPlayer()).getTeamCartDirection()).getLocation());

					e.getPlayer().updateInventory();
				}
			}

			if(!TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted() || e.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
				return;

			final UUID pid = e.getPlayer().getUniqueId();
			rClickingPlayersLast.put(pid, System.currentTimeMillis());

			long differenceTempsNonFinal = 300;

			if(timeLastClick.get(pid) == null)
			{
				timeLastClick.put(pid, System.currentTimeMillis());
			}

			else if(System.currentTimeMillis() - timeLastClick.get(pid) > 600 || System.currentTimeMillis() - timeLastClick.get(pid) < 110)
			{
				timeLastClick.replace(pid, System.currentTimeMillis());
			}

			else
			{
				differenceTempsNonFinal = System.currentTimeMillis() - timeLastClick.get(pid);
				timeLastClick.replace(pid, System.currentTimeMillis());
			}

			final long differenceTemps = differenceTempsNonFinal;

			if(!rClickingPlayersTask.containsKey(pid) || rClickingPlayersTask.get(pid).getTaskId() > 0)
			{
				if(e.getItem() != null && !e.getPlayer().getGameMode().equals(GameMode.SPECTATOR) && !gm.getTeamOf(e.getPlayer()).getSafeZone().hasInside(e.getPlayer()))
				{
					for(Something s : TF.getInstance().getWeaponManager().listThings())
					{
						if(Utils.areSimilar(s.getItem(1), e.getItem()))
						{
							WeaponManager.getInstance().doEffectAndReload(e.getItem(), e.getPlayer(), pid, differenceTemps);
							e.setCancelled(true);
							break;
						}
					}
				}
			}

			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !gm.getTeamOf(e.getPlayer()).getSafeZone().hasInside(e.getPlayer()))
			{
				for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
				{
					if(w instanceof AbstractBlock && Utils.areSimilar(w.getItem(1), e.getItem()) && e.getItem().getAmount() > 0)
					{
						Location l = new Location(e.getPlayer().getWorld(), e.getBlockFace().getModX() + e.getClickedBlock().getLocation().getX() + 0.5, e.getBlockFace().getModY() + e.getClickedBlock().getLocation().getY(), e.getBlockFace().getModZ() + e.getClickedBlock().getLocation().getZ() + 0.5);

						if(!l.getBlock().getType().equals(Material.AIR))
							break;
						
						if(w instanceof C4)
						{
							pm.getTFPlayer(e.getPlayer()).setC4Location(l);

							l.getBlock().setType(((AbstractBlock)w).getBlock());
							
							BlockState state = l.getBlock().getState();
							Button b = (Button)state.getData();
							b.setFacingDirection(e.getBlockFace());
							state.setData(b);
							state.update();

							((AbstractBlock)w).onPlaceBlock(e.getPlayer(), e.getItem());

							Utils.playSound(l, w.getSound(), w.getMaxDistance());
							
							break;
						}

						if(w instanceof CanonMontable && !l.clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR))
						{
							((AbstractBlock)w).onPlaceBlock(e.getPlayer(), e.getItem());
							((CanonMontable)w).build(e.getPlayer(), l);
	
							l.getBlock().setType(((AbstractBlock)w).getBlock());
	
							Utils.playSound(l, w.getSound(), w.getMaxDistance());
						}
						
						if(!transparent().contains(l.clone().add(0, -1, 0).getBlock().getType()))
						{
							((AbstractBlock)w).onPlaceBlock(e.getPlayer(), e.getItem());
	
							l.getBlock().setType(((AbstractBlock)w).getBlock());
	
							Utils.playSound(l, w.getSound(), w.getMaxDistance());
						}
						
						if(w instanceof Mine && !transparent().contains(l.clone().add(0, -1, 0).getBlock().getType()))
						{
							List<Location> list = pm.getTFPlayer(e.getPlayer()).getMineLocations();
							list.add(l.getBlock().getLocation());
							pm.getTFPlayer(e.getPlayer()).setMineLocations(list);
						}

						if(w instanceof Trampoline && !transparent().contains(l.clone().add(0, -1, 0).getBlock().getType()))
						{
							pm.getTFPlayer(e.getPlayer()).setTrampoLocation(l.getBlock().getLocation());

							if(GameManager.getInstance(e.getPlayer().getWorld()).getTeamOf(e.getPlayer()).getName(false).equals("rouge"))
								e.getPlayer().getWorld().getBlockAt(l).setType(Material.IRON_PLATE);

							else if(GameManager.getInstance(e.getPlayer().getWorld()).getTeamOf(e.getPlayer()).getName(false).equals("bleu"))
								e.getPlayer().getWorld().getBlockAt(l).setType(Material.GOLD_PLATE);
						}
					}

					if(w instanceof CleMolette && Utils.areSimilar(w.getItem(1), e.getItem()))
					{
						if(e.getClickedBlock().getType().equals(Material.CARPET) && e.getClickedBlock().getData() == 0 && e.getClickedBlock().getLocation().equals(pm.getTFPlayer(e.getPlayer()).getTurretLocation()))
						{
							if(new CanonMontable().removeCanon(e.getPlayer()))
							{
								e.getPlayer().getInventory().remove(Material.WHEAT);
								e.getPlayer().getInventory().addItem(new CanonMontable().getItem(1));
							}
						}

						else if((e.getClickedBlock().getType().equals(Material.GOLD_PLATE) || e.getClickedBlock().getType().equals(Material.IRON_PLATE)) && e.getClickedBlock().getLocation().equals(pm.getTFPlayer(e.getPlayer()).getTrampoLocation()))
						{
							e.getClickedBlock().setType(Material.AIR);
							e.getPlayer().getInventory().addItem(new Trampoline().getItem(1));
							pm.getTFPlayer(e.getPlayer()).setTrampoLocation(null);
						}

						else if(e.getClickedBlock().getType().equals(Material.STONE_PLATE) && pm.getTFPlayer(e.getPlayer()).getMineLocations().contains(e.getClickedBlock().getLocation()))
						{
							e.getClickedBlock().setType(Material.AIR);
							e.getPlayer().getInventory().addItem(new Mine().getItem(1));

							List<Location> list = pm.getTFPlayer(e.getPlayer()).getMineLocations();
							list.remove(e.getClickedBlock().getLocation());
							pm.getTFPlayer(e.getPlayer()).setMineLocations(list);
						}

						e.getPlayer().updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void clickEvent(PlayerInteractEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			// items � droite

			if(e.getItem() != null && Utils.areSimilar(e.getItem(), Kit.nameTag()))
			{
				e.getPlayer().openInventory(Kit.getChangeKitInventory(e.getPlayer()));
			}
		}
	}

	@EventHandler
	public void jump(PlayerMoveEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(e.getFrom().getY() < e.getTo().getY() && pm.getTFPlayer(e.getPlayer()).isLooking() && e.getPlayer().getVelocity().getY() > 0.05)
		{
			e.getPlayer().setVelocity(e.getPlayer().getVelocity().multiply(0.5));
		}

		if(e.getPlayer().getGameMode().equals(GameMode.SPECTATOR) || e.getPlayer().getGameMode().equals(GameMode.CREATIVE) || pm.getTFPlayer(e.getPlayer()).getKit().equals(Kit.SCOUT) && (canDoubleJump.get(e.getPlayer()) == null || canDoubleJump.get(e.getPlayer())))// verifier
			e.getPlayer().setAllowFlight(true);																																																									   // fly
																																																																					   // scout
		else																																																																	   // double
			e.getPlayer().setAllowFlight(false);																																																								   // jump
																																																																				   // tout
		// // ca
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		GameManager gm = GameManager.getInstance(e.getPlayer().getWorld());

		Player p = e.getPlayer();
		
		if(!wentOutSZ.containsKey(p))
			wentOutSZ.put(p, new HashMap<>());
		
		if(gm.isGameStarted())
		{
			for(Team t : gm.getTeams())
			{
				if(!wentOutSZ.get(p).containsKey(t))
					wentOutSZ.get(p).put(t, false);
				
				if(t.getSafeZone().hasInside(e.getTo().clone().add(0, 0.6, 0)) && wentOutSZ.get(p).get(t))
				{
					e.setCancelled(true);
				}
				
				if(!t.getSafeZone().hasInside(e.getTo().clone().add(0, 0.6, 0)))
				{
					wentOutSZ.get(p).put(t, true);
				}
				
				float r = ((float)t.getDyeColor().getColor().getRed())/255.0f;
				float g = ((float)t.getDyeColor().getColor().getGreen())/255.0f;
				float b = ((float)t.getDyeColor().getColor().getBlue())/255.0f;
				
				if(wentOutSZ.get(p).get(t) && p.getLocation().distance(t.getSafeZone().midpoint()) < t.getSafeZone().getLoc1().distance(t.getSafeZone().getLoc2())/3+10)
				{
					int max = 50;
					
					Random rd = new Random();
					
					for(int i = 0; i < max; i++)
					{
						double nb1 = rd.nextDouble();
						double nb2 = rd.nextDouble();
						
						Location toPlay = p.getEyeLocation().add(Utils.vectorFrom(p.getEyeLocation(), p.getEyeLocation().add(Utils.rdVector().multiply(rd.nextDouble()*10).multiply(nb1 % nb2))));//TODO
						
						if(t.getSafeZone().hasInside(toPlay))
						{
							p.getWorld().spigot().playEffect(toPlay, Effect.COLOURED_DUST, 0, 0, r, g, b, 1, 0, 100);
						}
						
						else if(max < 500)
							max++;
					}
				}
			}
		}
		
		for(Player pl : gm.getPlayersOnline())
		{
			if(gm.sameTeam(pl, p))
				continue;
			
			if(pm.getTFPlayer(pl).getpower() != null && pm.getTFPlayer(pl).getpower().equals(SuperPower.AIMBOT))// || pm.isLooking(pl))//AIMBOT
			{
				Vector velo = pl.getVelocity();
				
				Location plLastEyePos = pl.getEyeLocation().getDirection().toLocation(gm.getWorld()).clone().add(pl.getLocation());
				Location plNowEyePos = p.getLocation().add(0, -0.6, 0).toVector().subtract(pl.getLocation().toVector()).normalize().toLocation(gm.getWorld()).clone().add(pl.getLocation());
				
				if(plLastEyePos.distance(plNowEyePos) < 0.05 && Utils.pointsBetween(p.getEyeLocation(), pl.getEyeLocation(), null).size() < 2)
				{
					pl.teleport(pl.getLocation().setDirection(p.getLocation().add(0, -0.6, 0).toVector().subtract(pl.getLocation().toVector())));
					pl.setVelocity(velo);
				}
			}
		}
		
		if(TfCommand.debug)
		{
			Location point = p.getEyeLocation();
			
			for(int i = 0; i < 50; i++)
			{
				point.add(p.getEyeLocation().getDirection().multiply(0.1));
				
				if(!point.getBlock().getType().equals(Material.AIR))
				{
					Title.sendTitle(p, 0, 20, 0, Utils.locToString(point.getBlock().getLocation()));
					break;
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(!TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted())
			return;

		else if(TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted())
			e.setCancelled(true);

		for(AbstractWeapon g : TF.getInstance().getWeaponManager().listWeapons())
		{
			if(Utils.areSimilar(e.getItemDrop().getItemStack(), g.getItem(1)))
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						WeaponManager.getInstance().reload(e.getPlayer(), g);
					}
				}.runTaskLater(TF.getInstance(), 2);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onScroll(PlayerItemHeldEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(pm.getTFPlayer(e.getPlayer()).isLooking() || pm.getTFPlayer(e.getPlayer()).isLookingHeavy())
		{
			e.getPlayer().removePotionEffect(new PotionEffectType(2)
			{

				@Override
				public boolean isInstant()
				{
					return false;
				}

				@Override
				public String getName()
				{
					return "Lenteur";
				}

				@Override
				public double getDurationModifier()
				{
					return 0;
				}
			});
		}
		
		pm.getTFPlayer(e.getPlayer()).setLooking(false);
		pm.getTFPlayer(e.getPlayer()).setLookingHeavy(false);

		if(e.getPlayer().getInventory().getItem(e.getPreviousSlot()) != null && Utils.areSimilar(e.getPlayer().getInventory().getItem(e.getPreviousSlot()), new LaTornade().getItem(1)))
		{
			e.getPlayer().getInventory().remove(e.getPreviousSlot());
			e.getPlayer().getInventory().setItem(e.getPreviousSlot(), LaTornade.getItem(1, false));
		}

		if(pm.getTFPlayer(e.getPlayer()).getDisguised().equals(e.getPlayer()))
			e.getPlayer().getInventory().setHelmet(pm.getTFPlayer(e.getPlayer()).getKit().getBlockOnHead());

		e.getPlayer().updateInventory();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClickOnEntity(PlayerInteractAtEntityEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(!TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted())
			return;

		if(e.getRightClicked() != null)
		{
			final UUID pid = e.getPlayer().getUniqueId();
			rClickingPlayersLast.put(pid, System.currentTimeMillis());

			long differenceTempsNonFinal = 300;

			if(timeLastClick.get(pid) == null)
			{
				timeLastClick.put(pid, System.currentTimeMillis());
			}

			else if(System.currentTimeMillis() - timeLastClick.get(pid) > 600 || System.currentTimeMillis() - timeLastClick.get(pid) < 110)
			{
				timeLastClick.replace(pid, System.currentTimeMillis());
			}

			else
			{
				differenceTempsNonFinal = System.currentTimeMillis() - timeLastClick.get(pid);
				timeLastClick.replace(pid, System.currentTimeMillis());
			}

			final long differenceTemps = differenceTempsNonFinal;

			if(!rClickingPlayersTask.containsKey(pid) || rClickingPlayersTask.get(pid).getTaskId() > 0)
			{
				if(e.getPlayer().getItemInHand() != null && !GameManager.getInstance(e.getPlayer().getWorld()).getTeamOf(e.getPlayer()).getSafeZone().hasInside(e.getPlayer()))
					if(WeaponManager.getInstance().doEffectAndReload(e.getPlayer().getItemInHand(), e.getPlayer(), pid, differenceTemps))// DO EFFECT AND RELOAD
						e.setCancelled(true);
			}
		}

		if(e.getRightClicked() != null && e.getRightClicked().getType().equals(EntityType.ARMOR_STAND) && e.getPlayer().getItemInHand() != null && Utils.areSimilar(e.getPlayer().getItemInHand(), new CleMolette().getItem(1)))
		{
			Location carpetLoc = e.getRightClicked().getLocation();

			if(carpetLoc.getBlock().getType().equals(Material.CARPET) && carpetLoc.getBlock().getData() == 0 && carpetLoc.getBlock().getLocation().equals(pm.getTFPlayer(e.getPlayer()).getTurretLocation()))
			{
				if(new CanonMontable().removeCanon(e.getPlayer()))
				{
					e.getPlayer().getInventory().remove(Material.WHEAT);
					e.getPlayer().getInventory().addItem(new CanonMontable().getItem(1));
				}
			}
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getEntity().getWorld()))
			return;

		e.setCancelled(true);

		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player && e.getCause().equals(DamageCause.ENTITY_ATTACK) && GameManager.getInstance(e.getEntity().getWorld()).isGameStarted() && !GameManager.getInstance(e.getEntity().getWorld()).sameTeam((Player)e.getDamager(), (Player)e.getEntity()))
		{
			Player damager = (Player)e.getDamager();
			Player victim = (Player)e.getEntity();

			Location point = victim.getLocation().clone().add(0, 1, 0);

			Location po = damager.getEyeLocation();

			for(int i = 0; i < 20; i++)
			{
				po.add(po.getDirection().multiply(0.25));

				if(PlayerManager.bodyCub(victim).hasInside(po) || PlayerManager.headCub(victim).hasInside(po))
				{
					point = po;
					break;
				}
			}
			
			if(pm.getTFPlayer(damager).getLastHitDate() + 400 > System.currentTimeMillis())//TEMPS AVANT TAPER
				return;

			else
				pm.getTFPlayer(damager).setLastHitDate(System.currentTimeMillis());

			for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
			{
				if(w instanceof AbstractCorpsACorpsWeapon && w.getItem(1).isSimilar(damager.getItemInHand()) && damager.getItemInHand().getAmount() > 0)
				{

					if(victim.getHealth() - ((AbstractCorpsACorpsWeapon)w).getDamage() <= 0)
						pm.getTFPlayer(victim).addDamager(damager);

					if(pm.getTFPlayer(damager).isInvisible() || w instanceof Batte)
						GameManager.damageTF(victim, damager, ((AbstractCorpsACorpsWeapon)w).getDamage() * 2, damager.getEyeLocation().getDirection().add(new Vector(0, 0.3, 0)).multiply(1.1), point);

					else
						GameManager.damageTF(victim, damager, ((AbstractCorpsACorpsWeapon)w).getDamage(), damager.getEyeLocation().getDirection().add(new Vector(0, 0.3, 0)).multiply(0.75), point);

					if(w instanceof Poignard)
					{
						for(int i = 0; i < 50; i++)
						{
							double angle1 = new Random().nextDouble() * 2 * Math.PI;
							double angle2 = new Random().nextDouble() * 2 * Math.PI - Math.PI / 2;

							double x = Math.cos(angle1) * Math.cos(angle2);
							double z = Math.sin(angle1) * Math.cos(angle2);
							double y = Math.sin(angle2);

							damager.getWorld().spigot().playEffect(point.clone().add(x / 1.5, y / 1.5, z / 1.5), Effect.COLOURED_DUST, 0, 1, 0.7f, 0f, 0.4f, 1f, 0, 100);
						}
					}

					if(damager.getItemInHand().getAmount() == 1 && w.getTimeCharging() >= 0.1)
						WeaponManager.getInstance().reload(damager, w);

					else
						damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);

					damager.updateInventory();

					return;
				}
			}

			for(AbstractUltimateWeapon w : TF.getInstance().getWeaponManager().listUltimateWeapons())
			{
				if(w instanceof AbstractUltimateCorpsACorpsWeapon && w.getItem(1).isSimilar(damager.getItemInHand()) && damager.getItemInHand().getAmount() > 0)
				{
					GameManager.damageTF(victim, damager, ((AbstractUltimateCorpsACorpsWeapon)w).getDamage(), damager.getEyeLocation().getDirection().add(new Vector(0, 0.3, 0)).multiply(0.75), point);

					if(WeaponManager.getInstance().getReloadUltiFinished().get(damager) == null)
					{
						WeaponManager.getInstance().getReloadUltiFinished().put(damager, false);
					}

					WeaponManager.getInstance().getReloadUltiFinished().replace(damager, false);
					
					if(damager.getItemInHand().getAmount() == 1)
						WeaponManager.getInstance().startReloadUlti(damager);

					else
						damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
					
					damager.updateInventory();

					return;
				}
			}
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				GameManager.respawn(e.getPlayer());
			}
		}.runTaskLater(TF.getInstance(), 2);
	}

	@EventHandler
	public void deathEvent(PlayerDeathEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getEntity().getWorld()))
			return;

		GameManager.death(e.getEntity());

		e.setDeathMessage("");

		// Player p = e.getEntity();
		//
		// WeaponManager.deleteUltiFinished(e.getEntity());
		//
		// List<Player> killers = pm.getLastDamagersOf(p);
		//
		// if(killers == null)
		// {
		// e.setDeathMessage("�8[�9TF2�8] " + (GameManager.getInstance(e.getEntity().getWorld()).getTeamOf(p).equalsIgnoreCase("red") ? "�c" : "�9") + p.getName() + " �7est mort");
		// return;
		// }
		//
		// if(TF.getInstance().getGM(e.getEntity().getWorld()).isGameStarted())
		// {
		// e.setKeepInventory(true);
		//
		// String deathMsg = "�8[�9TF2�8] " + (GameManager.getInstance(e.getEntity().getWorld()).getTeamOf(p).equalsIgnoreCase("red") ? "�c" : "�9") + p.getName() + " �7a �t� tu� par �f";
		//
		// for(Player killer : killers)
		// {
		// deathMsg = deathMsg + (GameManager.getInstance(e.getEntity().getWorld()).getTeamOf(killer).equalsIgnoreCase("red") ? "�c" : "�9") + killer.getName() + " " +
		// pm.kitOf(killer).getSymbole() + "�7 , ";
		// }
		//
		// deathMsg = deathMsg + "##";
		//
		// deathMsg = deathMsg.replaceAll(" \\, \\#\\#", "");
		// deathMsg = deathMsg.replaceAll("a �t� tu� par �f\\#\\#", "est mort");
		//
		// e.setDeathMessage(deathMsg);
		//
		// for(Player killer : killers)
		// WeaponManager.startReloadUlti(killer);
		//
		// pm.rmDamagersTo(p);
		//
		// pm.deleteThingsLikeMineOrTurretOf(p);
		// }
	}

	private HashMap<Player, Kit> ancientKit = new HashMap<>();
	
	@EventHandler
	public void inventoryEvent(InventoryClickEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getWhoClicked().getWorld()))
			return;

		GameManager gm = TF.getInstance().getGM(e.getWhoClicked().getWorld());

		if(!e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE) && !e.getInventory().getTitle().equals(Kit.getConfigKitInventory().getTitle()))
			e.setCancelled(true);

		if(!(e.getWhoClicked() instanceof Player))
			return;

		Player p = (Player)e.getWhoClicked();

		if(e.getInventory().getTitle().equals(Kit.getChangeKitInventory(p).getName()) && e.getCurrentItem() != null)
		{
			e.setCancelled(true);
			
			Kit k = Kit.byRepItem(e.getCurrentItem());
			
			if(k != null)
			{
				boolean isInSZ;
				
				if(gm.getTeamOf(p) != null)
					isInSZ = gm.getTeamOf(p).getSafeZone().hasInside(p);
				
				else
					isInSZ = false;

				if(k.getRepItem(gm.getGameType()).equals(e.getCurrentItem()) && gm.isGameStarted() && !pm.kitOf(p).equals(Kit.NOKIT) && !isInSZ)
				{
					pm.setNextKit(p, k);
					p.closeInventory();
					p.sendMessage("�8[�9TF2�8] �7Vous serez �6" + k.getName() + " �7� votre prochaine r�aparition");

					if(randomKit.get(p) == null)
						randomKit.put(p, false);

					else
						randomKit.replace(p, false);
				}

				else if(k.getRepItem(gm.getGameType()).equals(e.getCurrentItem()) && gm.isGameStarted() && (pm.kitOf(p).equals(Kit.NOKIT) || isInSZ))
				{
					pm.changeKit(p, k);
					pm.setNextKit(p, k);
					p.closeInventory();
					
					if(randomKit.get(p) == null)
						randomKit.put(p, false);

					else
						randomKit.replace(p, false);
				}

				else if(k.getRepItem(gm.getGameType()).equals(e.getCurrentItem()) && !gm.isGameStarted())
				{
					pm.changeKit(p, k);
					pm.setNextKit(p, k);
					p.closeInventory();

					if(randomKit.get(p) == null)
						randomKit.put(p, false);

					else
						randomKit.replace(p, false);
				}
			}

			if(e.getCurrentItem().getType().equals(Material.INK_SACK) && e.getCurrentItem().getItemMeta().getDisplayName().equals("�c�kmm�r �fAl�atoire �c�kmm") && gm.isGameStarted())
			{
				do
					pm.setNextKit(p, Kit.values()[new Random().nextInt(Kit.values().length)]);
				while(!pm.nextKit(p).isReal());

				p.closeInventory();
				p.sendMessage("�8[�9TF2�8] �7Vous aurez une classe al�atoire � votre prochaine r�aparition");

				if(randomKit.get(p) == null)
					randomKit.put(p, true);

				else
					randomKit.replace(p, true);
			}

			else if(e.getCurrentItem().getType().equals(Material.INK_SACK) && e.getCurrentItem().getItemMeta().getDisplayName().equals("�c�kmm�r �fAl�atoire �c�kmm") && !gm.isGameStarted())
			{
				do
					pm.setKitOf(p, Kit.values()[new Random().nextInt(Kit.values().length)]);
				while(!pm.kitOf(p).isReal());
				
				pm.changeKit(p, pm.kitOf(p));

				p.closeInventory();
			}

			else if(e.getCurrentItem().getType().equals(Material.COOKED_CHICKEN) && e.getCurrentItem().getItemMeta().getDisplayName().equals("�6Tips : �aActiv�s"))
			{
				pm.setTipsActive(p, false);

				e.getInventory().setItem(27, Kit.getChangeKitInventory(p).getItem(27));
			}

			else if(e.getCurrentItem().getType().equals(Material.RAW_CHICKEN) && e.getCurrentItem().getItemMeta().getDisplayName().equals("�6Tips : �cD�sactiv�s"))
			{
				pm.setTipsActive(p, true);

				e.getInventory().setItem(27, Kit.getChangeKitInventory(p).getItem(27));
			}

			else if(e.getCurrentItem().getType().equals(Material.PAPER))
			{
				p.openInventory(Kit.getConfigKitInventory());
				
				ancientKit.put(p, pm.kitOf(p));
				
				p.getOpenInventory().getBottomInventory().clear();
			}
			
			for(Team t : gm.getTeams())
			{
				if(t.getRepItem().equals(e.getCurrentItem()) && !gm.isGameStarted())
				{
					for(Team te : gm.getTeams())
					{
						te.getPlayers().remove(p.getUniqueId());
					}
					
					t.getPlayers().add(p.getUniqueId());

					p.closeInventory();
					
					break;
				}
			}

			TF.getInstance().getPlayerManager().setArmorTo(p);
		}
		
		else if(e.getInventory().getTitle().equals("Choisissez une abilit�e") && e.getCurrentItem() != null)
		{
			if(e.getCurrentItem().getType().equals(Material.SKULL_ITEM))
			{
				Inventory inv = Bukkit.createInventory(null, 3*9, "Disguise");
				
				for(Player pl : GameManager.getInstance(p.getWorld()).getPlayersOnline())
				{
					if(GameManager.getInstance(p.getWorld()).sameTeam(pl, p))
						continue;
					
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
					SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
					skullMeta.setOwner(pl.getName());
					skullMeta.setDisplayName(GameManager.getInstance(p.getWorld()).getTeamOf(pl).getPrefix()+pl.getName());
					List<String> lore = new ArrayList<>();
					lore.add("�7Se d�guiser en tant que "+GameManager.getInstance(p.getWorld()).getTeamOf(pl).getPrefix()+pl.getName());
					skullMeta.setLore(lore);
					head.setItemMeta(skullMeta);
					
					inv.addItem(head);
				}
				
				p.openInventory(inv);
			}
			
			else if(e.getCurrentItem().getType().equals(Material.BARRIER))
			{
				((Disguise)Disguise.getInstance()).decharge(p);
				p.closeInventory();
				WeaponManager.getInstance().startReloadUlti(p);
			}
		}

		else if(e.getInventory().getTitle().equals("Disguise") && e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null)
		{
			e.setCancelled(true);

			for(Player pl : e.getWhoClicked().getWorld().getPlayers())
			{
				if((GameManager.getInstance(p.getWorld()).getTeamOf(pl).getPrefix() + pl.getName()).equals(e.getCurrentItem().getItemMeta().getDisplayName()))
				{
					e.getWhoClicked().closeInventory();
					WeaponManager.getInstance().startReloadUlti(p);

					pm.setDisguise(p, pl);
					pm.setArmorTo(p);

					((Disguise)Disguise.getInstance()).disguise(p, pl);

					new BukkitRunnable()
					{
						@Override
						public void run()
						{
							pm.setDisguise(p, null);
							pm.setArmorTo(p);

							((Disguise)Disguise.getInstance()).unDisguise((Player)e.getWhoClicked());
						}
					}.runTaskLater(TF.getInstance(), 400);
				}
			}
		}
		
		else if(e.getInventory().getTitle().equals(Kit.getConfigKitInventory().getTitle()))
		{
			if(e.getClickedInventory().equals(p.getOpenInventory().getTopInventory()) && e.getCurrentItem() != null)
			{
				pm.changeKit(p, Kit.byRepItem(e.getCurrentItem()));
				p.updateInventory();
				
				e.setCancelled(true);

				p.sendMessage("�aModifiez les places des armes et fermez l'inventaire quand vous aurez fini.");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;
		
		Player p = (Player)e.getPlayer();
		
		if(e.getView().getTopInventory().getTitle().equals(Kit.getConfigKitInventory().getTitle()))
		{
			HashMap<Class<? extends AbstractWeapon>, Integer> weaponsPlaces = pm.getWeaponsPlaces(p.getName()).get(pm.kitOf(p));
			
			for(int i = 0; i < 6; i++)
			{
				if(p.getInventory().getItem(i) != null)
				{
					weaponsPlaces.replace((Class<? extends AbstractWeapon>)TF.getInstance().getWeaponManager().byName(ChatColor.stripColor(p.getInventory().getItem(i).getItemMeta().getDisplayName())).getClass(), i);
				}
			}
			
			HashMap<Kit, HashMap<Class<? extends AbstractWeapon>, Integer>> places = pm.getWeaponsPlaces(p.getName());
			
			places.replace(pm.kitOf(p), weaponsPlaces);
			
			pm.setWeaponsPlaces(p.getName(), places);
	
			if(ancientKit.get(p) != null)
				pm.changeKit(p, ancientKit.get(p));
			
			p.updateInventory();
			
			ancientKit.remove(p);
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getWorld()))
			return;
		
		if(e.toWeatherState())
			e.setCancelled(true);
	}

	@EventHandler
	public void damageEvent(EntityDamageEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getEntity().getWorld()))
			return;

		if(!(e.getEntity() instanceof Player))
			return;

		if(e.getCause().equals(DamageCause.FALL))
			e.setCancelled(true);

		Player p = (Player)e.getEntity();
		TF.getInstance().getPlayerManager().setArmorTo(p);
		
		if(e.getCause().equals(DamageCause.FIRE_TICK))
		{
//			TF.debug(e.getCause());
			
			e.setCancelled(true);
			
			PlayerManager pm = pm;

//			if(pm.kitOf(pm.getFireCauseOf(p).getDamager()).equals(Kit.DEMOMAN))
//				GameManager.damageTF(p, pm.getFireCauseOf(p), e.getDamage() * 2, new Vector(0, 0, 0));
//
//			else if(pm.kitOf(pm.getFireCauseOf(p)).equals(Kit.PYRO))
//				GameManager.damageTF(p, pm.getFireCauseOf(p), e.getDamage() * 1.6, new Vector(0, 0, 0));
//
//			else
//				GameManager.damageTF(p, pm.getFireCauseOf(p), e.getDamage(), new Vector(0, 0, 0));
			
			if(!pm.getFireCauseOf(p).isUnknown())
			{
				if(pm.getFireCauseOf(p).getReason() instanceof Barbecue)
					GameManager.damageTF(p, Bukkit.getPlayer(pm.getFireCauseOf(p).getDamager()), e.getDamage() * 1.2, new Vector(0, 0, 0));
				
				else if(pm.getFireCauseOf(p).getReason() instanceof CoktailMolotov)
					GameManager.damageTF(p, Bukkit.getPlayer(pm.getFireCauseOf(p).getDamager()), e.getDamage() * 2.1, new Vector(0, 0, 0));
				
				else if(pm.getFireCauseOf(p).getReason() instanceof FuseeDeDetresse)
					GameManager.damageTF(p, Bukkit.getPlayer(pm.getFireCauseOf(p).getDamager()), e.getDamage() * 2, new Vector(0, 0, 0));
				
				else
					GameManager.damageTF(p, Bukkit.getPlayer(pm.getFireCauseOf(p).getDamager()), e.getDamage(), new Vector(0, 0, 0));
			}
			
			else
				GameManager.damageTF(p, null, e.getDamage(), new Vector(0, 0, 0));
		}
	}

	@EventHandler
	public void vehicleDamageEvent(VehicleDamageEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getVehicle().getWorld()))
			return;
		
		GameManager gm = GameManager.getInstance(e.getVehicle().getWorld());
		
		if(!gm.getGameType().isCarts() || !(e.getAttacker() instanceof Player) || !(e.getVehicle() instanceof Minecart) || !gm.getCartManager().getCarts().contains(e.getVehicle()))
			return;

		e.setCancelled(true);
		CartRunnable.addHitCart((Minecart)e.getVehicle());
	}

	@EventHandler
	public void projectileBreak(ProjectileHitEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getEntity().getWorld()))
			return;

		for(AbstractUltimateWeapon w : TF.getInstance().getWeaponManager().listUltimateWeapons())
		{
			if(w instanceof AbstractUltimateThingLauncher && ((AbstractUltimateThingLauncher)w).getProjectile().equals(e.getEntityType()) && w.name().equals(e.getEntity().getCustomName()))
			{
				((AbstractUltimateThingLauncher)w).explode(((Player)e.getEntity().getShooter()), e.getEntity().getLocation());
				e.getEntity().remove();
			}
		}

		for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
		{
			if(w instanceof AbstractThingLauncher && ((AbstractThingLauncher)w).getProjectile().equals(e.getEntityType()) && w.getName().equals(e.getEntity().getCustomName()))
			{
				((AbstractThingLauncher)w).explode(((Player)e.getEntity().getShooter()), e.getEntity());
				e.getEntity().remove();
			}
		}
	}

	@EventHandler
	public void onKick(PlayerKickEvent e)
	{
		if(e.getReason().equals("Flying is not enabled on this server"))
			e.setCancelled(true);
	}

	@EventHandler
	public void doubleJump(PlayerToggleFlightEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			e.setCancelled(true);

		if(TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted() && pm.kitOf(e.getPlayer()).equals(Kit.SCOUT) && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE) && (canDoubleJump.get(e.getPlayer()) == null || canDoubleJump.get(e.getPlayer())))
		{
			e.getPlayer().setFlying(false);
			e.getPlayer().setVelocity(e.getPlayer().getEyeLocation().getDirection().clone().multiply(2).add(e.getPlayer().getVelocity().setY(0).multiply(1.75)).add(new Vector(0, e.getPlayer().getEyeLocation().getDirection().getY() < 0 ? Math.abs(e.getPlayer().getEyeLocation().getDirection().getY()) * 3.75 : 0, 0)));

			reloadDoubleJump(e.getPlayer(), false);

			Utils.playSound(e.getPlayer().getLocation(), Sound.BAT_TAKEOFF, 15);
		}
		
		if(!pm.kitOf(e.getPlayer()).equals(Kit.SCOUT))
		{
			reloadDoubleJump(e.getPlayer(), false);
		}
	}
	
	private HashMap<Player, BukkitRunnable> reloadRunnables;

	public void reloadDoubleJump(Player p, boolean now)
	{
		if(reloadRunnables == null)
			reloadRunnables = new HashMap<>();
		
		if(reloadRunnables.get(p) != null)
			reloadRunnables.get(p).cancel();
		
		if(canDoubleJump.get(p) == null)
			canDoubleJump.put(p, false);

		else
			canDoubleJump.replace(p, false);

		p.setAllowFlight(false);
		
		if(pm.kitOf(p).equals(Kit.SCOUT))
		{
			BukkitRunnable br = new BukkitRunnable()
			{
				double turns = 0;
				final int maxTurns = (now ? 1 : (TfCommand.cheaters.contains(p) ? 10 : 40));
				
				@Override
				public void run()
				{
					if(!GameManager.getInstance(p.getWorld()).isGameStarted())
					{
						p.setAllowFlight(true);
						cancel();
						return;
					}
					
					if(turns < maxTurns)
						Title.sendActionBar(p, "�3Dash dans : "+(maxTurns-turns)/10);
					
					else if(turns >= maxTurns)
					{
						canDoubleJump.replace(p, true);
						p.setAllowFlight(true);
						Title.sendActionBar(p, "�bDash pr�t");
					}
					
					turns++;
				}
			};
			
			br.runTaskTimer(TF.getInstance(), 0l, 2);
	
			reloadRunnables.put(p, br);
		}
	}

	@EventHandler
	public void trampoAndMine(PlayerMoveEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()) || !TF.getInstance().getGM(e.getPlayer().getWorld()).isGameStarted())
			return;
		
		Player p = e.getPlayer();
		
		if(!GameManager.getInstance(p.getWorld()).getTeamOf(p).areBlocksUsable())
		{
			Title.sendActionBar(p, "�eLes syst�mes sont inutilisables !");
			return;
		}
		
		if(TF.getInstance().getGM(p.getWorld()).isGameStarted() && e.getTo().getBlock().getType().equals(Material.IRON_PLATE) && GameManager.getInstance(p.getWorld()).getTeamOf(p).getName(false).equals("rouge"))
		{
			p.setVelocity(new Vector(p.getVelocity().getX()*2, 1, p.getVelocity().getZ()*2));
		}

		if(TF.getInstance().getGM(p.getWorld()).isGameStarted() && e.getTo().getBlock().getType().equals(Material.GOLD_PLATE) && GameManager.getInstance(p.getWorld()).getTeamOf(p).getName(false).equals("bleu"))
		{
			p.setVelocity(new Vector(p.getVelocity().getX()*2, 1, p.getVelocity().getZ()*2));
		}

		else if(TF.getInstance().getGM(p.getWorld()).isGameStarted() && e.getTo().getBlock().getType().equals(Material.STONE_PLATE))
		{
			for(Player pl : Bukkit.getOnlinePlayers())
			{
				if(GameManager.getInstance(pl.getWorld()).sameTeam(pl, p))
					continue;

				if(!GameManager.getInstance(p.getWorld()).getTeamOf(pl).areBlocksUsable())
				{
					Title.sendActionBar(p, "�eLes syst�mes sont inutilisables !");
					return;
				}
				
				
				if(pm.mineLocationsOf(pl).contains(e.getTo().getBlock().getLocation()))
				{
					List<Location> mineLocs = pm.mineLocationsOf(pl);
					mineLocs.remove(e.getTo().getBlock().getLocation());
					pm.setMineLocations(pl, mineLocs);
					
					((Mine)Mine.getInstance()).explode(e.getTo().getBlock().getLocation().clone().add(0.5, 0, 0.5), pl);

					List<Location> list = pm.mineLocationsOf(p);
					list.remove(e.getTo().getBlock().getLocation());
					pm.setMineLocations(p, list);
				}
			}
		}
	}

	@EventHandler
	public void onInteractWithArmorStand(PlayerInteractAtEntityEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(!(e.getRightClicked() instanceof ArmorStand))
			return;

		e.setCancelled(true);
	}
	
	@EventHandler
	public void onWorldChanged(PlayerChangedWorldEvent e)
	{
		onJoin(new PlayerJoinEvent(e.getPlayer(), "�e"+e.getPlayer().getName()+" a rejoint ce monde"));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		GameManager gm = GameManager.getInstance(e.getPlayer().getWorld());

		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		
		if(gm.isGameStarted() && !gm.getPlayers().contains(e.getPlayer().getUniqueId()))
		{
			gm.getPlayers().add(e.getPlayer().getUniqueId());

			GameManager.getInstance(e.getPlayer().getWorld()).giveTeam(e.getPlayer());
			
			GameManager.respawn(e.getPlayer());
			
			pm.changeKit(e.getPlayer(), Kit.NOKIT);
			
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
//					TF.debug("run");
//					TF.debug(!pm.kitOf(e.getPlayer()).isReal());
//					TF.debug(!e.getPlayer().getOpenInventory().getTopInventory().equals(Kit.getChangeKitInventory(e.getPlayer())));
					
					if(!pm.kitOf(e.getPlayer()).isReal())
						if(!e.getPlayer().getOpenInventory().getTopInventory().equals(Kit.getChangeKitInventory(e.getPlayer())))
							e.getPlayer().openInventory(Kit.getChangeKitInventory(e.getPlayer()));
					
					else
						cancel();
				}
			}.runTaskTimer(TF.getInstance(), 10, 10);
		}
		
		else if(!gm.isGameStarted())
			e.getPlayer().teleport(gm.getLobbySpawnPoint());

		if(gm.isGameStarted())
		{
			e.setJoinMessage("�8[�9TF2�8] �f" + gm.getTeamOf(e.getPlayer()).getPrefix() + e.getPlayer().getName() + " �7a rejoint la partie �9(�8" + (e.getPlayer().getWorld().getPlayers().size() + 1) + "�0/�8" + gm.getMinToStart() + "�9)");
		}

		else
		{
			if(gm.getTeamOf(e.getPlayer()) != null)
				e.setJoinMessage("�8[�9TF2�8] �f" + gm.getTeamOf(e.getPlayer()).getPrefix() + e.getPlayer().getName() + " �7a rejoint la partie �9(�8" + (e.getPlayer().getWorld().getPlayers().size() + 1) + "�0/�8" + gm.getMinToStart() + "�9)");

			else
				e.setJoinMessage("�8[�9TF2�8] �f" + "�e" + e.getPlayer().getName() + " �7a rejoint la partie �9(�8" + (e.getPlayer().getWorld().getPlayers().size() + 1) + "�0/�8" + gm.getMinToStart() + "�9)");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		pm.deleteThingsLikeMineOrTurretOf(e.getPlayer());

		if(e.getPlayer().getWorld().getPlayers().size() < 1)
			GameManager.getInstance(e.getPlayer().getWorld()).setStarted(false);
		
//		if(GameManager.getInstance(e.getPlayer().getWorld()).isGameStarted())
//			GameManager.death(e.getPlayer());
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;

		if(GameManager.getInstance(e.getPlayer().getWorld()).isGameStarted())
			e.setCancelled(true);

		if(e.getItem().getItemStack().getType().equals((new Mine().getItem(1).getType())))
		{
			e.getPlayer().getInventory().addItem(new Mine().getItem(e.getItem().getItemStack().getAmount()));
			e.getItem().remove();
		}
	}

	@EventHandler
	public void onRideMinecart(VehicleEnterEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getVehicle().getWorld()))
			return;
		
		if(!GameManager.getInstance(e.getVehicle().getWorld()).getGameType().isCarts())
			return;

		if(e.getVehicle() instanceof Minecart)
			for(Team t : GameManager.getInstance(e.getVehicle().getWorld()).getTeams())
				if(CartManager.getInstance(e.getVehicle().getWorld()).getCart(t).equals(e.getVehicle()))
					e.setCancelled(true);
	}

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;
		
		Player p = e.getPlayer();
		GameManager gm = GameManager.getInstance(p.getWorld());
		CartManager cm = CartManager.getInstance(p.getWorld());
		ZoneManager zm = ZoneManager.getInstance(p.getWorld());
		PlayerManager pm = pm;
		
		if(pm.getSmallBlockPoser() != null && p.equals(pm.getSmallBlockPoser()))
		{
			e.setCancelled(true);
			
			Location loc = e.getBlock().getLocation().add(0.5, -0.6, 0.5);
			
			if(e.getItemInHand().getType().equals(Material.WOOD) && !(e.getItemInHand().getDurability() == 0))
				loc = loc.add(0.5, 0, 0.5);
			
			ArmorStand as = (ArmorStand)e.getBlock().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			
			as.setHelmet(e.getItemInHand());
			as.setSmall(true);
			as.setGravity(false);
			as.setVisible(false);
			as.setHeadPose(pm.getSmallBlockAngle());
			
			pm.setSmallBlockPoser(null);
		}
		
		else if(pm.getBigBlockPoser() != null && p.equals(pm.getBigBlockPoser()))
		{
			e.setCancelled(true);
			
			Location loc = e.getBlock().getLocation().add(0.5, -1.2, 0.5);
			
			if(e.getItemInHand().getType().equals(Material.WOOD) && !(e.getItemInHand().getDurability() == 0))
				loc = loc.add(0.5, 0, 0.5);
			
			ArmorStand as = (ArmorStand)e.getBlock().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			
			as.setHelmet(e.getItemInHand());
			as.setSmall(false);
			as.setGravity(false);
			as.setVisible(false);
			as.setHeadPose(pm.getBigBlockAngle());
			
			pm.setBigBlockPoser(null);
		}
		
		if(p.equals(pm.getZone1Modifier()))
		{
			e.setCancelled(true);
			p.getWorld().playEffect(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), Effect.POTION_SWIRL, 1);

			pm.setZone1Modifier(null);
			pm.setZone2Modifier(p);

			zm.setZone(new Cuboid(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), new Location(p.getWorld(), 0, 0, 0)));

			p.sendMessage("�aCoin 1 d�fini !");

			return;
		}

		else if(p.equals(pm.getZone2Modifier()))
		{
			e.setCancelled(true);
			p.getWorld().playEffect(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), Effect.POTION_SWIRL, 1);

			pm.setZone2Modifier(null);

			zm.getZone().setLoc2(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));

			p.sendMessage("�aZone d�finie !");

			return;
		}

		for(Team t : gm.getTeams())
		{
			if(t.isModifyingSZ1(p))
			{
				e.setCancelled(true);
				p.getWorld().playEffect(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), Effect.HEART, 1);

				t.setSZ1Modifier(null);
				t.setSZ2Modifier(p);

				t.getSafeZone().setLoc1(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));

				p.sendMessage("�aCoin 1 " + t.getName(false) + " ajout� !");

				return;
			}

			else if(t.isModifyingSZ2(p))
			{
				e.setCancelled(true);
				p.getWorld().playEffect(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), Effect.HEART, 1);

				t.setSZ2Modifier(null);

				t.getSafeZone().setLoc2(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));

				for(Block loc : t.getSafeZone().blocksInside())
					p.getWorld().playEffect(loc.getLocation().add(0.5, 0.5, 0.5), Effect.HEART, 1);

				p.sendMessage("�aCoin 2 " + t.getName(false) + " ajout� !");

				return;
			}

			//////

			if(e.getBlock().getType().equals(Material.RAILS))
			{
				if(t.isModifyingStartRails(p))
				{
					cm.setStart(t, e.getBlock().getLocation());

					t.setStartRailsModifier(null);

					p.sendMessage("�aStart " + t.getName(false) + " d�fini");

					return;
				}

				else if(t.isModifyingEndRails(p))
				{
					cm.setEnd(t, e.getBlock().getLocation());

					t.setEndRailsModifier(null);

					p.sendMessage("�aEnd " + t.getName(false) + " d�fini. Nombre de rails : " + cm.recalculateRails(t).size());

					return;
				}

				if(t.isAddingBifurc(p))
				{
					cm.getBifurcs(t).add(e.getBlock().getLocation());

					t.setBifurcAdder(null);

					p.sendMessage("�aBifurc ajout�e. Nombre de rails : " + cm.recalculateRails(t).size());

					return;
				}
			}
		}
	}

	@EventHandler
	public void onVehicleCollide(VehicleEntityCollisionEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getVehicle().getWorld()))
			return;

		CartManager cm = CartManager.getInstance(e.getVehicle().getWorld());
		GameManager gm = GameManager.getInstance(e.getVehicle().getWorld());
		
		if(!gm.getGameType().isCarts())
			return;

		for(Team t : gm.getTeams())
		{
			if(cm == null || !cm.areCartsCreated() || !(e.getVehicle() instanceof Minecart) || !((Minecart)e.getVehicle()).equals(cm.getCart(t)))
				continue;

			e.setCollisionCancelled(true);
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getBlock().getWorld()))
			return;

		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getBlock().getWorld()))
			return;

		e.setCancelled(true);
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if(!TF.getInstance().getWorlds().contains(e.getPlayer().getWorld()))
			return;
		
		boolean all = false;
		
		String message = e.getMessage();
		
		if(message.startsWith("!"))
		{
			all = true;
			message = message.replaceFirst("!", "");
		}
		
		e.setCancelled(true);
		
		GameManager gm = GameManager.getInstance(e.getPlayer().getWorld());
		
		boolean sent = false;
		
		if(gm.getTeamOf(e.getPlayer()) != null)
		{
			for(Player p : gm.getWorld().getPlayers())
			{
				if(gm.getTeamOf(p) == null || gm.sameTeam(e.getPlayer(), p) || all)
				{
					p.sendMessage((all ? "�7(all) " : "�7(team) ")+gm.getTeamOf(e.getPlayer()).getPrefix()+e.getPlayer().getName()+"�7: �f"+message);
					
					if(!sent)
						Bukkit.getConsoleSender().sendMessage((all ? "�7(all) " : "�7(team) ")+gm.getTeamOf(e.getPlayer()).getPrefix()+e.getPlayer().getName()+"�7: �f"+message);
					
					sent = true;
					
					if(all)
						for(World w : Bukkit.getWorlds())
							if(w != p.getWorld())
								for(Player pl : w.getPlayers())
									pl.sendMessage("�7(all) "+gm.getTeamOf(e.getPlayer()).getPrefix()+e.getPlayer().getName()+"�7: �f"+message);
				}
			}
		}
		
		else
		{
			for(Player p : gm.getWorld().getPlayers())
			{
				p.sendMessage("�7"+e.getPlayer().getName()+"�7: �f"+message);
				
				if(!sent)
					Bukkit.getConsoleSender().sendMessage("�7"+e.getPlayer().getName()+"�7: �f"+message);
				
				sent = true;
			}
		}
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent e)
	{
//		TF.debug("item spawn");
//		TF.debug(TF.getInstance().getWeaponManager().listWeapons());
		
		for(AbstractWeapon w : TF.getInstance().getWeaponManager().listWeapons())
		{
			if(!(w instanceof AbstractLaunchableItem))
				continue;
			
//			TF.debug(w.getName());
//			TF.debug(((AbstractLaunchableItem)w).getLaunched());
//			TF.debug(e.getEntity().getItemStack().getType());
			
			if(((AbstractLaunchableItem)w).getLaunched().equals(e.getEntity().getItemStack().getType()))
			{
				e.setCancelled(false);
				break;
			}
			
			else
				e.setCancelled(true);
		}
	}
	
	List<Material> transparent;
	
	public List<Material> transparent()
	{
		if(transparent == null)
		{
			transparent = new ArrayList<>();
			transparent.add(Material.STEP);
			transparent.add(Material.WOOD_STEP);
			transparent.add(Material.STONE_PLATE);
			transparent.add(Material.GOLD_PLATE);
			transparent.add(Material.IRON_PLATE);
			transparent.add(Material.WOOD_STAIRS);
			transparent.add(Material.COBBLESTONE_STAIRS);
			transparent.add(Material.BRICK_STAIRS);
			transparent.add(Material.SMOOTH_STAIRS);
			transparent.add(Material.NETHER_BRICK_STAIRS);
			transparent.add(Material.SANDSTONE_STAIRS);
			transparent.add(Material.SPRUCE_WOOD_STAIRS);
			transparent.add(Material.BIRCH_WOOD_STAIRS);
			transparent.add(Material.JUNGLE_WOOD_STAIRS);
			transparent.add(Material.QUARTZ_STAIRS);
			transparent.add(Material.ACACIA_STAIRS);
			transparent.add(Material.DARK_OAK_STAIRS);
			transparent.add(Material.RED_SANDSTONE_STAIRS);
	
			for(Material mat : Material.values())
				if(mat.isTransparent())
					transparent.add(mat);
		}
		
		return transparent;
	}
}
