package fr.lumin0u.vertix.weapons.guns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import fr.lumin0u.vertix.SuperPower;
import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.Utils;
import fr.lumin0u.vertix.weapons.AbstractWeapon;

public abstract class AbstractGun extends AbstractWeapon
{
	protected double damage;
	protected int range;
	protected double precision;
	protected double knockback;

	public AbstractGun(String name, Material mat, int munitions, double timeCharging, int ratioTir, double damage, int range, double precision, boolean enchanted, String sound, float amplifier, double knockback, String... lore)
	{
		super(name, mat, munitions, timeCharging, ratioTir, enchanted, sound, amplifier, lore);

		this.damage = damage;
		this.range = range;
		this.precision = precision;
		this.knockback = knockback;
	}

	@SuppressWarnings("deprecation")
	public void shoot(Player p)
	{
		Location start = p.getEyeLocation();
		Vector increase = start.getDirection();

		if(this instanceof LaTornade)
		{
			p.setVelocity(p.getVelocity().subtract(p.getEyeLocation().getDirection().multiply(0.05)));

			double j = PlayerManager.getInstance().getHeavyBulletNb(p);
			double multi = 0.15;

			Vector x1 = new Vector(-p.getLocation().getDirection().normalize().getZ(), 0d, p.getLocation().getDirection().normalize().getX()).normalize();
			Vector x2 = p.getLocation().getDirection().normalize().crossProduct(x1).normalize();
			start.add(x1.clone().multiply(multi * Math.sin(j / 10 * Math.PI * 2d))).add(x2.clone().multiply(multi * Math.cos(j / 10 * Math.PI * 2d)));

			PlayerManager.getInstance().addHeavyBulletNb(p);
		}

		Utils.playSound(p.getLocation(), sound, getMaxDistance());

		increase.multiply(0.5);

		try
		{
			if(PlayerManager.getInstance().getSP(p) != null && PlayerManager.getInstance().getSP(p).equals(SuperPower.AIMBOT) && !(this instanceof LaTornade))
				precision = getClass().newInstance().precision * 0.4;

			else if(this instanceof LaTornade && PlayerManager.getInstance().isLookingHeavy(p))
				precision = LaTornade.scopePres() * 0.4;

			else if(this instanceof LaTornade && !PlayerManager.getInstance().isLookingHeavy(p))
				precision = LaTornade.noScopePres() * 0.4;

		}catch(Exception e)
		{
			e.printStackTrace();
		}

		increase.setX(increase.getX() + (new Random().nextBoolean() ? new Random().nextDouble() % precision / 10 : 0 - new Random().nextDouble() % precision / 10));

		increase.setY(increase.getY() + (new Random().nextBoolean() ? new Random().nextDouble() % precision / 10 : 0 - new Random().nextDouble() % precision / 10));

		increase.setZ(increase.getZ() + (new Random().nextBoolean() ? new Random().nextDouble() % precision / 10 : 0 - new Random().nextDouble() % precision / 10));

		increase.multiply(0.5);

		List<Player> alreadyHit = new ArrayList<>();

		HashMap<Player, Location> nearestPoint = new HashMap<>();

		double hitboxMultiplier = (this instanceof LaTornade ? 1.1 : 1);

		counter: for(int counter = 0; counter < range * 4; counter++)
		{
			if(counter == 1)
				p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);

			Location point = start.add(increase);

			if(PlayerManager.getInstance().getSP(p) != null && PlayerManager.getInstance().getSP(p).equals(SuperPower.DMG) && counter % 4 == 1)
				p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, 1f, 0f, 0f, 1, 0, 100);// RED

			else
				p.getWorld().spigot().playEffect(point, Effect.COLOURED_DUST, 0, 0, (float)1 / 255, (float)0 / 255, (float)0 / 255, 1, 0, 100);

