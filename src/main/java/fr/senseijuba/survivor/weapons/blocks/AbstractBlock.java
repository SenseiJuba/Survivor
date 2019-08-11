package fr.senseijuba.survivor.weapons.blocks;

import fr.senseijuba.survivor.weapons.AbstractWeapon;
import fr.senseijuba.survivor.weapons.WeaponManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;



public abstract class AbstractBlock extends AbstractWeapon
{
	protected AbstractWeapon replacement;
	protected Material block;
	
	public AbstractBlock(String name, Material mat, int munitions, int maxMunitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, AbstractWeapon replacement, Material block, String... lore)
	{
		super(name, mat, munitions, maxMunitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
		
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
				WeaponManager.reload(p, replacement);
			}
			catch(Exception e) {}
		}
		
		else if(item.getAmount() == 1 && replacement == null)
			p.getInventory().remove(item);
		
		p.updateInventory();
	}
}
