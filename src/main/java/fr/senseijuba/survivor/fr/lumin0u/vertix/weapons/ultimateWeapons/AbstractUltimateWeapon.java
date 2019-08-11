package fr.lumin0u.vertix.weapons.ultimateWeapons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.lumin0u.vertix.weapons.Something;
import fr.lumin0u.vertix.weapons.WeaponManager;

public abstract class AbstractUltimateWeapon extends Something
{
	public AbstractUltimateWeapon(String name, Material mat, int munitions, int ratioTir, boolean enchanted, String sound, float amplifier, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
	}
	
	@Override
	public ItemStack getItem(int amount)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§5"+name);
		meta.spigot().setUnbreakable(true);
		List<String> loree = new ArrayList<>();
		
		for(int i = 0; i < lore.length; i++)
			loree.add(lore[i]);
		
		meta.setLore(loree);
		item.setItemMeta(meta);
		item.setAmount(amount);
		
		if(enchanted)
		{
			if(mat.equals(Material.POTION))
				item.setDurability((short)1);
			
			else if(mat.equals(Material.GOLDEN_APPLE))
				item.setDurability((short)1);
			
			else
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		}
		
		return item;
	}
	
	public int getMaxMunitions()
	{
		return munitions;
	}
	
	public String name()
	{
		return name;
	}
	
	public String lore()
	{
		return String.join("\n", lore);
	}
	
	@SuppressWarnings("unchecked")
	public static AbstractUltimateWeapon getInstance()
	{
		try
		{
			return (AbstractUltimateWeapon)WeaponManager.getInstance().byClass((Class<? extends Something>)(Class.forName(Thread.currentThread().getStackTrace()[1].getClassName())));
		}catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
