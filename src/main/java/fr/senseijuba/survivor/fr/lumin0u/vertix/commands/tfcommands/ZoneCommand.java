package fr.lumin0u.vertix.commands.tfcommands;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;

public class ZoneCommand extends TfArgCommand
{
	public ZoneCommand()
	{
		super("zone", "définir la zone à capturer en posant des blocks dans les coins", "", false, 0);
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		PlayerManager.getInstance().setZone1Modifier(p);
		
		p.sendMessage("§aPlacez un premier block");
	}
}
