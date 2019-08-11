package fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.weapons.WeaponManager;

public class Disguise extends AbstractNotDangerousUltimateWeapon
{
	public Disguise()
	{
		super("Troubles", Material.LEATHER, 1, 10, false, "", 1, "§6Choisissez une abilitée");
	}

	@Override
	public void effect(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 2*9, "Choisissez une abilitée");
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setDisplayName("§5Disguise");
		List<String> lore = new ArrayList<>();
		lore.add("§7Choisissez une personne en");
		lore.add("§7qui vous déguiser !");
		skullMeta.setLore(lore);
		head.setItemMeta(skullMeta);
		
		ItemStack barrier = new ItemStack(Material.BARRIER);
		ItemMeta barrierMeta = barrier.getItemMeta();
		barrierMeta.setDisplayName("§5Decharge electrique");
		List<String> barrierLore = new ArrayList<>();
		barrierLore.add("§7Empechez tout système ennemis de");
		barrierLore.add("§7fonctionner ! (Canon montable, C4 ...)");
		barrierMeta.setLore(barrierLore);
		barrier.setItemMeta(barrierMeta);
		
		inv.setItem(9, head);
		inv.setItem(10 ,barrier);
		
		p.openInventory(inv);
		
		WeaponManager.getInstance().stopUlti(p);
	}
	
	public void decharge(Player p)
	{
		GameManager gm = GameManager.getInstance(p.getWorld());
		
		for(Player pl : p.getWorld().getPlayers())
		{
			if(!gm.sameTeam(p, pl))
			{
				gm.getTeamOf(pl).setBlocksUsable(false);
				
				if(PlayerManager.getInstance().c4LocationOf(pl) != null)
					p.getWorld().strikeLightningEffect(PlayerManager.getInstance().c4LocationOf(pl));
				
				if(PlayerManager.getInstance().turretLocationOf(pl) != null)
					p.getWorld().strikeLightningEffect(PlayerManager.getInstance().turretLocationOf(pl));
				
				if(PlayerManager.getInstance().trampoLocationOf(pl) != null)
					p.getWorld().strikeLightningEffect(PlayerManager.getInstance().trampoLocationOf(pl));
				
				if(PlayerManager.getInstance().mineLocationsOf(pl) != null && !PlayerManager.getInstance().mineLocationsOf(pl).isEmpty())
					for(Location l : PlayerManager.getInstance().mineLocationsOf(pl))
						p.getWorld().strikeLightningEffect(l);
			}
		}
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(Player pl : p.getWorld().getPlayers())
				{
					if(!gm.sameTeam(p, pl))
						gm.getTeamOf(pl).setBlocksUsable(true);
				}
			}
		}.runTaskLater(TF.getInstance(), 300);
	}
	
	public void disguise(Player p, Player victim)
	{
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setOwner(victim.getName());
		skullMeta.setDisplayName("§6"+victim.getName());
		List<String> lore = new ArrayList<>();
		lore.add("§7Déguisé tant que "+victim.getName());
		skullMeta.setLore(lore);
		head.setItemMeta(skullMeta);
		
		p.getInventory().setHelmet(head);
	}
	
	public void unDisguise(Player p)
	{
		PlayerManager.getInstance().setArmorTo(p);
	}
}
