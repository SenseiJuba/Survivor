package fr.lumin0u.vertix.weapons.guns;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LaTornade extends AbstractGun
{
	public LaTornade()
	{
		super("La Tornade", Material.GOLD_PICKAXE, 1, -2, 3, 1.5, 40, noScopePres(), false, "guns.latornade", 20, 5);
	}
	
	public static double noScopePres()
	{
		return 0.8;
	}
	
	public static double scopePres()
	{
		return 0.04;
	}
	
	public static ItemStack getItem(int amount, boolean enchanted)
	{
		ItemStack item = new LaTornade().getItem(amount);
		ItemMeta meta = item.getItemMeta();
		
		if(enchanted)
		{
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
}
