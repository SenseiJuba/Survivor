package fr.senseijuba.survivor.weapons.ultimateWeapons.guns;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;

public abstract class AbstractUltimateGun extends AbstractUltimateWeapon
{
	protected double damage;
	protected int range;
	protected double precision;
	protected double knockback;
	
	public AbstractUltimateGun(String name, Material mat, int munitions, int ratioTir, double damage, int range, double precision, boolean enchanted, String sound, float amplifier, double knockback, String... lore)
	{
		super(name, mat, munitions, ratioTir, enchanted, sound, amplifier, lore);
		
		this.damage = damage;
		this.range = range;
		this.precision = precision;
		this.knockback = knockback;
	}
	
//	@SuppressWarnings("deprecation")
	public void shoot(Player p)//never used mais dans l'id�e on le met quand meme OU PAS
	{
//		Location start = p.getEyeLocation();
//		Vector increase = start.getDirection();
//
//		Utils.playSound(p.getLocation(), sound, 4);
//		
//		increase.multiply(0.5);
//
//		boolean stop = false;
//		
//		increase.setX(increase.getX()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
//		
//		increase.setY(increase.getY()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
//		
//		increase.setZ(increase.getZ()+(new Random().nextBoolean() ? new Random().nextDouble() % precision/10 : 0-new Random().nextDouble() % precision/10));
//
//		for(int counter = 0; counter < range*2; counter++)
//		{
//			
//			if(counter == 1)
//				p.getWorld().spigot().playEffect(start, Effect.FLAME, Effect.FLAME.getId(), 1, 200, 0, 0, 0, 0, 100);
//			
//			Location point = start.add(increase);
//			
//			for(int i = 0; i < 2; i++)
//				p.getWorld().spigot().playEffect(point, Effect.PARTICLE_SMOKE, Effect.PARTICLE_SMOKE.getId(), 1, 0, 0, 0, 0, 0, 100);
//			
//			stop = false;
//			
//			for(Entity ent : p.getWorld().getEntities())
//			{
//				if(ent.getLocation().distance(point) < 2 && ent instanceof Damageable)
//				{
//					if(!(point.getY()-1.6 > ent.getLocation().getY()) && ent.getLocation().distance(point) > 1.6)
//						break;
//					
//					Location newPoint = new Location(p.getWorld(), point.getX(), ent.getLocation().getY(), point.getZ());
//					
//					if(newPoint.distance(ent.getLocation()) > 0.5)
//						break;
//					
//					for(int i = 0; i < 20; i++)
//						p.getWorld().playEffect(point, Effect.TILE_BREAK, 152);
//					
//					if(point.getY()-1.6 > ent.getLocation().getY() && !name.equals("Sniper"))//headshot non sniper
//					{
//						((Damageable)ent).damage(0);
//
//						ent.getVelocity().multiply(0.25);
//						ent.setVelocity(ent.getVelocity().add(p.getEyeLocation().getDirection()).add(p.getEyeLocation().getDirection().multiply(knockback/3)));
//
//						((Damageable)ent).setLastDamageCause(new EntityDamageEvent(p, DamageCause.PROJECTILE, ((Damageable)ent).getHealth()-damage*2));
//						
//						if(ent instanceof Player && ((Damageable)ent).getHealth() - damage*2 <= 0)
//							PlayerManager.getInstance().setKiller(((Player)ent), p);
//						
//						((Damageable)ent).setHealth(!(((Damageable)ent).getHealth() - damage*2 <= 0) ? ((Damageable)ent).getHealth()-damage*2 : 0);
//					}
//					
//					else//normal
//					{
//						((Damageable)ent).damage(0);
//
//						ent.getVelocity().multiply(0.25);
//						ent.setVelocity(ent.getVelocity().add(p.getEyeLocation().getDirection()).add(p.getEyeLocation().getDirection().multiply(knockback/3)));
//
//						((Damageable)ent).setLastDamageCause(new EntityDamageEvent(p, DamageCause.PROJECTILE, ((Damageable)ent).getHealth()-damage));
//						
//						if(ent instanceof Player && ((Damageable)ent).getHealth() - damage <= 0)
//							PlayerManager.getInstance().setKiller(((Player)ent), p);
//						
//						((Damageable)ent).setHealth(!(((Damageable)ent).getHealth() - damage <= 0) ? ((Damageable)ent).getHealth()-damage : 0);
//						
////						System.out.println("Normal Hit");
//					}
//					
//					start.getWorld().playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
//					
//					stop = true;
//					break;
//				}
//			}
//			
//			List<Material> transparent = new ArrayList<>();
//			transparent.add(Material.LEAVES);
//			transparent.add(Material.LEAVES_2);
//			transparent.add(Material.BARRIER);
//			transparent.add(Material.STEP);
//			transparent.add(Material.WOOD_STEP);
//			
//			for(Material mat : Material.values())
//				if(mat.isTransparent())
//					transparent.add(mat);
//			
//			if(!point.getBlock().getType().equals(Material.AIR))
//			{
//				Material mat = point.getBlock().getType();
//				byte data = point.getBlock().getData();
//				
//				if(!point.getBlock().isLiquid())
//					for(int i = 0; i < 20; i++)
//						p.getWorld().playEffect(point.clone().subtract(increase), Effect.TILE_BREAK, new MaterialData(mat, data));
//				
//				if(!point.getBlock().isLiquid() && !transparent.contains(point.getBlock().getType()))
//					stop = true;
//			}
//			
//			if(stop)
//				break;
//		}
	}
}
