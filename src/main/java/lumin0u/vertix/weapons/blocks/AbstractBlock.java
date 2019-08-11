package fr.lumin0u.vertix.weapons.blocks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.lumin0u.vertix.weapons.AbstractWeapon;
import fr.lumin0u.vertix.weapons.WeaponManager;

public abstract class AbstractBlock extends AbstractWeapon
{
	protected AbstractWeapon replacement;
	protected Material block;
	
	public AbstractBlock(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, AbstractWeapon replacement, Material block, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
		
		this.replacement = replacement;
		this.block = block;
	}
	
	public AbstractWeapon getReplacement()
	{
		return replacement;
	}
	
	public Material getBlock()
	{
		return block;
	}
	
	public void onPlaceBlock(Player p, ItemStack item)
	{
		if(item.getAmount() > 1)
			item.setAmount(item.getAmount()-1);
		
		else if(item.getAmount() == 1 && replacement != null)
		{
			try {
				p.getInventory().setItem(p.getInventory().first(getItem(1).getType()), replacement.getItem(replacement.getMaxMunitions()));
				WeaponManager.getInstance().reload(p, replacement);
			}
			catch(Exception e) {}
		}
		
		else if(item.getAmount() == 1 && replacement == null)
			p.getInventory().remove(item);
		
		p.updateInventory();
	}
}
