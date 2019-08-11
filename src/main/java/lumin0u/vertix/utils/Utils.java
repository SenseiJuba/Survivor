package fr.lumin0u.vertix.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;

public class Utils
{
	// private static HashMap<UUID, String> nicked = new HashMap<>();

	public static void playSound(Location l, Sound sound, float maxDistance)
	{
		for(Player p : l.getWorld().getPlayers())
		{
			if(!p.getWorld().equals(l.getWorld()))
				continue;

			playSound(p, l, sound, maxDistance);
		}
	}

	public static void playSound(Player p, Location l, Sound sound, float maxDistance)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				float amplifier = (float)(1.0f - p.getEyeLocation().distance(l) / maxDistance);

				p.playSound(p.getEyeLocation(), sound, amplifier, 1);
				this.cancel();
			}
		}.runTaskLater(TF.getInstance(), (long)p.getEyeLocation().distance(l) / 17);
	}

	public static void playSound(Location l, String sound, float maxDistance)
	{
		for(Player p : l.getWorld().getPlayers())
		{
			if(!p.getWorld().equals(l.getWorld()))
				continue;

			playSound(p, l, sound, maxDistance);
		}
	}

	public static void playSound(Player p, Location l, String sound, float maxDistance)
	{
		new BukkitRunnable()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				float amplifier = (float)(1.0f - p.getEyeLocation().distance(l) / maxDistance);

				p.playSound(p.getEyeLocation(), sound, amplifier, 1);
				this.cancel();
			}
		}.runTaskLater(TF.getInstance(), (long)p.getEyeLocation().distance(l) / 17);
	}

	public static String locToString(Location loc)
	{
		return loc.getWorld().getName() + "!" + loc.getX() + "!" + loc.getY() + "!" + loc.getZ();
	}

	public static String locDirToString(Location loc)
	{
		return loc.getWorld().getName() + "!" + loc.getX() + "!" + loc.getY() + "!" + loc.getZ() + "!" + loc.getDirection().getX() + "!" + loc.getDirection().getY() + "!" + loc.getDirection().getZ();
	}

	public static Location stringToLoc(String loc)
	{
		if(loc.split("!").length < 7)
			return new Location(Bukkit.getWorld(loc.split("!")[0]), Double.parseDouble(loc.split("!")[1]), Double.parseDouble(loc.split("!")[2]), Double.parseDouble(loc.split("!")[3]));
		
		else
			return new Location(Bukkit.getWorld(loc.split("!")[0]), Double.parseDouble(loc.split("!")[1]), Double.parseDouble(loc.split("!")[2]), Double.parseDouble(loc.split("!")[3])).setDirection(new Vector(Double.parseDouble(loc.split("!")[4]), Double.parseDouble(loc.split("!")[5]), Double.parseDouble(loc.split("!")[6])));
	}

	public static Vector explosionVector(Entity ent, Location l, double radius)
	{
		Location entLoc = ent.getLocation().clone().add(0, 1, 0);

		double x = (entLoc.getX() - l.getX()) / entLoc.distance(l) * (radius - entLoc.distance(l));
		double y = (entLoc.getY() - l.getY()) / entLoc.distance(l) * (radius - entLoc.distance(l));
		double z = (entLoc.getZ() - l.getZ()) / entLoc.distance(l) * (radius - entLoc.distance(l));

		return new Vector(x, y, z).multiply(0.03);
	}

	public static void broadcastMessage(World w, String message)
	{
		for(Player p : w.getPlayers())
			p.sendMessage(message);
	}

	public static void explosionParticles(Location l, float rayon, int nbParticlesOfEach, Effect... particles)
	{
		Random r = new Random();

		double angle1 = new Random().nextDouble() * 2 * Math.PI;
		double angle2 = new Random().nextDouble() * 2 * Math.PI - Math.PI / 2;

		double x = Math.cos(angle1) * Math.cos(angle2);
		double z = Math.sin(angle1) * Math.cos(angle2);
		double y = Math.sin(angle2);

		float m = 0;

		for(Effect particle : particles)
		{
			for(int i = 0; i < nbParticlesOfEach; i++)
			{
				angle1 = r.nextDouble() * 2 * Math.PI;
				angle2 = r.nextDouble() * 2 * Math.PI - Math.PI / 2;

				x = Math.cos(angle1) * Math.cos(angle2);
				z = Math.sin(angle1) * Math.cos(angle2);
				y = Math.sin(angle2);

				m = r.nextFloat();

				l.getWorld().spigot().playEffect(l, particle, 0, 0, (float)((x * rayon / 10.0f) * m), (float)((y * rayon / 10.0f) * m), (float)((z * rayon / 10.0f) * m), 1, 0, 150);
			}
		}
	}
	
	public static Vector vectorFrom(Location l1, Location l2)
	{
		return l2.toVector().subtract(l1.toVector());
	}
	
	public static boolean startsLikely(String s1, String s2)
	{
		try {
			for(int i = 0; i < s1.toCharArray().length; i++)
				if(!((Character)s1.toCharArray()[i]).toString().equalsIgnoreCase(((Character)s2.toCharArray()[i]).toString()))
					return false;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
		
		return true;
	}
	
	public static List<Location> pointsBetweeen(Location l1, Location l2, Material... transparent)
	{
		List<Material> transparents = new ArrayList<>();
		
		for(Material mat : transparent)
			transparents.add(mat);
		
		return pointsBetween(l1, l2, transparents);
	}
	
	public static List<Location> pointsBetween(Location l1, Location l2, List<Material> transparent)
	{
		Vector increase = vectorFrom(l1, l2).normalize().multiply(0.1);
		
		List<Location> points = new ArrayList<>();
		
		if(transparent == null)
		{
			transparent = new ArrayList<>();
			transparent.add(Material.AIR);
		}
		
		Location point = l1.clone();
		
		for(int i = 0; i < 10000; i++)
		{
			point.add(increase);
			
			if(!transparent.contains(point.getBlock().getType()))
				points.add(point.clone());
			
			if(point.distance(l2) < 0.2)
				break;
		}
		
		return points;
	}
	
	private static double angle1;
	private static double angle2;
	private static double x;
	private static double z;
	private static double y;
	private static Random rd = new Random();
	
	public static Vector rdVector()
	{
		angle1 = rd.nextDouble() * 2 * Math.PI;
		angle2 = rd.nextDouble() * 2 * Math.PI - Math.PI / 2;

		x = Math.cos(angle1) * Math.cos(angle2);
		z = Math.sin(angle1) * Math.cos(angle2);
		y = Math.sin(angle2);
		
		return new Vector(x, y, z);
	}
	
	public static boolean areSimilar(ItemStack i1, ItemStack i2)
	{
		return i1 == null || i2 == null || (i1.getDurability() == i2.getDurability() && i1.getType().equals(i2.getType()) && ChatColor.stripColor(i1.getItemMeta().getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(i2.getItemMeta().getDisplayName())));
	}
}
