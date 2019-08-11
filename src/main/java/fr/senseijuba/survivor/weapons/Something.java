package fr.senseijuba.survivor.weapons;

import org.bukkit.Material;

public abstract class Something
{
	protected Material mat;
	protected int munitions;
	protected int maxMunitions;
	protected int currentMunitions;
	protected double timeCharging;
	protected int ratioTir;
	protected String name;
	protected String[] lore;
	protected boolean enchanted;
	protected String sound;
	protected float maxDistance;
	
	public Something(String name, Material mat, int munitions, int maxMunitions, int ratioTir, boolean enchanted, String sound, float maxDistance, String... lore)
	{
		this.name = name;
		this.mat = mat;
		this.munitions = munitions;
		this.maxMunitions = maxMunitions;
		this.currentMunitions = maxMunitions;
		this.ratioTir = ratioTir;
		this.lore = lore;
		this.enchanted = enchanted;
		this.sound = sound;
		this.maxDistance = maxDistance;
	}
	
	public int getRatioTir()
	{
		return ratioTir;
	}
	
	public String getSound()
	{
		return sound;
	}
	
	public float getMaxDistance()
	{
		return maxDistance;
	}
	
	public String getName()
	{
		return name;
	}
}
