package fr.lumin0u.vertix.weapons.notDangerous;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.weapons.blocks.CanonMontable;

public class Manette extends AbstractNotDangerousWeapon
{
	public Manette()
	{
		super("Télécommande Canon", Material.WHEAT, 1, -2, 3, false, "", 1, "§6Portée : §a10 blocs", "§6Clic droit pour diriger", "§6Clic gauche pour tirer");
	}

	@Override
	public void effect(Player p)
	{
		new CanonMontable().setDirection(p);
	}
}
