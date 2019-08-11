package fr.senseijuba.survivor.weapons.launchableItem;

import java.util.Random;

import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.weapons.AbstractWeapon;

public abstract class AbstractLaunchableItem extends AbstractWeapon
{
	protected long timeBeforeExplosion;
	
	public AbstractLaunchableItem(String name, Material mat, int munitions, double timeCharging, int ratioTir, boolean enchanted, String sound, float amplifier, long timeBeforeExplosion, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);
		this.timeBeforeExplosion = timeBeforeExplosion;
	}
	
	public void launch(Player p)
	{
		ItemStack it = new ItemStack(mat);
		ItemMeta meta = it.getItemMeta();
		meta.setDisplayName(""+new Random().nextInt(10000));
		it.setItemMeta(meta);
		Item i = p.getWorld().dropItem(p.getEyeLocation(), it);
		i.setPickupDelay(60);
		
		i.setVelocity(p.getEyeLocation().getDirection().add(new Vector(0, 0.1, 0)));
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				explode(i.getLocation().clone(), p);
				i.remove();
				this.cancel();
			}
		}.runTaskTimer(Survivor.getInstance(), timeBeforeExplosion, timeBeforeExplosion);
	}
	
	public abstract void explode(Location l, Player launcher);
	
	public Material getLaunched()
	{
		return mat;
	}
}
