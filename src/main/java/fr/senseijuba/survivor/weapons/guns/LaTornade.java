package fr.senseijuba.survivor.weapons.guns;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.lumin0u.vertix.TF;

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

	@Override
	public void shoot(Player p)
	{
		if(TF.getInstance().getPlayerManager().isLookingHeavy(p))
		{
			enchanted = true;
			precision = scopePres();
		}
		
		else
		{
			enchanted = false;
			precision = noScopePres();
		}
		
		super.shoot(p);
	}
	
	public ItemStack getItem(int amount, Player p)
	{
		return getItem(amount, TF.getInstance().getPlayerManager().isLookingHeavy(p));
	}
	
	public ItemStack getItem(int amount, boolean enchanted)
	{
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.BLUE+name);
		List<String> loree = getLore();
		meta.setLore(loree);
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		item.setAmount(amount);
		
		this.enchanted = enchanted;
		
		if(enchanted)
		{
			item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			item.getItemMeta().addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		
		return item;
	}
}
