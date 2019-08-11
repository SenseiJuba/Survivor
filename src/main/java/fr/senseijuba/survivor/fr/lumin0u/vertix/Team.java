package fr.lumin0u.vertix;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import fr.lumin0u.vertix.utils.Cuboid;
import fr.lumin0u.vertix.utils.Utils;

public class Team
{
	private String name;
	private String prefix;
	private List<UUID> players;
	private Location spawnpoint;
	private Cuboid safeZone;
	private World w;
	private DyeColor dyeColor;
	private Material blockInCart;
	private int kills;
	private ItemStack repItem;
	private int place;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	
	private Player modifySZ1;
	private Player modifySZ2;
	private Player modifyStartRails;
	private Player modifyEndRails;
	private Player addBifurc;
	private Player modifyFinalTerminus;
	
	private boolean blocksUsable;
	
	public Team(String name, String prefix, Location spawnpoint, Cuboid safeZone, World w, ItemStack chestplate, ItemStack leggings, ItemStack boots, Material blockInCart, DyeColor dyeColor, int place, Material repItem)
	{
		construct(name, prefix, spawnpoint, safeZone, w, chestplate, leggings, boots, blockInCart, dyeColor, place, repItem);
	}

	public Team(int id, Material blockInCart, Location spawnpoint, Cuboid safeZone, World w)
	{
		String name = "ERROR";
		String prefix = "§4";
		
		ItemStack chestplate;
		ItemStack leggings;
		ItemStack boots;
		DyeColor dyeColor;
		int place;
		Material repItem = null;
		
		if(id == 0)
		{
			name = "rouge";
			prefix = "§c";
			place = 41;
			
			dyeColor = DyeColor.RED;
			
			chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
			boots = new ItemStack(Material.CHAINMAIL_BOOTS);
		}
		else if(id == 1)
		{
			name = "bleu";
			prefix = "§9";
			place = 39;
			
			dyeColor = DyeColor.BLUE;
			
			chestplate = new ItemStack(Material.IRON_CHESTPLATE);
			leggings = new ItemStack(Material.IRON_LEGGINGS);
			boots = new ItemStack(Material.IRON_BOOTS);
		}
		else if(id == 2)
		{
			name = "vert";
			prefix = "§a";
			place = 31;
			
			dyeColor = DyeColor.GREEN;
			
			chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta chestplateMeta = (LeatherArmorMeta)chestplate.getItemMeta();
			chestplateMeta.setColor(Color.GREEN);
			chestplate.setItemMeta(chestplateMeta);
			
			leggings = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta leggingsMeta = (LeatherArmorMeta)leggings.getItemMeta();
			leggingsMeta.setColor(Color.GREEN);
			leggings.setItemMeta(leggingsMeta);
			
			boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta bootsMeta = (LeatherArmorMeta)boots.getItemMeta();
			bootsMeta.setColor(Color.GREEN);
			boots.setItemMeta(bootsMeta);
		}
		else if(id == 3)
		{
			name = "jaune";
			prefix = "§e";
			place = 49;
			repItem = Material.POTATO_ITEM;
			
			dyeColor = DyeColor.YELLOW;
			
			chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta chestplateMeta = (LeatherArmorMeta)chestplate.getItemMeta();
			chestplateMeta.setColor(Color.YELLOW);
			chestplate.setItemMeta(chestplateMeta);
			
			leggings = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta leggingsMeta = (LeatherArmorMeta)leggings.getItemMeta();
			leggingsMeta.setColor(Color.YELLOW);
			leggings.setItemMeta(leggingsMeta);
			
			boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta bootsMeta = (LeatherArmorMeta)boots.getItemMeta();
			bootsMeta.setColor(Color.YELLOW);
			boots.setItemMeta(bootsMeta);
			
			
			blockInCart = Material.CARPET;
		}
		
		else
		{
			chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta chestplateMeta = (LeatherArmorMeta)chestplate.getItemMeta();
			chestplateMeta.setColor(Color.OLIVE);
			chestplate.setItemMeta(chestplateMeta);
			
			leggings = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta leggingsMeta = (LeatherArmorMeta)leggings.getItemMeta();
			leggingsMeta.setColor(Color.NAVY);
			leggings.setItemMeta(leggingsMeta);
			
			boots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta bootsMeta = (LeatherArmorMeta)boots.getItemMeta();
			bootsMeta.setColor(Color.SILVER);
			boots.setItemMeta(bootsMeta);
			
			
			blockInCart = Material.CARPET;
			dyeColor = DyeColor.BROWN;
			place = 0;
		}
		
		construct(name, prefix, spawnpoint, safeZone, w, chestplate, leggings, boots, blockInCart, dyeColor, place, repItem);
	}
	
