package fr.senseijuba.survivor.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
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

	private HashMap<UUID, Boolean> isLooking;
	private HashMap<UUID, Boolean> isLookingHeavy;
	private HashMap<UUID, Boolean> tipsActive;
	private HashMap<UUID, Kit> playerKits;
	private HashMap<UUID, Kit> playerNextKits;
	private HashMap<UUID, Location> c4Location;
	private HashMap<UUID, Location> turretLocation;
	private HashMap<UUID, Location> trampoLocation;
	private HashMap<UUID, List<Location>> mineLocations;
	private HashMap<UUID, Boolean> isInvisible;
	private HashMap<UUID, Boolean> isInvicible;
	private HashMap<UUID, HashMap<UUID, Integer>> lastDamagers;
	private HashMap<UUID, Long> lastHitDate;
	private HashMap<UUID, SuperPower> powers;
	private HashMap<UUID, Integer> money;
	@Getter private HashMap<UUID, Integer> kills;
	@Getter private HashMap<UUID, Integer> deaths;
	private HashMap<Player, Player> disguised;
	private HashMap<Player, Team> teamCartDirection;
	private HashMap<UUID, FireCause> fireCause;
	private HashMap<Player, Integer> heavyBulletNb;
	private HashMap<String, HashMap<Kit, HashMap<Class<? extends AbstractWeapon>, Integer>>> weaponsPlaces;
	private Player modifyZone1;
	private Player modifyZone2;
	private Player bigBlockPoser;
	private Player smallBlockPoser;
	private EulerAngle bigBlockAngle;
	private EulerAngle smallBlockAngle;

	public PlayerManager()
	{
		isLooking = new HashMap<>();
		isLookingHeavy = new HashMap<>();
		tipsActive = new HashMap<>();
		playerKits = new HashMap<>();
		playerNextKits = new HashMap<>();
		c4Location = new HashMap<>();
		turretLocation = new HashMap<>();
		trampoLocation = new HashMap<>();
		mineLocations = new HashMap<>();
		isInvisible = new HashMap<>();
		isInvicible = new HashMap<>();
		lastDamagers = new HashMap<>();
		fireCause = new HashMap<>();
		lastHitDate = new HashMap<>();
		disguised = new HashMap<>();
		teamCartDirection = new HashMap<>();
		powers = new HashMap<>();
		heavyBulletNb = new HashMap<>();
		weaponsPlaces = new HashMap<>();
		money = new HashMap<>();
		kills = new HashMap<>();
		deaths = new HashMap<>();

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
				for(UUID victim : lastDamagers.keySet())
				{
					List<UUID> toRemove = new ArrayList<>();

					for(UUID damager : lastDamagers.get(victim).keySet())
					{
						if(lastDamagers.get(victim).get(damager) > 10)
							toRemove.add(damager);

						else
							lastDamagers.get(victim).replace(damager, lastDamagers.get(victim).get(damager) + 1);
					}

					for(UUID rm : toRemove)
						lastDamagers.get(victim).remove(rm);
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

	public boolean isLooking(Player p)
	{
		if(isLooking.get(p.getUniqueId()) == null)
		{
			isLooking.put(p.getUniqueId(), false);
			return false;
		}

		return isLooking.get(p.getUniqueId());
	}

	public void setLooking(Player p, boolean looking)
	{
		if(isLooking.get(p.getUniqueId()) == null)
		{
			isLooking.put(p.getUniqueId(), looking);
		}

		isLooking.replace(p.getUniqueId(), looking);
	}

	public boolean isLookingHeavy(Player p)
	{
		if(isLookingHeavy.get(p.getUniqueId()) == null)
		{
			isLookingHeavy.put(p.getUniqueId(), false);
			return false;
		}

		return isLookingHeavy.get(p.getUniqueId());
	}

	public void setLookingHeavy(Player p, boolean lookingHeavy)
	{
		if(isLookingHeavy.get(p.getUniqueId()) == null)
		{
			isLookingHeavy.put(p.getUniqueId(), lookingHeavy);
		}

		isLookingHeavy.replace(p.getUniqueId(), lookingHeavy);
	}

	public boolean hasTipsActived(Player p)
	{
		if(tipsActive.get(p.getUniqueId()) == null)
		{
			tipsActive.put(p.getUniqueId(), true);
			return true;
		}

		return tipsActive.get(p.getUniqueId());
	}

	public void setTipsActive(Player p, boolean tipsActived)
	{
		if(tipsActive.get(p.getUniqueId()) == null)
		{
			tipsActive.put(p.getUniqueId(), tipsActived);
		}

		tipsActive.replace(p.getUniqueId(), tipsActived);
	}

	public Kit kitOf(Player p)
	{
		if(playerKits.get(p.getUniqueId()) == null)
		{
			playerKits.put(p.getUniqueId(), Kit.NOKIT);
			return Kit.NOKIT;
		}

		else
			return playerKits.get(p.getUniqueId());
	}

	public void setKitOf(Player p, Kit k)
	{
		if(playerKits.get(p.getUniqueId()) == null)
		{
			playerKits.put(p.getUniqueId(), k);
		}

		playerKits.replace(p.getUniqueId(), k);
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

		setKitOf(p, kit);
		
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

	public Kit nextKit(Player p)
	{
		if(playerNextKits.get(p.getUniqueId()) == null)
		{
			playerNextKits.put(p.getUniqueId(), Kit.NOKIT);
			return Kit.NOKIT;
		}

		else
			return playerNextKits.get(p.getUniqueId());
	}

	public void setNextKit(Player p, Kit k)
	{
		if(playerNextKits.get(p.getUniqueId()) == null)
		{
			playerNextKits.put(p.getUniqueId(), k);
		}

		playerNextKits.replace(p.getUniqueId(), k);
	}

	public Location c4LocationOf(Player p)
	{
		return c4Location.get(p.getUniqueId());
	}

	public void setC4Location(Player p, Location l)
	{
		if(c4Location.get(p.getUniqueId()) == null)
		{
			c4Location.put(p.getUniqueId(), l);
		}

		c4Location.replace(p.getUniqueId(), l);
	}

	public FireCause getFireCauseOf(Player p)
	{
		if(fireCause.containsKey(p.getUniqueId()))
			return fireCause.get(p.getUniqueId());

		else
			return new FireCause(true, null, null);
	}

	public void setFireCauseOf(Player p, FireCause cause)
	{
		fireCause.put(p.getUniqueId(), cause);
	}

	public boolean isInvisible(Player p)
	{
		if(isInvisible.get(p.getUniqueId()) == null)
		{
			isInvisible.put(p.getUniqueId(), false);
			return false;
		}

		else
			return isInvisible.get(p.getUniqueId());
	}

	public void setIsInvisible(Player p, boolean k)
	{
		if(isInvisible.get(p.getUniqueId()) == null)
		{
			isInvisible.put(p.getUniqueId(), k);
		}

		isInvisible.replace(p.getUniqueId(), k);
	}

	public boolean isInvicible(Player p)
	{
		if(isInvicible.get(p.getUniqueId()) == null)
		{
			isInvicible.put(p.getUniqueId(), false);
			return false;
		}

		else
			return isInvicible.get(p.getUniqueId());
	}

	public void setIsInvicible(Player p, boolean k)
	{
		if(isInvicible.get(p.getUniqueId()) == null)
		{
			isInvicible.put(p.getUniqueId(), k);
		}

		isInvicible.replace(p.getUniqueId(), k);
	}

	public List<Location> mineLocationsOf(Player p)
	{
		if(mineLocations.get(p.getUniqueId()) == null)
		{
			mineLocations.put(p.getUniqueId(), new ArrayList<Location>());
			return new ArrayList<Location>();
		}

		else
			return mineLocations.get(p.getUniqueId());
	}

	public void setMineLocations(Player p, List<Location> l)
	{
		if(mineLocations.get(p.getUniqueId()) == null)
		{
			mineLocations.put(p.getUniqueId(), l);
		}

		mineLocations.replace(p.getUniqueId(), l);
	}

	public Location turretLocationOf(Player p)
	{
		return turretLocation.get(p.getUniqueId());
	}

	public void setTurretLocation(Player p, Location l)
	{
		if(turretLocation.get(p.getUniqueId()) == null)
		{
			turretLocation.put(p.getUniqueId(), l);
		}

		turretLocation.replace(p.getUniqueId(), l);
	}

	public Location trampoLocationOf(Player p)
	{
		return trampoLocation.get(p.getUniqueId());
	}

	public void setTrampoLocation(Player p, Location l)
	{
		if(trampoLocation.get(p.getUniqueId()) == null)
		{
			trampoLocation.put(p.getUniqueId(), l);
		}

		trampoLocation.replace(p.getUniqueId(), l);
	}

	public void setArmorTo(Player p)
	{
		if(!isLooking(p) && getDisguised(p).equals(p))
			p.getInventory().setHelmet(kitOf(p).getBlockOnHead());

		if(!kitOf(p).getSpecial().getItem(1).isSimilar(p.getInventory().getItem(6)))
		{
			p.getInventory().setItem(6, Kit.cadenas());
			WeaponManager.reloadUltiStarted.put(p, false);
			WeaponManager.reloadUltiFinished.put(p, false);
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

		t = GameManager.getInstance(p.getWorld()).getTeamOf(getDisguised(p));// getdisguised return p if not disguised

		p.getInventory().setBoots(t.getBoots());
		p.getInventory().setLeggings(t.getLeggings());
		p.getInventory().setChestplate(t.getChestplate());

		if(p.getOpenInventory() == null)
			p.updateInventory();
	}

	public void addDamagerTo(Player victim, Player damager)
	{
		if(lastDamagers.get(victim.getUniqueId()) == null)
		{
			lastDamagers.put(victim.getUniqueId(), new HashMap<>());
			lastDamagers.get(victim.getUniqueId()).put(damager.getUniqueId(), 0);
		}

		else if(lastDamagers.get(victim.getUniqueId()).keySet().contains(damager.getUniqueId()))
		{
			lastDamagers.get(victim.getUniqueId()).replace(damager.getUniqueId(), 0);
		}

		else
		{
			lastDamagers.get(victim.getUniqueId()).put(damager.getUniqueId(), 0);
		}
	}

	public List<Player> getLastDamagersOf(Player p)
	{
		if(!lastDamagers.containsKey(p.getUniqueId()))
			return new ArrayList<>();

		List<Player> damagers = new ArrayList<>();

		for(UUID pl : lastDamagers.get(p.getUniqueId()).keySet())
			damagers.add(Bukkit.getPlayer(pl));

		return damagers;
	}

	public HashMap<UUID, Integer> getLastDamagersAndTheirTimeOf(Player p)
	{
		return lastDamagers.get(p.getUniqueId());
	}

	public void rmDamagersTo(Player p)
	{
		lastDamagers.remove(p.getUniqueId());
	}

	public void setDisguise(Player p, Player disguised)
	{
		if(p == disguised)
			disguised = null;

		if(!this.disguised.containsKey(p) && disguised != null)
			this.disguised.put(p, disguised);

		else if(disguised != null)
			this.disguised.replace(p, disguised);

		else if(this.disguised.containsKey(p))
			this.disguised.remove(p);
	}

	public Player getDisguised(Player p)
	{
		if(disguised.get(p) != null)
			return disguised.get(p);

		else
			return p;
	}

	public void deleteThingsLikeMineOrTurretOf(Player p)
	{
		if(c4LocationOf(p) != null)
		{
			c4LocationOf(p).getBlock().setType(Material.AIR);
		}
		setC4Location(p, null);

		for(Location l : mineLocationsOf(p))
		{
			l.getBlock().setType(Material.AIR);// mines
		}
		setMineLocations(p, null);

		if(trampoLocationOf(p) != null)
			trampoLocationOf(p).getBlock().setType(Material.AIR);
		setTrampoLocation(p, null);// trampo

		if(turretLocationOf(p) != null)
			CanonMontable.forceRemoveCanon(p);// canon montable
	}

	public void explodeThingsLikeMineOrTurretOf(Player p)
	{
		if(c4LocationOf(p) != null)
		{
			new C4().explode(c4LocationOf(p), p);
			c4LocationOf(p).getBlock().setType(Material.AIR);
		}
		setC4Location(p, null);

		for(Location l : mineLocationsOf(p))
		{
			if(l != null)
				Mine.explode(l, p);// mines
		}
		setMineLocations(p, null);

		if(trampoLocationOf(p) != null)
			trampoLocationOf(p).getBlock().setType(Material.AIR);
		setTrampoLocation(p, null);// trampo

		if(turretLocationOf(p) != null)
			CanonMontable.forceRemoveCanon(p);// canon montable
	}

	public void resetGame(Player p)
	{
		deleteThingsLikeMineOrTurretOf(p);

		isLooking.remove(p.getUniqueId());
		isLookingHeavy.remove(p.getUniqueId());
		playerKits.remove(p.getUniqueId());
		playerNextKits.remove(p.getUniqueId());
		c4Location.remove(p.getUniqueId());
		turretLocation.remove(p.getUniqueId());
		trampoLocation.remove(p.getUniqueId());
		mineLocations.remove(p.getUniqueId());
		isInvisible.remove(p.getUniqueId());
		isInvicible.remove(p.getUniqueId());
		lastDamagers.remove(p.getUniqueId());
		fireCause.remove(p.getUniqueId());
		lastHitDate.remove(p.getUniqueId());
		disguised.remove(p);
		teamCartDirection.remove(p);
		powers.remove(p.getUniqueId());
		heavyBulletNb.remove(p);

		WeaponManager.reloadUltiFinished.remove(p);
		WeaponManager.reloadUltiStarted.remove(p);
	}

	public long lastHitDate(Player p)
	{
		if(lastHitDate.containsKey(p.getUniqueId()) && !TfCommand.cheaters.contains(p))
			return lastHitDate.get(p.getUniqueId());

		else
			return 0;
	}

	public void setHitDate(Player p, long date)
	{
		if(!lastHitDate.containsKey(p.getUniqueId()))
		{
			lastHitDate.put(p.getUniqueId(), date);
		}

		lastHitDate.replace(p.getUniqueId(), date);
	}

	public void setTeamCartDirection(Player p, Team t)
	{
		if(!teamCartDirection.containsKey(p))
			teamCartDirection.put(p, t);

		else
			teamCartDirection.replace(p, t);
	}

	public Team getTeamCartDirection(Player p)
	{
		if(teamCartDirection.containsKey(p))
			return teamCartDirection.get(p);

		else if(GameManager.getInstance(p.getWorld()).getTeamOf(p) != null)
			return GameManager.getInstance(p.getWorld()).getTeamOf(p);

		else
			return GameManager.getInstance(p.getWorld()).getTeams().get(0);
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

		powers.put(p.getUniqueId(), sp);
	}

	public void setPower(Player p, SuperPower sp)
	{
		powers.put(p.getUniqueId(), sp);
	}

	public SuperPower getSP(Player p)
	{
		return powers.get(p.getUniqueId());
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

	public int getHeavyBulletNb(Player p)
	{
		if(!heavyBulletNb.containsKey(p))
			heavyBulletNb.put(p, 0);

		return heavyBulletNb.get(p);
	}

	public void addHeavyBulletNb(Player p)
	{
		if(!heavyBulletNb.containsKey(p))
			heavyBulletNb.put(p, 0);

		heavyBulletNb.replace(p, heavyBulletNb.get(p) + 1);
	}

	public void setHeavyBulletNb(Player p, int nb)
	{
		heavyBulletNb.put(p, nb);
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

	public HashMap<UUID, Integer> getMoney() {
		return money;
	}

	public void setMoney(HashMap<UUID, Integer> money) {
		this.money = money;
	}

	public void addKills(Player player){
		this.kills.put(player.getUniqueId(), this.kills.get(player.getUniqueId()) + 1);
	}

	public void addDeaths(Player player){
		this.deaths.put(player.getUniqueId(), this.deaths.get(player.getUniqueId()) + 1);
	}
}
