package fr.senseijuba.survivor.weapons.ultimateWeapons.notDangerous;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.weapons.WeaponManager;

//@SuppressWarnings("deprecation")
public class Disguise extends AbstractNotDangerousUltimateWeapon
{
	public Disguise()
	{
		super("Disguise", Material.LEATHER, 1, 10, false, "", 1, "�6Transformez vous en un ennemi");
	}

	@Override
	public void effect(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 3*9, "Disguise");
		
		for(Player pl : GameManager.getInstance(p.getWorld()).getPlayersOnline())
		{
			if(GameManager.getInstance(p.getWorld()).sameTeam(pl, p))
				continue;
			
			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
			skullMeta.setOwner(pl.getName());
			skullMeta.setDisplayName(GameManager.getInstance(p.getWorld()).getTeamOf(pl).getPrefix()+pl.getName());
			List<String> lore = new ArrayList<>();
			lore.add("�7Se d�guiser en tant que "+GameManager.getInstance(p.getWorld()).getTeamOf(pl).getPrefix()+pl.getName());
			skullMeta.setLore(lore);
			head.setItemMeta(skullMeta);
			
			inv.addItem(head);
		}
		
		p.openInventory(inv);
		
		WeaponManager.stopUlti(p);
	}
	
	public static void disguise(Player p, Player victim)
	{
//		NickNamer.getNickManager().setNick(p.getUniqueId(), GameManager.getInstance(p.getWorld()).getTeamOf(victim).getPrefix()+victim.getName());
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(victim.getName());
		skullMeta.setDisplayName("�6"+victim.getName());
		List<String> lore = new ArrayList<>();
		lore.add("�7D�guis� tant que "+victim.getName());
		skullMeta.setLore(lore);
		head.setItemMeta(skullMeta);
		
		p.getInventory().setHelmet(head);
	}
	
	public static void unDisguise(Player p)
	{
//		NickNamer.getNickManager().setNick(p.getUniqueId(), GameManager.getInstance(p.getWorld()).getTeamOf(p).getPrefix()+p.getName());
		
		PlayerManager.getInstance().setArmorTo(p);
	}
}
