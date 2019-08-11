package fr.senseijuba.survivor.weapons.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import fr.senseijuba.survivor.weapons.notDangerous.Manette;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.TF;
import fr.lumin0u.vertix.commands.TfCommand;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.senseijuba.survivor.utils.Title;
import fr.lumin0u.vertix.utils.Utils;

public class CanonMontable extends AbstractBlock
{
	private static HashMap<UUID, Integer> playersTurrets = new HashMap<>();
	private static HashMap<UUID, Vector> playersLastDirection = new HashMap<>();
	private static HashMap<UUID, Vector> playersLastDirectionF = new HashMap<>();
	private static HashMap<UUID, Boolean> playerCanShootTurret = new HashMap<>();

	public CanonMontable()
	{
		super("Canon Montable", Material.CARPET, 1, 0, 10, false, "mortier.poser", 5, new Manette(), Material.CARPET, "�6Montez un canon pour tirer des", "�6projectile explosifs");
	}
	
	@Override
	public void onPlaceBlock(Player p, ItemStack item)
	{
		p.getInventory().remove(item);
		
		p.updateInventory();
	}

	public void build(Player p, Location l)
	{
		PlayerManager.getInstance().setTurretLocation(p, l.getBlock().getLocation());

		ArmorStand c = p.getWorld().spawn(l, ArmorStand.class);
		c.setGravity(false);
		c.setCustomName("0%");
		c.setCustomNameVisible(true);
		c.setVisible(false);

		c.teleport(l);

		if(!playersTurrets.containsKey(p.getUniqueId()))
			playersTurrets.put(p.getUniqueId(), 0);

		else
			playersTurrets.replace(p.getUniqueId(), 0);

		new BukkitRunnable()
		{
			private int nbTours = 0;

			@Override
			public void run()
			{
				if(nbTours == 0)
					if(playersTurrets.containsKey(p.getUniqueId()))
						playersTurrets.replace(p.getUniqueId(), this.getTaskId());

				nbTours++;

				c.setCustomName(nbTours + "%");

				if(!playersTurrets.containsKey(p.getUniqueId()))
				{
					cancel();
					return;
				}

				if(nbTours >= 101)
				{
					p.getInventory().addItem(new Manette().getItem(1));

					c.setCustomNameVisible(false);
					c.setHelmet(new ItemStack(Material.DROPPER));
					c.setCustomName("Head of turret #" + this.getTaskId());

					ArmorStand ready = p.getWorld().spawn(c.getLocation(), ArmorStand.class);

					ready.setCustomName("Ready");
					ready.setCustomNameVisible(true);
					ready.setVisible(false);
					ready.setGravity(false);

					playerCanShootTurret.put(p.getUniqueId(), true);

					this.cancel();
				}

			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);
	}

	public static void setDirection(Player p)
	{
		ArmorStand c = null;

		for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
			if(playersTurrets.containsKey(p.getUniqueId()) && ent.getCustomName() != null && ent.getCustomName().equals("Head of turret #" + playersTurrets.get(p.getUniqueId())))
				c = ent;

		if(c == null || c.getLocation().distance(p.getLocation()) > 10)
		{
			Title.sendActionBar(p, "�cRapprochez vous de votre canon");

			return;
		}
		
		Utils.playSound(p.getLocation(), "mortier.direction", 5);

		EulerAngle eu = new EulerAngle(p.getLocation().getPitch() / (18 * Math.PI), p.getLocation().getYaw() / (18 * Math.PI), 0);

		if(!playersLastDirection.containsKey(p.getUniqueId()))
		{
			playersLastDirection.put(p.getUniqueId(), p.getEyeLocation().getDirection());
			playersLastDirectionF.put(p.getUniqueId(), p.getEyeLocation().getDirection());
		}

		else
		{
			playersLastDirection.replace(p.getUniqueId(), p.getEyeLocation().getDirection());
			playersLastDirectionF.replace(p.getUniqueId(), p.getEyeLocation().getDirection());
		}

		c.setHeadPose(eu);
	}

	public static void shoot(Player p)
	{
		if(!playersLastDirection.containsKey(p.getUniqueId()))
		{
			playersLastDirection.put(p.getUniqueId(), new Vector(0, 0, 1));
			playersLastDirectionF.put(p.getUniqueId(), new Vector(0, 0, 1));
		}

		if(!playerCanShootTurret.containsKey(p.getUniqueId()))
			playerCanShootTurret.put(p.getUniqueId(), true);

		else if(!playerCanShootTurret.get(p.getUniqueId()))
			return;

		ArmorStand c = null;

		for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
			if(playersTurrets.containsKey(p.getUniqueId()) && ent.getCustomName() != null && ent.getCustomName().equals("Head of turret #" + playersTurrets.get(p.getUniqueId())))
				c = ent;

		if(c == null || c.getLocation().distance(p.getLocation()) > 10)
		{
			Title.sendActionBar(p, "�cRapprochez vous de votre canon");

			return;
		}

		c.getWorld().playEffect(c.getEyeLocation(), Effect.EXPLOSION_LARGE, 0);

		Random rand = new Random();

		float x = (float)(c.getEyeLocation().clone().subtract(playersLastDirectionF.get(p.getUniqueId())).subtract(c.getEyeLocation()).getX());
		float y = (float)(c.getEyeLocation().clone().subtract(playersLastDirectionF.get(p.getUniqueId())).subtract(c.getEyeLocation()).getY());
		float z = (float)(c.getEyeLocation().clone().subtract(playersLastDirectionF.get(p.getUniqueId())).subtract(c.getEyeLocation()).getZ());

		for(int i = 0; i < 100; i++)
		{
			c.getWorld().spigot().playEffect(c.getEyeLocation(), Effect.CLOUD, 0, 0, (float)(x + rand.nextDouble() - 0.5), (float)(y + rand.nextDouble() - 0.5), (float)(z + rand.nextDouble() - 0.5), Math.abs(rand.nextFloat()), 0, 10);
		}

		ArmorStand ball = p.getWorld().spawn(c.getLocation().add(0, 0, 0), ArmorStand.class);

		ball.setHelmet(new ItemStack(Material.QUARTZ_ORE));
		ball.setGravity(false);
		ball.setCustomName("Ball launched by " + p.getName());
		ball.setVisible(false);

//		if(p.getName().equals("lumin0u") && new Random().nextInt(10) == 5)
//		{
//			ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
//			SkullMeta skullMeta = (SkullMeta)head.getItemMeta();
//			skullMeta.setOwner("lumin0u");
//			head.setItemMeta(skullMeta);
//			ball.setHelmet(head);
//		}

//		if(new Random().nextInt(1000) == 563)
//			ball.setHelmet(new ItemStack(Material.DROPPER));

		ball.teleport(c.getLocation().add(playersLastDirection.get(p.getUniqueId())));

		List<Location> list = calculateTrajectory(playersLastDirectionF.get(p.getUniqueId()), ball.getEyeLocation());

		Utils.playSound(p.getLocation(), "mortier.lancer", 50);

		new BukkitRunnable()
		{
			int nbTours = 0;

			@Override
			public void run()
			{
				nbTours++;

				if(list.size() <= nbTours + 1)
				{
					// EXPLOSION

					Location l;
					
					try {
						l = list.get(nbTours - 1);
					}
					catch(Exception e)
					{
						l = ball.getEyeLocation();
					}

					for(Player ent : l.getWorld().getPlayers())
					{
						if(ent.getLocation().distance(l) <= 13)
						{
							GameManager.damageTF(ent, p, (13 - ent.getLocation().distance(l)) / 13 * 24, Utils.explosionVector(ent, l, 15).multiply(7));
						}
					}

					Utils.playSound(ball.getLocation(), "mortier.obus", 100);

					Utils.explosionParticles(l, (float)(15 / 1.5), 20 * (int)15, Effect.FLAME, Effect.LARGE_SMOKE, Effect.CLOUD);

					for(int i = 0; i < 50; i++)
						ball.getWorld().playEffect(ball.getEyeLocation().clone().add((((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5, (((double)new Random().nextInt(500)) / 100.0) - 2.5), Effect.LAVA_POP, 0);

					// EXPLOSION

					ball.setHealth(0);
					ball.setHelmet(new ItemStack(Material.AIR));
					this.cancel();
					return;
				}

				ball.teleport(list.get(nbTours));
			}
		}.runTaskTimer(TF.getInstance(), 1l, 1l);

		if(!TfCommand.cheaters.contains(p))
		{
			if(!playerCanShootTurret.containsKey(p.getUniqueId()))
				playerCanShootTurret.put(p.getUniqueId(), false);

			else
				playerCanShootTurret.replace(p.getUniqueId(), false);

			reloadCannon(p, c.getLocation());
		}
	}

	public static void reloadCannon(Player p, Location cannonLoc)
	{
		ArmorStand r = null;

		for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
			if(ent.getCustomName() != null && ent.getCustomName().equals("Ready"))
				r = ent;

		if(r != null && r.getLocation().distance(cannonLoc) < 1)
		{
			r.setHealth(0);
			r.setCustomNameVisible(false);
		}

		ArmorStand wait = p.getWorld().spawn(cannonLoc, ArmorStand.class);

		wait.setCustomName("8");
		wait.setCustomNameVisible(true);
		wait.setVisible(false);
		wait.setGravity(false);

		final ArmorStand wFinal = wait;

		new BukkitRunnable()
		{
			int nbTours = 0;

			@Override
			public void run()
			{
				nbTours++;

				if(!playersTurrets.containsKey(p.getUniqueId()))
				{
					cancel();
					return;
				}

				if(nbTours == 8)
				{
					wFinal.setHealth(0);
					wFinal.setCustomNameVisible(false);
					playerCanShootTurret.replace(p.getUniqueId(), true);

					boolean ok = true;

					for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
						if(!(ent.getCustomName() != null && !ent.getCustomName().equals("Ready") || ent.getLocation().distance(wFinal.getLocation()) >= 1))
							ok = false;

					if(ok)
					{
						ArmorStand ready = p.getWorld().spawn(wait.getLocation(), ArmorStand.class);

						ready.setCustomName("Ready");
						ready.setCustomNameVisible(true);
						ready.setVisible(false);
						ready.setGravity(false);
					}

					this.cancel();
					return;
				}

				wFinal.setCustomName(8 - nbTours + "");
			}
		}.runTaskTimer(TF.getInstance(), 20l, 20l);
	}

	public static boolean removeCanon(Player p)
	{
		if(!playerCanShootTurret.containsKey(p.getUniqueId()) || !playerCanShootTurret.get(p.getUniqueId()))
			return false;

		forceRemoveCanon(p);

		return true;
	}

	public static void forceRemoveCanon(Player p)
	{
		playerCanShootTurret.remove(p.getUniqueId());
		playersLastDirection.remove(p.getUniqueId());
		playersLastDirectionF.remove(p.getUniqueId());
		playersTurrets.remove(p.getUniqueId());

		Location loc = PlayerManager.getInstance().turretLocationOf(p).clone();

		loc.getBlock().setType(Material.AIR);

		PlayerManager.getInstance().setTurretLocation(p, null);

		for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
		{
			if(ent.getLocation().distance(loc) < 2)
			{
				// ((ArmorStand)ent).setHealth(0);
				ent.remove();
			}
		}

		new BukkitRunnable()
		{
			int i = 0;

			@Override
			public void run()
			{
				for(ArmorStand ent : p.getWorld().getEntitiesByClass(ArmorStand.class))
				{
					if(ent.getLocation().distance(loc) < 2)
					{
						// ((ArmorStand)ent).setHealth(0);
						ent.remove();
					}
				}

				if(i == 4)
					this.cancel();

				i++;
			}
		}.runTaskTimer(TF.getInstance(), 5l, 5l);
	}

	private static List<Location> calculateTrajectory(Vector vec, Location start)
	{
		Location point = start.clone();

		List<Location> list = new ArrayList<>();

		double m = 1.4;

		Vector v = vec.clone().setY(vec.getY() - 0.014).multiply(m);
		
		if(!start.clone().add(v.clone().multiply(0.5)).getBlock().getType().equals(Material.AIR))
			list.add(start.clone().add(v));
		
		else
		{
			for(int i = 0; point.getBlock().getType().equals(Material.AIR) || point.getBlock().isLiquid(); i++)
			{
				if(i > 500)
					break;
	
				point.add(v);
	
				v.setY(v.getY() - (0.014 * m));
				list.add(point.clone());
			}
		}

		if(list.isEmpty())
			list.add(start.clone().add(v));

		return list;
	}
}
