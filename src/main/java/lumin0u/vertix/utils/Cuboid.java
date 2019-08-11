package fr.lumin0u.vertix.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Cuboid implements Cloneable
{
	private Location loc1;
	private Location loc2;
	private List<Block> inside;
	private double xMin;
	private double xMax;
	
	private double yMin;
	private double yMax;
	
	private double zMin;
	private double zMax;
	
	public Cuboid(Location loc1, Location loc2)
	{
		this.loc1 = loc1;
		this.loc2 = loc2;
		
		inside = new ArrayList<>();
		
		update();
	}

	public Location getLoc1()
	{
		return loc1;
	}

	public Cuboid setLoc1(Location loc1)
	{
		this.loc1 = loc1;
		
		update();
		
		return this;
	}

	public Location getLoc2()
	{
		return loc2;
	}

	public Cuboid setLoc2(Location loc2)
	{
		this.loc2 = loc2;
		
		update();
		
		return this;
	}
	
	public boolean hasInside(Location loc)
	{
		return (loc.getX() > xMin && loc.getX() < xMax && loc.getY() > yMin && loc.getY() < yMax && loc.getZ() > zMin && loc.getZ() < zMax);
	}
	
	public boolean hasInside(Player p)
	{
		Location loc = p.getLocation().clone().add(0, 0.6, 0);
		
		return (loc.getX() > xMin && loc.getX() < xMax && loc.getY() > yMin && loc.getY() < yMax && loc.getZ() > zMin && loc.getZ() < zMax);
	}
	
	public List<Block> blocksInside()
	{
		return inside;
	}
	
	private void update()
	{
		inside = new ArrayList<>();
		
		xMin = Math.min(loc1.getX(), loc2.getX());
		xMax = Math.max(loc1.getX(), loc2.getX());
		
		yMin = Math.min(loc1.getY(), loc2.getY());
		yMax = Math.max(loc1.getY(), loc2.getY());
		
		zMin = Math.min(loc1.getZ(), loc2.getZ());
		zMax = Math.max(loc1.getZ(), loc2.getZ());
		

		if(xMax - xMin < 50 && yMax - yMin < 50 && zMax - zMin < 50)
		{
			for(int x = (int)xMin; x < xMax; x++)
			{
				for(int y = (int)yMin; y < yMax; y++)
				{
					for(int z = (int)zMin; z < zMax; z++)
					{
						inside.add(getWorld().getBlockAt(x, y, z));
					}
				}
			}
		}
	}
	
	public Location midpoint()
	{
		return loc1.clone().add(Utils.vectorFrom(loc1, loc2).multiply(0.5));
	}
	
	public Cuboid multiply(double m)
	{
		loc1 = midpoint().add(Utils.vectorFrom(midpoint(), loc1).multiply(m));
		loc2 = midpoint().add(Utils.vectorFrom(midpoint(), loc2).multiply(m));
		
		update();
		
		return this;
	}
	
	public World getWorld()
	{
		return loc1.getWorld();
	}

	@Override
	public String toString()
	{
		return "Cuboid [loc1=" + loc1 + ", loc2=" + loc2 + "]";
	}
	
	@Override
	public Cuboid clone()
	{
		Cuboid o = null;
		
		try
		{
			o = (Cuboid) super.clone();
		}catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		
		return o;
	}
}
