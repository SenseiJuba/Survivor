package fr.senseijuba.survivor.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameManager;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.blocks.C4;
import fr.senseijuba.survivor.weapons.blocks.CanonMontable;
import fr.senseijuba.survivor.weapons.blocks.Mine;
import fr.senseijuba.survivor.weapons.blocks.Trampoline;
import fr.senseijuba.survivor.weapons.corpsACorps.*;
import fr.senseijuba.survivor.weapons.guns.*;
import fr.senseijuba.survivor.weapons.launchableItem.AbstractLaunchableItem;
import fr.senseijuba.survivor.weapons.launchableItem.Dynamite;
import fr.senseijuba.survivor.weapons.launchableItem.Fumigene;
import fr.senseijuba.survivor.weapons.launchableItem.GrenadeFlash;
import fr.senseijuba.survivor.weapons.notDangerous.*;
import fr.senseijuba.survivor.weapons.thingsLauncher.RocketLauncher;
import fr.senseijuba.survivor.weapons.ultimateWeapons.AbstractUltimateWeapon;
import fr.senseijuba.survivor.weapons.ultimateWeapons.notDangerous.AbstractNotDangerousUltimateWeapon;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.weapons.blocks.C4;
import fr.lumin0u.vertix.weapons.blocks.CanonMontable;
import fr.lumin0u.vertix.weapons.blocks.Mine;
import fr.lumin0u.vertix.weapons.blocks.Trampoline;
import fr.lumin0u.vertix.weapons.corpsACorps.Batte;
import fr.lumin0u.vertix.weapons.corpsACorps.CleMolette;
import fr.lumin0u.vertix.weapons.corpsACorps.Hache;
import fr.lumin0u.vertix.weapons.corpsACorps.Poignard;
import fr.lumin0u.vertix.weapons.corpsACorps.PoingsAmericains;
import fr.lumin0u.vertix.weapons.corpsACorps.ScieAmputation;
import fr.lumin0u.vertix.weapons.guns.AbstractGun;
import fr.lumin0u.vertix.weapons.guns.Defenseur;
import fr.lumin0u.vertix.weapons.guns.Defoncator;
import fr.lumin0u.vertix.weapons.guns.FuseeDeDetresse;
import fr.lumin0u.vertix.weapons.guns.LaTornade;
import fr.lumin0u.vertix.weapons.guns.MitrailletteLourde;
import fr.lumin0u.vertix.weapons.guns.PistoletAutomatique;
import fr.lumin0u.vertix.weapons.guns.PistoletTranquilisant;
import fr.lumin0u.vertix.weapons.guns.Revolver;
import fr.lumin0u.vertix.weapons.guns.Sniper;
import fr.lumin0u.vertix.weapons.guns.shotguns.Blaoups;
import fr.lumin0u.vertix.weapons.guns.shotguns.CanonScie;
import fr.lumin0u.vertix.weapons.guns.shotguns.FusilAPompe;
import fr.lumin0u.vertix.weapons.launchableItem.AbstractLaunchableItem;
import fr.lumin0u.vertix.weapons.launchableItem.Dynamite;
import fr.lumin0u.vertix.weapons.launchableItem.Fumigene;
import fr.lumin0u.vertix.weapons.launchableItem.GrenadeFlash;
import fr.lumin0u.vertix.weapons.notDangerous.AbstractNotDangerousWeapon;
import fr.lumin0u.vertix.weapons.notDangerous.Detonateur;
import fr.lumin0u.vertix.weapons.notDangerous.HealthPotion;
import fr.lumin0u.vertix.weapons.notDangerous.Manette;
import fr.lumin0u.vertix.weapons.notDangerous.MedecinePortable;
import fr.lumin0u.vertix.weapons.notDangerous.MontreInvi;
import fr.lumin0u.vertix.weapons.thingsLauncher.AbstractThingLauncher;
import fr.lumin0u.vertix.weapons.thingsLauncher.RocketLauncher;
import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.corpsACorps.Kukri;
import fr.lumin0u.vertix.weapons.ultimateWeapons.guns.AbstractUltimateGun;
import fr.lumin0u.vertix.weapons.ultimateWeapons.guns.Scavenger;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.AbstractNotDangerousUltimateWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.BeastFury;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.Disguise;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.RedButton;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.ScoutRace;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.UberCharge;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.AbstractUltimateThingLauncher;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.CoktailMolotov;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.Striker;

