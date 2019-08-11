package fr.senseijuba.survivor.commands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetWeapon implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(args.length < 1)
        {
            sender.sendMessage("§cUsage : /getWeapon <arme>");
            return true;
        }

        boolean ok = false;

        for(AbstractWeapon gun : Survivor.getInstance().getWeaponManager().listWeapons())
        {
            if(gun.getName().equalsIgnoreCase(String.join(" ", args)) && sender instanceof Player)
            {
                ((Player)sender).getInventory().addItem(gun.getItem(gun.getMaxMunitions()));
                ((Player)sender).setNoDamageTicks(20);

                ok = true;
            }
        }


        if(!ok)
            if(args[0].equalsIgnoreCase("list"))
                for(AbstractWeapon k : Survivor.getInstance().getWeaponManager().listWeapons())
                    sender.sendMessage("§a"+k.getName());

            else if(args[0].equalsIgnoreCase("all"))
                for(AbstractWeapon w : Survivor.getInstance().getWeaponManager().listWeapons())
                    ((Player)sender).getInventory().addItem(w.getItem(w.getMaxMunitions()));

            else
                sender.sendMessage("§cArme inconnue");

        sender.sendMessage(String.join(" ", args));

        return true;
    }
}
