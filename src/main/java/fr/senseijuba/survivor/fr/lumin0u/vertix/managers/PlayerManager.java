package fr.lumin0u.vertix.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import fr.lumin0u.vertix.Kit;
import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.TFPlayer;
import fr.lumin0u.vertix.Team;
import fr.lumin0u.vertix.commands.TfCommand;
import fr.lumin0u.vertix.utils.Cuboid;
import fr.lumin0u.vertix.utils.FireCause;
import fr.lumin0u.vertix.weapons.AbstractWeapon;
import fr.lumin0u.vertix.weapons.WeaponManager;
import fr.lumin0u.vertix.weapons.blocks.C4;
import fr.lumin0u.vertix.weapons.blocks.CanonMontable;
import fr.lumin0u.vertix.weapons.blocks.Mine;

public class PlayerManager
{
	private FileConfiguration file;
	
	private HashMap<String, HashMap<Kit, HashMap<Class<? extends AbstractWeapon>, Integer>>> weaponsPlaces;
	private List<TFPlayer> players;
	private Player modifyZone1;
	private Player modifyZone2;
	private Player bigBlockPoser;
	private Player smallBlockPoser;
	private EulerAngle bigBlockAngle;
	private EulerAngle smallBlockAngle;

	public PlayerManager()
	{
		weaponsPlaces = new HashMap<>();

		File check = new File(TF.getInstance().getDataFolder(), "players.yml");

		if(!TF.getInstance().getDataFolder().exists())
			TF.getInstance().getDataFolder().mkdir();

		try
		{
			if(!check.exists())
				check.createNewFile();

			file = YamlConfiguration.loadConfiguration(check);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(file.contains("weaponsplaces"))
		{
			for(String p : file.getConfigurationSection("weaponsplaces").getKeys(false))
			{
				resetWeaponsPlaces(p);
				
				for(String k : file.getConfigurationSection("weaponsplaces." + p).getKeys(false))
				{
					for(String w : file.getConfigurationSection("weaponsplaces." + p + "." + k).getKeys(false))
					{
//						TF.debug(w + " " + file.getInt("weaponsplaces." + p + "." + k + "." + w));
						weaponsPlaces.get(p).get(Kit.valueOf(k)).replace((Class<? extends AbstractWeapon>)WeaponManager.getInstance().forName(w), file.getInt("weaponsplaces." + p + "." + k + "." + w));
					}
				}
			}
		}

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(TFPlayer victim : players)
				{
					List<UUID> toRemove = new ArrayList<>();

					for(UUID damager : victim.getLastDamagers().keySet())
					{
						if(victim.getLastDamagers().get(damager) > 10)
							toRemove.add(damager);

						else
							victim.getLastDamagers().replace(damager, victim.getLastDamagers().get(damager) + 1);
					}

					for(UUID rm : toRemove)
						victim.getLastDamagers().remove(rm);
				}
			}
		}.runTaskTimer(TF.getInstance(), 20, 20);
	}

	public void saveInFile()
	{
		for(String p : weaponsPlaces.keySet())
			for(Kit k : weaponsPlaces.get(p).keySet())
				for(Class<? extends AbstractWeapon> w : weaponsPlaces.get(p).get(k).keySet())
					file.set("weaponsplaces." + p + "." + k.name() + "." + w.getSimpleName(), weaponsPlaces.get(p).get(k).get(w));

		try
		{
			file.save(new File(TF.getInstance().getDataFolder(), "players.yml"));
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static PlayerManager getInstance()
	{
		return TF.getInstance().getPlayerManager();
	}
	
	public TFPlayer getTFPlayer(Player p)
	{
		for(TFPlayer tfp : players)
			if(tfp.getPlayer().equals(p))
				return tfp;
		
		return null;
	}
	
	public List<TFPlayer> getPlayers()
	{
		return players;
	}

	public void changeKit(Player p, Kit kit)
	{
		if(p.getOpenInventory() == null)
			p.getInventory().clear();

		else
			p.getOpenInventory().getBottomInventory().clear();

		p.setMaxHealth(kit.getMaxHealth() * (TfCommand.cheaters.contains(p) ? 5 : 1));
		p.setHealth(kit.getMaxHealth() * (TfCommand.cheaters.contains(p) ? 5 : 1));

		if(getSP(p) != null && getSP(p).equals(SuperPower.SPEED))
			p.setWalkSpeed(kit.getSpeed() * 2f);

		else
			p.setWalkSpeed(kit.getSpeed());

		for(AbstractWeapon w : kit.getWeapons())
		{
			p.getInventory().setItem(getWeaponsPlaces(p.getName()).get(kit).get(w.getClass()), w.getItem(w.getMaxMunitions()));
		}

		p.getInventory().setItem(6, Kit.cadenas());

		if(!p.getInventory().contains(Kit.boussole(p).getType()))
			p.getInventory().setItem(7, Kit.boussole(p));

		p.getInventory().setItem(8, Kit.nameTag());

		p.getInventory().setHelmet(kit.getBlockOnHead());

		getTFPlayer(p).setKit(kit);
		
		if(kit.equals(Kit.SCOUT))
		{
			p.setAllowFlight(true);
			TF.getInstance().getListener().reloadDoubleJump(p, true);
		}
		
		else
		{
			p.setAllowFlight(false);
			TF.getInstance().getListener().reloadDoubleJump(p, true);
		}
	}

	public void setArmorTo(Player p)
	{
		if(!getTFPlayer(p).isLooking() && getTFPlayer(p).getDisguised().equals(p))
			p.getInventory().setHelmet(getTFPlayer(p).getKit().getBlockOnHead());

		if(!getTFPlayer(p).getKit().getSpecial().getItem(1).isSimilar(p.getInventory().getItem(6)))
		{
			p.getInventory().setItem(6, Kit.cadenas());
			WeaponManager.getInstance().getReloadUltiStarted().put(p, false);
			WeaponManager.getInstance().getReloadUltiFinished().put(p, false);
		}

		// TF.debug(GameManager.getInstance(p.getWorld()));

		if(!p.getInventory().contains(Kit.boussole(p).getType()))
			p.getInventory().setItem(7, Kit.boussole(p));

		p.getInventory().setItem(8, Kit.nameTag());

		Team t = GameManager.getInstance(p.getWorld()).getTeamOf(p);

		if(t == null)
		{
			p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
			p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
			p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));

			return;
		}

		t = GameManager.getInstance(p.getWorld()).getTeamOf(getTFPlayer(p).getDisguised());// getdisguised return p if not disguised

		p.getInventory().setBoots(t.getBoots());
		p.getInventory().setLeggings(t.getLeggings());
		p.getInventory().setChestplate(t.getChestplate());

		if(p.getOpenInventory() == null)
			p.updateInventory();
	}

	public void deleteThingsLikeMineOrTurretOf(Player p)
	{
		if(getTFPlayer(p).getC4Location() != null)
			getTFPlayer(p).getC4Location().getBlock().setType(Material.AIR);
		getTFPlayer(p).setC4Location(null);

		for(Location l : getTFPlayer(p).getMineLocations())
		{
			l.getBlock().setType(Material.AIR);// mines
		}
		getTFPlayer(p).setMineLocations(null);

		if(getTFPlayer(p).getTrampoLocation() != null)
			getTFPlayer(p).getTrampoLocation().getBlock().setType(Material.AIR);
		getTFPlayer(p).setTrampoLocation(null);

		if(getTFPlayer(p).getTurretLocation() != null)
			((CanonMontable)WeaponManager.getInstance().byClass(CanonMontable.class)).forceRemoveCanon(p);// canon montable
	}

	public void explodeThingsLikeMineOrTurretOf(Player p)
	{
		if(getTFPlayer(p).getC4Location() != null)
		{
			((C4)WeaponManager.getInstance().byClass(C4.class)).explode(getTFPlayer(p).getC4Location(), p);
			getTFPlayer(p).getC4Location().getBlock().setType(Material.AIR);
		}
		getTFPlayer(p).setC4Location(null);

		for(Location l : getTFPlayer(p).getMineLocations())
		{
			((Mine)WeaponManager.getInstance().byClass(Mine.class)).explode(getTFPlayer(p).getC4Location(), p);
			l.getBlock().setType(Material.AIR);// mines
		}
		getTFPlayer(p).setMineLocations(null);

		if(getTFPlayer(p).getTrampoLocation() != null)
			getTFPlayer(p).getTrampoLocation().getBlock().setType(Material.AIR);
		getTFPlayer(p).setTrampoLocation(null);

		if(getTFPlayer(p).getTurretLocation() != null)
			((CanonMontable)WeaponManager.getInstance().byClass(CanonMontable.class)).forceRemoveCanon(p);// canon montable
	}

	public void resetGame(Player p)
	{
		deleteThingsLikeMineOrTurretOf(p);

		getPlayers().remove(getTFPlayer(p));
		getPlayers().add(new TFPlayer(p, GameManager.getInstance(p.getWorld())));

		WeaponManager.getInstance().getReloadUltiFinished().remove(p);
		WeaponManager.getInstance().getReloadUltiStarted().remove(p);
	}

	public static Cuboid bodyCub(Player p)
	{
		Cuboid body = new Cuboid(p.getLocation().clone().add(-0.48, 0, -0.48), p.getLocation().clone().add(0.48, 1.5, 0.48));

		return body;
	}

	public static Cuboid headCub(Player p)
	{
		Cuboid head = new Cuboid(p.getLocation().clone().add(-0.40, 1.5, -0.40), p.getLocation().clone().add(0.40, 1.9, 0.40));

		return head;
	}

	public void giveRdPower(Player p)
	{
		SuperPower sp;

		if(GameManager.getInstance(p.getWorld()).getGameType().isCarts())
			sp = SuperPower.getRandomValue();

		else
		{
			do
			{
				sp = SuperPower.getRandomValue();
			}while(sp.isACartPower());
		}

		getTFPlayer(p).setpower(sp);
	}

	public void setBigBlockPoser(Player p)
	{
		bigBlockPoser = p;
	}

	public Player getBigBlockPoser()
	{
		return bigBlockPoser;
	}

	public void setSmallBlockPoser(Player p)
	{
		smallBlockPoser = p;
	}

	public Player getSmallBlockPoser()
	{
		return smallBlockPoser;
	}

	// public void setWood1Poser(Player p)
	// {
	// woodPoser1 = p;
	// }
	//
	// public Player getWood1Poser()
	// {
	// return woodPoser1;
	// }
	//
	// public void setWood2Poser(Player p)
	// {
	// woodPoser2 = p;
	// }
	//
	// public Player getWood2Poser()
	// {
	// return woodPoser2;
	// }
	//
	public void setBigBlockAngle(EulerAngle p)
	{
		bigBlockAngle = p;
	}

	public EulerAngle getBigBlockAngle()
	{
		return bigBlockAngle;
	}

	public void setSmallBlockAngle(EulerAngle p)
	{
		smallBlockAngle = p;
	}

	public EulerAngle getSmallBlockAngle()
	{
		return smallBlockAngle;
	}

	public Player getZone1Modifier()
	{
		return modifyZone1;
	}

	public void setZone1Modifier(Player p)
	{
		modifyZone1 = p;
	}

	public Player getZone2Modifier()
	{
		return modifyZone2;
	}

	public void setZone2Modifier(Player p)
	{
		modifyZone2 = p;
	}

	public HashMap<Kit, HashMap<Class<? extends AbstractWeapon>, Integer>> getWeaponsPlaces(String p)
	{
		if(weaponsPlaces.get(p) == null || weaponsPlaces.get(p).size() < Kit.values().length)
			resetWeaponsPlaces(p);
		
		else
		{
			int normalSize = 0;
			int weaponsPlacesSize = 0;
			
			for(Kit k : Kit.values())
				for(int i = 0; i < k.getWeapons().length; i++)
					normalSize++;
			
			for(Kit k : weaponsPlaces.get(p).keySet())
				for(int i = 0; i < k.getWeapons().length; i++)
					weaponsPlacesSize++;
			
			if(weaponsPlacesSize < normalSize)
				resetWeaponsPlaces(p);
		}

		return weaponsPlaces.get(p);
	}

	public void setWeaponsPlaces(String p, HashMap<Kit, HashMap<Class<? extends AbstractWeapon>, Integer>> weaponsPlaces)
	{
		this.weaponsPlaces.put(p, weaponsPlaces);
	}
	
	public void resetWeaponsPlaces(String p)
	{
		weaponsPlaces.put(p, new HashMap<>());

		for(Kit k : Kit.values())
		{
			weaponsPlaces.get(p).put(k, new HashMap<>());
			
			int i = 0;

			for(AbstractWeapon w : k.getWeapons())
			{
				weaponsPlaces.get(p).get(k).put(w.getClass(), i);
				i++;
			}
		}
	}
}
