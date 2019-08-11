package fr.lumin0u.vertix.commands.tfcommands;

import org.bukkit.entity.Player;

import fr.lumin0u.vertix.managers.GameManager;

public class SetMinToStartCommand extends TfArgCommand
{
	public SetMinToStartCommand()
	{
		super("setMinToStart", "d�finit le nombre de personnes pr�sentes minimal pour que la partie se lance", "<nb>", false, 1, "smts");
	}

	@Override
	public void execute(Player p, GameManager gm, String[] args)
	{
		try
		{
			gm.setMinToStart(Integer.parseInt(args[0]));
			p.sendMessage("�aR�gle ajout�e");
		}catch(NumberFormatException e)
		{
			p.sendMessage("�c\"" + args[0] + "\" n'est pas consid�r� comme un nombre");
		}
	}
}
