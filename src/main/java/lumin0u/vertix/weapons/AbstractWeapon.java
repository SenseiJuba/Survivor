package fr.lumin0u.vertix.weapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.lumin0u.vertix.weapons.guns.LaTornade;
import fr.lumin0u.vertix.weapons.guns.MitrailletteLourde;

public abstract class AbstractWeapon extends Something
{
	protected double timeCharging;
	
	public AbstractWeapon(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
		this.timeCharging = timeCharging;
		
		if(ratioTir < 3 && !(this instanceof MitrailletteLourde) && !(this instanceof LaTornade))
			this.ratioTir = 3;
	}
	
	@Override
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
		return munitions;
	}
	
	public double getTimeCharging()
	{
		return timeCharging;
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
