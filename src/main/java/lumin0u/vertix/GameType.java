package fr.lumin0u.vertix;

import java.util.ArrayList;
import java.util.List;

public enum GameType
{
	PAYLOADS("Payloads", true, true, 2, false, false, false, false, false),
	FFA("FFA", false, false, 0, false, false, false, false, true),
	TEAM_DEATHMATCH2("Team Deathmatch (2)", true, false, 2, false, true, false, false, false),
	TEAM_DEATHMATCH3("Team Deathmatch (3)", true, false, 3, false, true, false, false, false),
	TEAM_DEATHMATCH4("Team Deathmatch (4)", true, false, 4, false, true, false, false, false),
	KOTH2("Zone (2)", true, false, 2, false, false, true, false, false),
	KOTH4("Zone (4)", true, false, 4, false, false, true, false, false),
	CTF2("CTF (2)", true, false, 2, false, false, false, true, false),
	CTF4("CTF (4)", true, false, 4, false, false, false, true, false),
	
	PAYLOADS_SP("Payloads SuperPower", true, true, 2, true, false, false, false, true),
	TEAM_DEATHMATCH2_SP("Team Deathmatch SuperPower (2)", true, false, 2, true, true, false, false, true),
	TEAM_DEATHMATCH3_SP("Team Deathmatch SuperPower (3)", true, false, 3, true, true, false, false, true),
	TEAM_DEATHMATCH4_SP("Team Deathmatch SuperPower (4)", true, false, 4, true, true, false, false, true);
	
	private String name;
	private int nbTeams;
	private boolean hide, superPower, tdm, koth, carts, teams, ctf;
	
	private GameType(String name, boolean teams, boolean carts, int nbTeams, boolean superPower, boolean tdm, boolean koth, boolean ctf, boolean hide)
	{
		this.name = name;
		this.teams = teams;
		this.carts= carts;
		this.nbTeams = nbTeams;
		this.hide = hide;
		this.superPower = superPower;
		this.tdm = tdm;
		this.koth = koth;
		this.ctf = ctf;
	}

	public String getName()
	{
		return name;
	}

	public String get_Name()
	{
		return name.replaceAll(" ", "_");
	}
	
	public static GameType byName(String name)
	{
		for(GameType gt : values())
			if(gt.getName().equalsIgnoreCase(name.replaceAll("_", " ")))
				return gt;
		
		return null;
	}

	public boolean areTeamsActive()
	{
		return teams;
	}

	public boolean isCarts()
	{
		return carts;
	}
	
	public int nbTeams()
	{
		return nbTeams;
	}
	
	public boolean isHidden()
	{
		return hide;
	}
	
	public static List<GameType> notHiddenValues()
	{
		List<GameType> types = new ArrayList<>();
		
		for(GameType gt : values())
			if(!gt.isHidden())
				types.add(gt);
		
		return types;
	}
	
	public boolean isSuperPowerMode()
	{
		return superPower;
	}
	
	public boolean isTDM()
	{
		return tdm;
	}
	
	public boolean isKoth()
	{
		return koth;
	}
	
	public boolean isCTF()
	{
		return ctf;
	}
}
