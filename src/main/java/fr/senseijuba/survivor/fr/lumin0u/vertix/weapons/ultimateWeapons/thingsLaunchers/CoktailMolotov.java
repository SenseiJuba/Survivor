package fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.utils.FireCause;
import fr.lumin0u.vertix.utils.Utils;

public class CoktailMolotov extends AbstractUltimateThingLauncher
{
	public CoktailMolotov()
	{
		super("Coktail Molotov", Material.COAL, 1, 5, false, "", 1, EntityType.SNOWBALL, 5, 0, 0, "§6Enflamme les ennemis dans un", "§6rayon de 5 blocs autour de", "§6l'explosion pendant 10s");
	}
	
	@Override
	public void explode(Player p, Location l)
	{
		Utils.playSound(l, Sound.GLASS, 30);
		
		for(Player ent : l.getWorld().getPlayers())
		{
			if(GameManager.getInstance(p.getWorld()).sameTeam(p, ent))
				continue;
			
			if(ent.getLocation().distance(l) <= rayonExplosion)
			{
				ent.setFireTicks(200);
				PlayerManager.getInstance().addDamagerTo(ent, p);
				PlayerManager.getInstance().setFireCauseOf(ent, new FireCause(false, p.getUniqueId(), this));
			}
		}
		
		Utils.explosionParticles(l, (float)(rayonExplosion/2), 60*(int)rayonExplosion, Effect.FLAME);
		
		for(int i = 0; i < 10; i++)
			l.getWorld().playEffect(l, Effect.LAVA_POP, 0);
	}
}
