package fr.lumin0u.vertix.weapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.senseijuba.survivor.utils.Title;
import fr.lumin0u.vertix.weapons.WeaponManager;
import fr.lumin0u.vertix.weapons.blocks.C4;

public class Detonateur extends AbstractNotDangerousWeapon
{
	public Detonateur()
	{
		super("D�tonateur", Material.FLINT, 1, 0.9, 10, false, "guns.detonateur", 5, "�6Faites exploser votre C4");
	}

	@Override
	public void effect(Player p)
	{
		if(!GameManager.getInstance(p.getWorld()).getTeamOf(p).areBlocksUsable())
		{
			Title.sendActionBar(p, "�eLes syst�mes sont inutilisables !");
			return;
		}
		
		C4 c4 = ((C4)C4.getInstance());
		
		p.getInventory().setItem(p.getInventory().first(getItem(1).getType()), c4.getItem(1));
		WeaponManager.getInstance().reload(p, c4);
		
		c4.explode(PlayerManager.getInstance().c4LocationOf(p), p);
		PlayerManager.getInstance().setC4Location(p, null);
	}
}
