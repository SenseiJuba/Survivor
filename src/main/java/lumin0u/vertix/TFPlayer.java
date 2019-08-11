package fr.lumin0u.vertix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.utils.FireCause;

public class TFPlayer
{
	private UUID p;
	private GameManager assigned;
	private boolean isLooking;
	private boolean isLookingHeavy;
	private boolean tipsActive;
	private Kit kit;
	private Kit nextKit;
	private Location c4Location;
	private Location turretLocation;
	private Location trampoLocation;
	private List<Location> mineLocations;
	private boolean isInvisible;
	private boolean isInvicible;
	private HashMap<UUID, Integer> lastDamagers;
	private long lastHitDate;
	private SuperPower power;
	private Player disguised;
	private Team teamCartDirection;
	private FireCause fireCause;
	private int heavyBulletNb;
	private boolean randomKit;
	private boolean canDoubleJump;
	
	public TFPlayer(Player p)
	{
		this.p = p.getUniqueId();
		this.assigned = GameManager.getInstance(p.getWorld());
		
		this.isLooking = false;
		this.isLookingHeavy = false;
		this.tipsActive = true;
		this.kit = Kit.NOKIT;
		this.nextKit = Kit.NOKIT;
		this.isInvisible = false;
		this.isInvicible = false;
		this.lastDamagers = new HashMap<>();
		this.mineLocations = new ArrayList<>();
		this.lastHitDate = 0;
		this.disguised = p;
		this.teamCartDirection = assigned.getTeamOf(p);
		this.fireCause = new FireCause(true, null, null);
		this.heavyBulletNb = 0;
		this.randomKit = false;
		this.canDoubleJump = false;
	}

	public Player getPlayer()
	{
		return Bukkit.getPlayer(p);
	}
	
	public Team getTeam()
	{
		return assigned.getTeamOf(getPlayer());
	}
	
	public GameManager getAssigned()
	{
		return assigned;
	}
	
	public boolean isLooking()
	{
		return isLooking;
	}

	public void setLooking(boolean looking)
	{
		isLooking = looking;
	}

	public boolean isLookingHeavy()
	{
		return isLookingHeavy;
	}

	public void setLookingHeavy(boolean lookingHeavy)
	{
		isLookingHeavy = lookingHeavy;
	}

	public boolean hasTipsActive()
	{
		return tipsActive;
	}

	public void setTipsActive(boolean tipsActive)
	{
		this.tipsActive = tipsActive;
	}

	public Kit getKit()
	{
		return kit;
	}

	public void setKit(Kit kit)
	{
		this.kit = kit;
	}

	public Kit getNextKit()
	{
		return nextKit;
	}

	public void setNextKit(Kit nextKit)
	{
		this.nextKit = nextKit;
	}

	public Location getC4Location()
	{
		return c4Location;
	}

	public void setC4Location(Location c4Location)
	{
		this.c4Location = c4Location;
	}

	public Location getTurretLocation()
	{
		return turretLocation;
	}

	public void setTurretLocation(Location turretLocation)
	{
		this.turretLocation = turretLocation;
	}

	public Location getTrampoLocation()
	{
		return trampoLocation;
	}

	public void setTrampoLocation(Location trampoLocation)
	{
		this.trampoLocation = trampoLocation;
	}

	public List<Location> getMineLocations()
	{
		return mineLocations;
	}

	public void setMineLocations(List<Location> mineLocations)
	{
		this.mineLocations = mineLocations;
	}

	public boolean isInvisible()
	{
		return isInvisible;
	}

	public void setInvisible(boolean isInvisible)
	{
		this.isInvisible = isInvisible;
	}

	public boolean isInvicible()
	{
		return isInvicible;
	}

	public void setInvicible(boolean isInvicible)
	{
		this.isInvicible = isInvicible;
	}

	public HashMap<UUID, Integer> getLastDamagers()
	{
		return lastDamagers;
	}

	public void setLastDamagers(HashMap<UUID, Integer> lastDamagers)
	{
		this.lastDamagers = lastDamagers;
	}

	public void addDamager(Player damager)
	{
		lastDamagers.put(damager.getUniqueId(), 0);
	}

	public long getLastHitDate()
	{
		return lastHitDate;
	}

	public void setLastHitDate(long lastHitDate)
	{
		this.lastHitDate = lastHitDate;
	}

	public SuperPower getpower()
	{
		return power;
	}

	public void setpower(SuperPower power)
	{
		this.power = power;
	}

	public Player getDisguised()
	{
		return disguised;
	}

	public void setDisguised(Player disguised)
	{
		this.disguised = disguised;
	}

	public Team getTeamCartDirection()
	{
		return teamCartDirection;
	}

	public void setTeamCartDirection(Team teamCartDirection)
	{
		this.teamCartDirection = teamCartDirection;
	}

	public FireCause getFireCause()
	{
		return fireCause;
	}

	public void setFireCause(FireCause fireCause)
	{
		this.fireCause = fireCause;
	}

	public int getHeavyBulletNb()
	{
		return heavyBulletNb;
	}

	public void setHeavyBulletNb(int heavyBulletNb)
	{
		this.heavyBulletNb = heavyBulletNb;
	}
	
	public boolean hasRandomKit()
	{
		return randomKit;
	}
	
	public void setRandomKit(boolean randomKit)
	{
		this.randomKit = randomKit;
	}
	
	public boolean canDoubleJump()
	{
		return canDoubleJump;
	}
	
	public void setCanDoubleJump(boolean canDoubleJump)
	{
		this.canDoubleJump = canDoubleJump;
	}

	@Override
	public String toString()
	{
		return "TFPlayer [p=" + p + ", assigned=" + assigned + ", kit=" + kit + "]";
	}
}
