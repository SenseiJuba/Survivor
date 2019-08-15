package fr.senseijuba.survivor.weapons;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.commands.SurvivorCommand;
import fr.senseijuba.survivor.managers.GameManager;
import fr.senseijuba.survivor.utils.Utils;
import fr.senseijuba.survivor.weapons.guns.*;
import fr.senseijuba.survivor.weapons.guns.shotguns.OLYMPIA;
import fr.senseijuba.survivor.weapons.guns.shotguns.REMINGTON;
import fr.senseijuba.survivor.weapons.guns.shotguns.SPAS12;
import fr.senseijuba.survivor.weapons.thingsLauncher.AbstractThingLauncher;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WeaponManager
{
	private static List<AbstractWeapon> weapons;
	private static Survivor survivor = Survivor.getInstance();
	
	private static HashMap<Player, Boolean> accelererLUlti = new HashMap<>();
	private static HashMap<Player, Boolean> stopUlti = new HashMap<>();
	private static HashMap<Player, List<String>> whatsReloading = new HashMap<>();
	public static HashMap<Player, Boolean> reloadUltiStarted = new HashMap<>();
	public static HashMap<Player, Boolean> reloadUltiFinished = new HashMap<>();//TODO barbecue reload
	
	public WeaponManager()
	{
		weapons = new ArrayList<>();
		registerWeapons();
	}
	
	private void registerWeapons()
	{
		registerWeapon(new AK47());
		registerWeapon(new CARABINE());
		registerWeapon(new DRAGUNOV());
		registerWeapon(new EXECUTEUR());
		registerWeapon(new FAMAS());
		registerWeapon(new LSAT());
		registerWeapon(new M14());
		registerWeapon(new M16());
		registerWeapon(new M1911());
		registerWeapon(new MG08());
		registerWeapon(new MP5());
		registerWeapon(new MPL());
		registerWeapon(new SCAR());
		registerWeapon(new UMP45());
		registerWeapon(new OLYMPIA());
		registerWeapon(new REMINGTON());
		registerWeapon(new SPAS12());
	}

	private void registerWeapon(AbstractWeapon w)
	{
		weapons.add(w);
	}
	
	public static List<AbstractWeapon> listWeapons()
	{
		return weapons;
	}

	public List<Something> listThings()
	{
		List<Something> things = new ArrayList<Something>(weapons);

		return things;
	}
	
	public static boolean doEffectAndReload(ItemStack eItem, Player p, UUID pid, long differenceTemps) //TODO UNDERSTAND
	{
		if(!GameManager.getInstance(p.getWorld()).getPlayers().contains(p.getUniqueId()))
			return false;
		
//		Survivor.debug(p.getGameMode());

//		if(p.getGameMode().equals(GameMode.SPECTATOR))
//		{
//			if(Survivor.getInstance().getListener().rClickingPlayersTask.containsKey(pid))
//			{
//				Survivor.getInstance().getListener().rClickingPlayersTask.get(pid).cancel();
//				Survivor.getInstance().getListener().rClickingPlayersTask.remove(pid);
//			}
//			
////			Survivor.debug("CANCEL");
//			return false;
//		}
		
//		GameManager.doDeathEffect(p, p, GameManager.getInstance(p.getWorld()));
		
		boolean cancel = false;
		
		Something g = null;
		ItemStack gunItemNonFinal = null;
		
		if(eItem != null && eItem.hasItemMeta() && eItem.getItemMeta().getDisplayName() != null)
		{
			for(AbstractWeapon gu : Survivor.getInstance().getPlayerWeapon().get(pid))
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
		
		Survivor.getInstance().getL().rClickingPlayersTask.put(pid, new BukkitRunnable()//DEBUT DE LA BOUCLE
		{
			boolean cancel = false;
			
			@Override
			public void run()
			{
//				Survivor.debug(cancel);
				
				if(!p.getGameMode().equals(GameMode.SPECTATOR))
					cancel = false;
				
				if(cancel)
				{
					super.cancel();
					return;
				}
				
				ItemStack gunItem = p.getInventory().getItem(weaponSlot);
				
				if(Survivor.getInstance().getL().rClickingPlayersTaskId.get(pid) == null)
					Survivor.getInstance().getL().rClickingPlayersTaskId.put(pid, this.getTaskId());
				
				if(Survivor.getInstance().getL().rClickingPlayersLast.get(pid) + differenceTemps < System.currentTimeMillis() || !p.getItemInHand().getType().equals(gunItemtype) || Survivor.getInstance().getL().rClickingPlayersTaskId.get(pid) != this.getTaskId() || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					if(Survivor.getInstance().getL().rClickingPlayersTaskId.get(pid) == this.getTaskId())
						Survivor.getInstance().getL().rClickingPlayersTaskId.remove(pid);
					
					Survivor.getInstance().getL().rClickingPlayersTask.remove(pid);
					super.cancel();
					return;
				}
				
				if(weapon instanceof AbstractGun)
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
						}
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractGun)
							((AbstractGun)weapon).shoot(p);


						Utils.playSound(p.getLocation(), weapon.getSound(), weapon.getMaxDistance());

						p.updateInventory();
					}
				}

				
				else if(weapon instanceof AbstractThingLauncher)
				{
					if(gunItem.getAmount() == 1)
					{
						if(weapon instanceof AbstractWeapon)
							reload(p, (AbstractWeapon)weapon);
					}

					else
						if(gunItem.getAmount() > 0)
							gunItem.setAmount(gunItem.getAmount() - 1);

					if(gunItem.getAmount() > 0)
					{
						if(weapon instanceof AbstractThingLauncher)
							((AbstractThingLauncher)weapon).launch(p);
						
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
		
		Survivor.getInstance().getL().rClickingPlayersTask.get(pid).runTaskTimer(Survivor.getInstance(), 0, g.getRatioTir());
		
		return cancel;
	}
	
	public static void reload(Player p, AbstractWeapon weapon)
	{
		reload(p, weapon, p.getInventory().first(weapon.getItem(1).getType()));
	}
	
	public static void reload(Player p, AbstractWeapon weapon, int place)
	{
		if(SurvivorCommand.cheaters.contains(p))
		{
			p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
			return;
		}
		
		if(!whatsReloading.containsKey(p))
			whatsReloading.put(p, new ArrayList<>());
		
		if(whatsReloading.containsKey(p) && whatsReloading.get(p).contains(weapon.getName()))
			return;

		int reloadingTime = Survivor.getInstance().getPlayerManager().hasSpeedCola(p) ?  0 - (int)(weapon.getTimeCharging()/2)-1 : 0 - (int)(weapon.getTimeCharging())-1;

		p.getInventory().remove(weapon.getItem(1));
		p.getInventory().setItem(place, weapon.getItem(reloadingTime));

		int munitions = weapon.getCurrentMaxMunitions()-weapon.getMunitions() >= 0 ? weapon.getCurrentMaxMunitions()-weapon.getMunitions() : weapon.getCurrentMaxMunitions();

		new BukkitRunnable()
		{
			double nbTours = weapon.getTimeCharging()*20;

			public void run()
			{
				if(!(whatsReloading.containsKey(p) && whatsReloading.get(p).contains(weapon.getName())))
					whatsReloading.get(p).add(weapon.getName());
				
				if(p.isDead() || p.getGameMode().equals(GameMode.SPECTATOR))
				{
					p.getInventory().setItem(place, weapon.getItem(munitions));
					weapon.setCurrentMaxMunitions(weapon.getCurrentMaxMunitions()-munitions);

					this.cancel();
					return;
				}
				
				nbTours -= 1;
				
				if(nbTours <= 0)
				{
					p.getInventory().remove(weapon.getItem(1));

					p.getInventory().setItem(place, weapon.getItem(weapon.getMaxMunitions()));
					
					this.cancel();
					return;
				}
			}
			
			@Override
			public void cancel()
			{
				super.cancel();
				whatsReloading.get(p).remove(weapon.getName());
			}
		}.runTaskTimer(Survivor.getInstance(), 1l, 1l);
	}
}
