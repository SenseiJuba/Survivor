package fr.lumin0u.vertix.weapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Something
{
	protected Material mat;
	protected int munitions;
	protected double timeCharging;
	protected int ratioTir;
	protected String name;
	protected String[] lore;
	protected boolean enchanted;
	protected String sound;
	protected float maxDistance;
	
	public Something(String name, Material mat, int munitions, int ratioTir, boolean enchanted, String sound, float maxDistance, String... lore)
	{
		this.name = name;
		this.mat = mat;
		this.munitions = munitions;
		this.ratioTir = ratioTir;
		this.lore = lore;
		this.enchanted = enchanted;
		this.sound = sound;
		this.maxDistance = maxDistance;
	}
	
	public int getRatioTir()
	{
		return ratioTir;
	}
	
	public String getSound()
	{
		return sound;
	}
	
	public float getMaxDistance()
	{
		return maxDistance;
	}
	
	public String getName()
	{
		return name;
	}
	
	public ItemStack getItem(int amount)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+name);
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
	
	@Override
	public boolean equals(Object obj)
	{
		return obj.getClass().equals(this.getClass());
	}
}
