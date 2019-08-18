package fr.senseijuba.survivor.weapons;

import lombok.Getter;
import lombok.Setter;
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
	protected int maxMunitions;
	@Getter @Setter
	protected int currentMunitions;
	@Getter @Setter
	protected int currentMaxMunitions;
	protected double timeCharging;
	protected int ratioTir;
	protected String name;
	protected String[] lore;
	protected boolean enchanted;
	protected String sound;
	protected float maxDistance;
	
	public Something(String name, Material mat, int munitions, int maxMunitions, int ratioTir, boolean enchanted, String sound, float maxDistance, String... lore)
	{
		this.name = name;
		this.mat = mat;
		this.munitions = munitions;
		this.maxMunitions = maxMunitions;
		this.currentMunitions = munitions;
		this.currentMaxMunitions = maxMunitions;
		this.ratioTir = ratioTir;
		this.lore = lore;
		this.enchanted = enchanted;
		this.sound = sound;
		this.maxDistance = maxDistance;
	}

	public ItemStack getItem(int amount)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+name);
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
}
