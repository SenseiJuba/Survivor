package fr.lumin0u.vertix;

import java.util.Random;

public enum SuperPower
{
	DMG("Damage+", false),
	RESISTANCE("Resistance", false),
	PUSHER("Pusher", true),
	AIMBOT("Aimbot", false),
	DEFENDER("Defender", true),
	SPEED("Speed", false),
//	JUMPER("Jump Boost", false),
	CACTUS("Thorns", false);
	
	private boolean cartPower;
	private String name;
	
	private SuperPower(String name, boolean cartPower)
	{
		this.cartPower = cartPower;
		this.name = name;
	}
	
	public boolean isACartPower()
	{
		return cartPower;
	}
	
	public String getName()
	{
		return name;
	}
	
	public static SuperPower getRandomValue()
	{
		return values()[new Random().nextInt(values().length)];
	}
}