	private void construct(String name, String prefix, Location spawnpoint, Cuboid safeZone, World w, ItemStack chestplate, ItemStack leggings, ItemStack boots, Material blockInCart, DyeColor dyeColor, int place, Material repItem)
	{
		players = new ArrayList<>();
		this.name = name;
		this.prefix = prefix;
		this.spawnpoint = spawnpoint;
		this.safeZone = safeZone;
		this.w = w;
		this.chestplate = chestplate;
		this.leggings = leggings;
		this.boots = boots;
		this.blockInCart = blockInCart;
		this.dyeColor = dyeColor;
		this.place = place;
		kills = 0;
		blocksUsable = true;
		
		buildItemRep(repItem);
	}
	
	public String getCharFR()
	{
		return new String(new char[]{name.toCharArray()[0]}).toUpperCase();
	}

	public String getName(boolean f)
	{
		if(f && (name.equals("bleu") || name.equals("vert")))
			return name+"e";
		
		else
			return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public List<UUID> getPlayers()
	{
		return players;
	}

	public List<Player> getPlayersOnline()
	{
		List<Player> players = new ArrayList<>();
		
		for(UUID uuid : this.players)
			if(w.getPlayers().contains(Bukkit.getPlayer(uuid)))
				players.add(Bukkit.getPlayer(uuid));
		
		return players;
	}

	public Location getSpawnpoint()
	{
		return spawnpoint;
	}

	public Cuboid getSafeZone()
	{
		return safeZone;
	}
	
	public void setSpawnPoint(Location spawnPoint)
	{
		this.spawnpoint = spawnPoint;
	}

	public void setSafeZone(Cuboid safeZone)
	{
		this.safeZone = safeZone;
	}
	
	public ItemStack getChestplate()
	{
		return chestplate.clone();
	}

	public ItemStack getLeggings()
	{
		return leggings.clone();
	}

	public ItemStack getBoots()
	{
		return boots.clone();
	}

	public boolean isModifyingSZ1(Player p)
	{
		if(modifySZ1 != null && modifySZ1.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setSZ1Modifier(Player p)
	{
		modifySZ1 = p;
	}
	
	public boolean isModifyingSZ2(Player p)
	{
		if(modifySZ2 != null && modifySZ2.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setSZ2Modifier(Player p)
	{
		modifySZ2 = p;
	}
	
	public boolean isModifyingStartRails(Player p)
	{
		if(modifyStartRails != null && modifyStartRails.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setStartRailsModifier(Player p)
	{
		modifyStartRails = p;
	}
	
	public boolean isModifyingEndRails(Player p)
	{
		if(modifyEndRails != null && modifyEndRails.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setEndRailsModifier(Player p)
	{
		modifyEndRails = p;
	}
	
	public boolean isModifyingFinalTerminus(Player p)
	{
		if(modifyFinalTerminus != null && modifyFinalTerminus.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setFinalTerminusModifier(Player p)
	{
		modifyFinalTerminus = p;
	}
	
	public boolean isAddingBifurc(Player p)
	{
		if(addBifurc != null && addBifurc.equals(p))
			return true;
		
		else
			return false;
	}
	
	public void setBifurcAdder(Player p)
	{
		addBifurc = p;
	}
	
	public Material getBlockInCart()
	{
		return blockInCart;
	}
	
	public DyeColor getDyeColor()
	{
		return dyeColor;
	}
	
	public void saveInConfig()
	{
		ConfigurationSection f = TF.getInstance().getConfig(w);
		
		if(safeZone != null)
		{
			f.set(name+".szun", Utils.locToString(safeZone.getLoc1()));
			f.set(name+".szdeux", Utils.locToString(safeZone.getLoc2()));
		}
		
		if(spawnpoint != null)
			f.set(name+".spawnpoint", Utils.locDirToString(spawnpoint));
		
		
		
		TF.getInstance().saveTheConfig(f, w);
		
	}
	
	public World getWorld()
	{
		return w;
	}
	
	public int getKills()
	{
		return kills;
	}
	
	public void addKill(int kills)
	{
		this.kills += kills;
	}
	
	public ItemStack getRepItem()
	{
		return repItem;
	}
	
	public int getPlace()
	{
		return place;
	}
	
	public void setBlocksUsable(boolean blocksUsable)
	{
		this.blocksUsable = blocksUsable;
	}
	
	public boolean areBlocksUsable()
	{
		return blocksUsable;
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack buildItemRep(Material mat)
	{
		ItemStack item;
		
		if(mat != null)
			item = new ItemStack(mat);
		else
			item = new ItemStack(Material.INK_SACK, 1, getDyeColor().getDyeData());
		
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(getPrefix()+"Rejoindre l'équipe "+getName(true).toUpperCase());
		item.setItemMeta(itemMeta);
		
		repItem = item;
		
		return item;
	}

	@Override
	public String toString()
	{
		return "Team [name=" + name + ", w=" + w + "]";
	}
}