			for(Player ent : p.getWorld().getPlayers())
			{
				if(GameManager.getInstance(p.getWorld()).sameTeam(p, ent) || alreadyHit.contains(ent) || ent.isDead())
					continue;

				if(!nearestPoint.containsKey(ent) || nearestPoint.get(ent).distance(ent.getEyeLocation()) > point.distance(ent.getEyeLocation()))
					nearestPoint.put(ent, point.clone());

				if(PlayerManager.bodyCub(ent).multiply(hitboxMultiplier).hasInside(point) || PlayerManager.headCub(ent).multiply(hitboxMultiplier).hasInside(point))
				{
					boolean damageDone = false;

					if(PlayerManager.headCub(ent).multiply(1.2).multiply(hitboxMultiplier).hasInside(point) && this instanceof Revolver)// headshot revolver
					{
						// TF.debug("Revolver headshot");
						damageDone = GameManager.damageTF(ent, p, damage + 2, p.getEyeLocation().getDirection().multiply(knockback / 3).add(increase.clone().normalize()).multiply(0.5), point);
					}

					else if(PlayerManager.getInstance().isLooking(p) && this instanceof Sniper)// looking sniper
					{
						// TF.debug("Sniper scope");
						damageDone = GameManager.damageTF(ent, p, damage * 2, p.getEyeLocation().getDirection().multiply(knockback / 3).add(increase.clone().normalize()).multiply(0.5), point);
					}

					else// normal
					{
						// TF.debug("Normal hit");
						damageDone = GameManager.damageTF(ent, p, damage, p.getEyeLocation().getDirection().multiply(knockback / 3).add(increase.clone().normalize()).multiply(0.5), point);
					}

					if(damageDone)
					{
						ent.setVelocity(ent.getVelocity().multiply(0.25));

						alreadyHit.add(ent);

						for(int i = 0; i < 10; i++)
							p.getWorld().playEffect(point, Effect.TILE_BREAK, 152);

						if(!p.equals(ent))
							p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 0.5f, 1.0f);

						if(!(this instanceof Sniper))
							break counter;
					}
				}
			}

			List<Material> transparent = new ArrayList<>();
			transparent.add(Material.LEAVES);
			transparent.add(Material.LEAVES_2);
			transparent.add(Material.STEP);
			transparent.add(Material.WOOD_STEP);
			transparent.add(Material.STONE_PLATE);
			transparent.add(Material.GOLD_PLATE);
			transparent.add(Material.IRON_PLATE);

			for(Material mat : Material.values())
				if(mat.isTransparent())
					transparent.add(mat);

			if(!point.getBlock().getType().equals(Material.AIR) && !point.getBlock().isLiquid() && !transparent.contains(point.getBlock().getType()))
			{
				Material mat = point.getBlock().getType();
				byte data = point.getBlock().getData();

				ArmorStand nearest = null;
				
				if(mat.equals(Material.BARRIER))
				{
					for(ArmorStand as : point.getWorld().getEntitiesByClass(ArmorStand.class))
					{
						if((nearest == null || as.getEyeLocation().distance(point) < nearest.getEyeLocation().distance(point)) && as.getHelmet() != null)
						{
							nearest = as;
						}
					}
				}
				
				if(nearest != null && nearest.getEyeLocation().distance(point) < 3)
				{
					data = (byte)nearest.getHelmet().getDurability();
					mat = nearest.getHelmet().getType();
				}

				if(!point.getBlock().isLiquid())
					for(int i = 0; i < 20; i++)
						p.getWorld().playEffect(point.clone().subtract(increase), Effect.TILE_BREAK, new MaterialData(mat, data));

				break;
			}
		}

		for(Player ent : nearestPoint.keySet())
			if(!alreadyHit.contains(ent))
				Utils.playSound(ent, nearestPoint.get(ent), "guns.fieew", 5);
	}

	@Override
	public List<String> getLore()
	{
		List<String> loree = new ArrayList<>();

		loree.add("§6Degats par balle : §a" + damage);
		loree.add("§6Range : §a" + range);

		if(timeCharging > 0)
			loree.add("§6Recharge : §a" + timeCharging + "s");

		loree.addAll(super.getLore());

		return loree;
	}
}
