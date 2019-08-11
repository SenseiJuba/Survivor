package fr.senseijuba.survivor.weapons;

import java.util.ArrayList;
import java.util.List;

import fr.senseijuba.survivor.weapons.guns.LaTornade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class AbstractWeapon extends Something
{
	protected double timeCharging;
	
	public AbstractWeapon(String name, Material mat, int munitions, int maxMunitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, maxMunitions, ratioTir, enchanted, sound, amplifier, lore);
		this.timeCharging = timeCharging;
		
		if(ratioTir < 3 && !(this instanceof MitrailletteLourde) && !(this instanceof LaTornade))
			this.ratioTir = 3;
	}
	
	public ItemStack getItem(int amount)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+name);
		List<String> loree = getLore();
		meta.setLore(loree);
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item.setAmount(amount);
		
		if(enchanted)
		{
			if(mat.equals(Material.POTION))
				item.setDurability((short)1);
			
			else
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
			
			item.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		return item;
	}
	
	public int getMaxMunitions()
	{
		return maxMunitions;
	}

	pubic int getMunitions() { return munitions; }
	
	public double getTimeCharging()
	{
		return timeCharging;
	}
	
	public String name()
	{
		return name;
	}
	
	public List<String> getLore()
	{
		return loreAdd();
	}
	
	public List<String> loreAdd()
	{
		List<String> loree = new ArrayList<>();
		
		for(int i = 0; i < lore.length; i++)
			loree.add(lore[i]);
		
		return loree;
	}
}
