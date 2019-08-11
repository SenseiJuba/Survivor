package fr.lumin0u.vertix.utils;

import java.util.UUID;

import fr.lumin0u.vertix.weapons.Something;

public class FireCause
{
	private boolean unknown;
	private UUID damager;
	private Something reason;
	
	public FireCause(boolean unknown, UUID damager, Something reason)
	{
		this.unknown = unknown;
		this.damager = damager;
		this.reason = reason;
	}

	public boolean isUnknown()
	{
		return unknown;
	}

	public UUID getDamager()
	{
		return damager;
	}

	public Something getReason()
	{
		return reason;
	}
}