public class WeaponManager
{
	private List<AbstractWeapon> weapons;
	private List<AbstractUltimateWeapon> ultimateWeapons;
	private static Survivor survivor = Survivor.getInstance();
	
	private static HashMap<Player, Boolean> accelererLUlti = new HashMap<>();
	private static HashMap<Player, Boolean> stopUlti = new HashMap<>();
	private static HashMap<Player, List<String>> whatsReloading = new HashMap<>();
	public static HashMap<Player, Boolean> reloadUltiStarted = new HashMap<>();
	public static HashMap<Player, Boolean> reloadUltiFinished = new HashMap<>();//TODO barbecue reload
	
	public WeaponManager()
	{
		weapons = new ArrayList<>();
		ultimateWeapons = new ArrayList<>();
		registerWeapons();
		registerUltimateWeapons();
	}
	
	private void registerWeapons()
	{
		registerWeapon(new Sniper());
		registerWeapon(new PistoletAutomatique());
		registerWeapon(new HealthPotion());
		registerWeapon(new Dynamite());
		registerWeapon(new Fumigene());
		registerWeapon(new FusilAPompe());
		registerWeapon(new FuseeDeDetresse());
		registerWeapon(new CanonScie());
		registerWeapon(new Batte());
		registerWeapon(new Defenseur());
		registerWeapon(new MitrailletteLourde());
		registerWeapon(new Defoncator());
		registerWeapon(new LaTornade());
		registerWeapon(new PoingsAmericains());
		registerWeapon(new RocketLauncher());
		registerWeapon(new GrenadeFlash());
		registerWeapon(new Poignard());
		registerWeapon(new Revolver());
		registerWeapon(new C4());
		registerWeapon(new Detonateur());
		registerWeapon(new MontreInvi());
		registerWeapon(new CanonMontable());
		registerWeapon(new Manette());
		registerWeapon(new Trampoline());
		registerWeapon(new Mine());
		registerWeapon(new CleMolette());
		registerWeapon(new MedecinePortable());
		registerWeapon(new PistoletTranquilisant());
		registerWeapon(new ScieAmputation());
		registerWeapon(new Barbecue());
		registerWeapon(new Hache());
		registerWeapon(new Blaoups());
	}
	
	private void registerUltimateWeapons()
	{
		registerUltimateWeapon(new ScoutRace());
		registerUltimateWeapon(new Striker());
		registerUltimateWeapon(new BeastFury());
		registerUltimateWeapon(new Kukri());
		registerUltimateWeapon(new Scavenger());
		registerUltimateWeapon(new Disguise());
		registerUltimateWeapon(new RedButton());
		registerUltimateWeapon(new CoktailMolotov());
		registerUltimateWeapon(new UberCharge());
	}

	private void registerWeapon(AbstractWeapon w)
	{
		weapons.add(w);
	}

	private void registerUltimateWeapon(AbstractUltimateWeapon w)
	{
		ultimateWeapons.add(w);
	}
	
	public static List<AbstractWeapon> listWeapons()
	{
		return weapons;
	}
	
	public static List<AbstractUltimateWeapon> listUltimateWeapons()
	{
		return ultimateWeapons;
	}
	
