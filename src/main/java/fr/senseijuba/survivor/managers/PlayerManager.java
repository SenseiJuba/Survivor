package fr.senseijuba.survivor.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.commands.SurvivorCommand;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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

	private HashMap<UUID, Boolean> tipsActive;
	private HashMap<UUID, Boolean> hasDoubleCoup;
	private HashMap<UUID, Boolean> hasSpeedCola;
	private HashMap<UUID, Boolean> hasThreeWeapons;
	private HashMap<UUID, Boolean> hasQuickRevive;
	private HashMap<UUID, Boolean> hasGrave;
	private HashMap<UUID, Integer> money;
	private HashMap<UUID, HashMap<Location, ArmorStand>> deadbody;
	@Getter private HashMap<UUID, Integer> kills;
	@Getter private HashMap<UUID, Integer> deaths;
	private HashMap<UUID, FireCause> fireCause;
	private HashMap<Player, Integer> heavyBulletNb;
	private Player modifyZone1;
	private Player modifyZone2;

	public PlayerManager()
	{
		tipsActive = new HashMap<>();
		hasDoubleCoup = new HashMap<>();
		hasSpeedCola = new HashMap<>();
		hasThreeWeapons = new HashMap<>();
		hasQuickRevive = new HashMap<>();
		hasGrave = new HashMap<>();
		fireCause = new HashMap<>();
		heavyBulletNb = new HashMap<>();
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
	}

	public void saveInFile()
	{
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
		return Survivor.getInstance().getPlayerManager();
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

	public boolean hasDoubleCoup(Player p)
	{
		if(hasDoubleCoup.get(p.getUniqueId()) == null)
		{
			hasDoubleCoup.put(p.getUniqueId(), false);
			return false;
		}

		else
			return hasDoubleCoup.get(p.getUniqueId());
	}

	public void setDoubleCoup(Player p, boolean k)
	{
		if(hasDoubleCoup.get(p.getUniqueId()) == null)
		{
			hasDoubleCoup.put(p.getUniqueId(), k);
		}

		hasDoubleCoup.replace(p.getUniqueId(), k);
	}

	public boolean hasQuickRevive(Player p)
	{
		if(hasQuickRevive.get(p.getUniqueId()) == null)
		{
			hasQuickRevive.put(p.getUniqueId(), false);
			return false;
		}

		else
			return hasQuickRevive.get(p.getUniqueId());
	}

	public void setQuickRevive(Player p, boolean k)
	{
		if(hasQuickRevive.get(p.getUniqueId()) == null)
		{
			hasQuickRevive.put(p.getUniqueId(), k);
		}

		hasQuickRevive.replace(p.getUniqueId(), k);
	}

	public boolean hasThreeWeapons(Player p)
	{
		if(hasThreeWeapons.get(p.getUniqueId()) == null)
		{
			hasThreeWeapons.put(p.getUniqueId(), false);
			return false;
		}

		else
			return hasThreeWeapons.get(p.getUniqueId());
	}

	public void setThreeWeapons(Player p, boolean k)
	{
		if(hasThreeWeapons.get(p.getUniqueId()) == null)
		{
			hasThreeWeapons.put(p.getUniqueId(), k);
		}

		hasThreeWeapons.replace(p.getUniqueId(), k);
	}
	public boolean hasSpeedCola(Player p)
	{
		if(hasSpeedCola.get(p.getUniqueId()) == null)
		{
			hasSpeedCola.put(p.getUniqueId(), false);
			return false;
		}

		else
			return hasSpeedCola.get(p.getUniqueId());
	}

	public void setSpeedCola(Player p, boolean k)
	{
		if(hasSpeedCola.get(p.getUniqueId()) == null)
		{
			hasSpeedCola.put(p.getUniqueId(), k);
		}

		hasSpeedCola.replace(p.getUniqueId(), k);
	}

	public boolean hasGrave(Player p)
	{
		if(hasGrave.get(p.getUniqueId()) == null)
		{
			hasGrave.put(p.getUniqueId(), false);
			return false;
		}

		else
			return hasGrave.get(p.getUniqueId());
	}

	public void setGrave(Player p, boolean k)
	{
		if(hasGrave.get(p.getUniqueId()) == null)
		{
			hasGrave.put(p.getUniqueId(), k);
		}

		hasGrave.replace(p.getUniqueId(), k);
	}



	public void resetGame(Player p)
	{

		hasDoubleCoup.remove(p.getUniqueId());
		hasSpeedCola.remove(p.getUniqueId());
		fireCause.remove(p.getUniqueId());
		heavyBulletNb.remove(p);
	}

	public static Cuboid bodyCub(Entity p)
	{
		Cuboid body = new Cuboid(p.getLocation().clone().add(-0.48, 0, -0.48), p.getLocation().clone().add(0.48, 1.5, 0.48));

		return body;
	}

	public static Cuboid headCub(Entity p)
	{
		Cuboid head = new Cuboid(p.getLocation().clone().add(-0.40, 1.5, -0.40), p.getLocation().clone().add(0.40, 1.9, 0.40));

		return head;
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


	public HashMap<UUID, Integer> getMoney() {
		return money;
	}

	public void addMoney(Player player, int money){
		this.money.replace(player.getUniqueId(), getMoney().get(player.getUniqueId()) + money);
	}

	public void setMoney(HashMap<UUID, Integer> money) {
		this.money = money;
	}

	public void addKills(UUID player, int count){
		this.kills.put(player, this.kills.get(player) + count);
	}

	public void addDeaths(UUID player, int count){
		this.deaths.put(player, this.deaths.get(player) + count);
	}

	public void initDeathKills(){
		for(Player player : Bukkit.getOnlinePlayers()){
			kills.put(player.getUniqueId(), 0);
			deaths.put(player.getUniqueId(), 0);
		}
	}
}
