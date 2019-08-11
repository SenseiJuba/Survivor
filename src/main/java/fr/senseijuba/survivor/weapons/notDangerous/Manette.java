package fr.senseijuba.survivor.weapons.notDangerous;

import fr.senseijuba.survivor.weapons.blocks.CanonMontable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Manette extends AbstractNotDangerousWeapon
{
	public Manette()
	{
		super("T�l�commande Canon", Material.WHEAT, 1, -2, 3, false, "", 1, "�6Port�e : �a10 blocs", "�6Clic droit pour diriger", "�6Clic gauche pour tirer");
	}

	@Override
	public void effect(Player p)
	{
		CanonMontable.setDirection(p);
	}
}