	public static boolean doEffectAndReload(ItemStack eItem, Player p, UUID pid, long differenceTemps)
	{
		if(!GameManager.getInstance(p.getWorld()).getPlayers().contains(p.getUniqueId()))
			return false;
		
//		TF.debug(p.getGameMode());

//		if(p.getGameMode().equals(GameMode.SPECTATOR))
//		{
//			if(TF.getInstance().getListener().rClickingPlayersTask.containsKey(pid))
//			{
//				TF.getInstance().getListener().rClickingPlayersTask.get(pid).cancel();
//				TF.getInstance().getListener().rClickingPlayersTask.remove(pid);
//			}
//			
////			TF.debug("CANCEL");
//			return false;
//		}
		
//		GameManager.doDeathEffect(p, p, GameManager.getInstance(p.getWorld()));
		
		boolean cancel = false;
		
		Something g = null;
		ItemStack gunItemNonFinal = null;
		
		if(eItem != null && eItem.hasItemMeta() && eItem.getItemMeta().getDisplayName() != null)
		{
			for(AbstractWeapon gu : listWeapons())
			{
				ItemStack gunItem1 = null;
				
				if(gu.name().equals("La Tornade"))
					gunItem1 = ((LaTornade)gu).getItem(1, p);
				
				else
					gunItem1 = gu.getItem(1);

				if(eItem.getType().equals(gunItem1.getType()) && eItem.getItemMeta().getDisplayName().equals(gunItem1.getItemMeta().getDisplayName()) && eItem.getAmount() > 0)
				{
					g = gu;
					gunItemNonFinal = gunItem1;
					cancel = true;
				}
			}
			
			if(g == null)
				for(AbstractUltimateWeapon gu : listUltimateWeapons())
				{
					ItemStack gunItem1 = null;
					
					gunItem1 = gu.getItem(1);
	
					if(eItem.getType().equals(gunItem1.getType()) && eItem.getItemMeta().getDisplayName().equals(gunItem1.getItemMeta().getDisplayName()) && eItem.getAmount() > 0)
					{
						g = gu;
						gunItemNonFinal = gunItem1;
						cancel = true;
					}
				}
		}
		
		if(g == null)
			return false;
		
		final Material gunItemtype = gunItemNonFinal.getType();
		final Something weapon = g;
		
		final int weaponSlot = p.getInventory().getHeldItemSlot();
		
		TF.getInstance().getListener().rClickingPlayersTask.put(pid, new BukkitRunnable()//DEBUT DE LA BOUCLE
		{
			boolean cancel = false;
			
			@Override
			public void run()
			{
//				TF.debug(cancel);
				
				if(!p.getGameMode().equals(GameMode.SPECTATOR))
					cancel = false;
				
				if(cancel)
				{
					super.cancel();
					return;
				}
				
				ItemStack gunItem = p.getInventory().getItem(weaponSlot);
				
				if(TF.getInstance().getListener().rClickingPlayersTaskId.get(pid) == null)
					TF.getInstance().getListener().rClickingPlayersTaskId.put(pid, this.getTaskId());
				
				if(TF.getInstance().getListener().rClickingPlayersLast.get(pid) + differenceTemps < System.currentTimeMillis() || !p.getItemInHand().getType().equals(gunItemtype) || TF.getInstance().getListener().rClickingPlayersTaskId.get(pid) != this.getTaskId() || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					if(TF.getInstance().getListener().rClickingPlayersTaskId.get(pid) == this.getTaskId())
						TF.getInstance().getListener().rClickingPlayersTaskId.remove(pid);
					
					TF.getInstance().getListener().rClickingPlayersTask.remove(pid);
					super.cancel();
					return;
				}
				
				if(weapon instanceof AbstractGun || weapon instanceof AbstractUltimateGun)
				{
					if(gunItem.getAmount() == 1)
					{
						if(weapon instanceof AbstractWeapon)
							reload(p, (AbstractWeapon)weapon);
						
						else
						{
							if(reloadUltiFinished.get(p) == null)
							{
								reloadUltiFinished.put(p, false);
							}
							
							reloadUltiFinished.replace(p, false);
							
							startReloadUlti(p);
						}
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractGun)
							((AbstractGun)weapon).shoot(p);
						
						else
							((AbstractUltimateGun)weapon).shoot(p);
						
						p.updateInventory();
					}
				}
				
				else if(weapon instanceof AbstractNotDangerousWeapon || weapon instanceof AbstractNotDangerousUltimateWeapon)
				{
					if(gunItem.getAmount() == 1)
					{
						if(weapon instanceof AbstractWeapon && !(weapon instanceof Detonateur))
							reload(p, (AbstractWeapon)weapon);
						
						else if(!(weapon instanceof Detonateur))
						{
							if(reloadUltiFinished.get(p) == null)
							{
								reloadUltiFinished.put(p, false);
							}
							
							reloadUltiFinished.replace(p, false);
							
							startReloadUlti(p);
						}
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractNotDangerousWeapon)
							((AbstractNotDangerousWeapon)weapon).effect(p);
						
						else
							((AbstractNotDangerousUltimateWeapon)weapon).effect(p);
						
						Utils.playSound(p.getLocation(), weapon.getSound(), weapon.getMaxDistance());
						
						p.updateInventory();
					}
				}
				
				else if(weapon instanceof AbstractLaunchableItem/* || weapon instanceof AbstractUltimateLaunchableItem*/)
				{
					if(gunItem.getAmount() == 1)
					{
						if(weapon instanceof AbstractWeapon)
							reload(p, (AbstractWeapon)weapon);
						
						else
						{
							if(reloadUltiFinished.get(p) == null)
							{
								reloadUltiFinished.put(p, false);
							}
							
							reloadUltiFinished.replace(p, false);
							
							startReloadUlti(p);
						}
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractLaunchableItem)
							((AbstractLaunchableItem)weapon).launch(p);
						
//						else
//							((AbstractUltimateLaunchableItem)weapon).launch(p);
						
						Utils.playSound(p.getLocation(), weapon.getSound(), weapon.getMaxDistance());
						
						reloadUltiFinished.replace(p, false);
						
						p.updateInventory();
					}
				}
				
				else if(weapon instanceof Barbecue)
				{
					if(gunItem.getAmount() > 1)
					{
						((Barbecue)weapon).shoot(p);
						
						p.updateInventory();
					}
					
					if(gunItem.getAmount() > 1)
					{
						gunItem.setAmount(gunItem.getAmount() - 1);
					}
				}
				
				else if(weapon instanceof AbstractThingLauncher || weapon instanceof AbstractUltimateThingLauncher)
				{
					if(gunItem.getAmount() == 1)
					{
						if(weapon instanceof AbstractWeapon)
							reload(p, (AbstractWeapon)weapon);
						
						else
						{
							if(reloadUltiFinished.get(p) == null)
							{
								reloadUltiFinished.put(p, false);
							}
							
							reloadUltiFinished.replace(p, false);
							
							startReloadUlti(p);
						}
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractThingLauncher)
							((AbstractThingLauncher)weapon).launch(p);
						
						else
							((AbstractUltimateThingLauncher)weapon).launch(p);
						
						Utils.playSound(p.getLocation(), weapon.getSound(), weapon.getMaxDistance());
				
						p.updateInventory();
					}
				}
			}
			
			@Override
			public void cancel()
			{
				cancel = true;
			}
		});
		
		TF.getInstance().getListener().rClickingPlayersTask.get(pid).runTaskTimer(TF.getInstance(), 0, g.getRatioTir());
		
		return cancel;
	}
	
	public static void reload(Player p, AbstractWeapon weapon)
	{
		reload(p, weapon, p.getInventory().first(weapon.getItem(1).getType()));
	}
	
	public static void reload(Player p, AbstractWeapon weapon, int place)
	{
		if(TfCommand.cheaters.contains(p))
		{
			p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
			return;
		}
		
		if(weapon instanceof Barbecue)
		{
			Barbecue.reload(p, (Barbecue)weapon);
			return;
		}
		
		
		if(!whatsReloading.containsKey(p))
			whatsReloading.put(p, new ArrayList<>());
		
		if(whatsReloading.containsKey(p) && whatsReloading.get(p).contains(weapon.getName()))
			return;
		
		p.getInventory().remove(weapon.getItem(1));
		p.getInventory().setItem(place, weapon.getItem(0 - (int)(weapon.getTimeCharging())-1));
		
		new BukkitRunnable()
		{
			double nbTours = weapon.getTimeCharging()*20;

			public void run()
			{
				if(!(whatsReloading.containsKey(p) && whatsReloading.get(p).contains(weapon.getName())))
					whatsReloading.get(p).add(weapon.getName());
				
				if(p.isDead() || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
					this.cancel();
					return;
				}
				
				nbTours -= 1;
				
				if(nbTours <= 0)
				{
					p.getInventory().remove(weapon.getItem(1));
					
					if(weapon.name().equals("La Tornade"))
						p.getInventory().setItem(place, ((LaTornade)weapon).getItem(weapon.getMaxMunitions(), p));
					
					else
						p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
					
					this.cancel();
					return;
				}
				
//				else if(nbTours/20 % 1 == 0)
//				{
					p.getInventory().remove(weapon.getItem((int)(0-(nbTours/20)-1)));
					p.getInventory().setItem(place, weapon.getItem(((int)(0-(nbTours/20)))-1));
//				}
			}
			
			@Override
			public void cancel()
			{
				super.cancel();
				whatsReloading.get(p).remove(weapon.getName());
			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);
	}
	
	public static boolean mustReloadUlti(Player p)
	{
		try {
		return !p.getInventory().getItem(6).getType().equals(PlayerManager.getInstance().kitOf(p).getSpecial().getItem(1).getType()) || p.getInventory().getItem(6).getAmount() < 1;
		} catch(Exception e)
		{
			return true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void startReloadUlti(Player p)
	{
		if(reloadUltiFinished.get(p) == null)
		{
			reloadUltiFinished.put(p, false);
		}
		
		if(reloadUltiFinished.get(p))
		{
			return;
		}
		
		
		if(reloadUltiStarted.get(p) == null)
		{
			reloadUltiStarted.put(p, false);
		}
		
		if(reloadUltiStarted.get(p))
		{
			accelererLUlti(p);
			return;
		}
		
		AbstractUltimateWeapon weapon = PlayerManager.getInstance().kitOf(p).getSpecial();
		
		reloadUltiStarted.replace(p, true);
		
		boolean firstTime = p.getInventory().getItem(6) == null || p.getInventory().getItem(6).equals(Kit.cadenas());
		
		int place = 6;
		
		if(p.getInventory().getItem(6) != null && !p.getInventory().getItem(6).equals(Kit.cadenas()) && p.getInventory().getItem(place).getAmount() > 1)
		{
			reloadUltiFinished.remove(p);
			reloadUltiStarted.remove(p);
			stopUlti.remove(p);
			accelererLUlti.remove(p);
			return;
		}
		
		p.getInventory().remove(place);
		p.getInventory().setItem(place, weapon.getItem(firstTime ? -15 : -25));

		new BukkitRunnable()
		{
			double nbTours = (firstTime ? 15 : 25)*20;
			
			public void run()
			{
				if(accelererLUlti.get(p) != null && accelererLUlti.get(p))
				{
					nbTours-=100;
					accelererLUlti.remove(p);
				}
				
				if(TfCommand.cheaters.contains(p))
				{
					nbTours-=1000;
				}
				
				if(stopUlti.get(p) != null && stopUlti.get(p))
				{
					p.getInventory().remove(weapon.getItem(1).getType());
					
					p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
					
					reloadUltiFinished.remove(p);
					reloadUltiStarted.remove(p);
					stopUlti.remove(p);
					accelererLUlti.remove(p);
					this.cancel();
					return;
				}
				
				if(p.isDead() || p.getGameMode().equals(GameMode.SPECTATOR))//TODO bug ulti logs
				{
					reloadUltiFinished.remove(p);
					reloadUltiStarted.remove(p);
					accelererLUlti.remove(p);
					this.cancel();
					return;
				}
				
				nbTours--;
				
				if(nbTours <= 0)
				{
					p.getInventory().remove(weapon.getItem((int)nbTours-1));
					
					p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
					
					this.cancel();
					reloadUltiStarted.replace(p, false);
					reloadUltiFinished.replace(p, true);
				}
				
				else if(nbTours/20 % 1 <= 0)
				{
					p.getInventory().remove(weapon.getItem((int)(firstTime ? -16-nbTours/20 : -26-nbTours/20)));
					p.getInventory().setItem(place, weapon.getItem((int)-nbTours/20));
				}
			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);
	}
	
	public static void accelererLUlti(Player p)
	{
		accelererLUlti.put(p, true);
	}
	
	public static void stopUlti(Player p)
	{
		stopUlti.put(p, true);
	}
	
	public static void deleteUltiFinished(Player p)
	{
		reloadUltiFinished.remove(p);
	}
}
