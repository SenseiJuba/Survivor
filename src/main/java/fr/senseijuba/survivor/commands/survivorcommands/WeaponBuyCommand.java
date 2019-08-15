package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.managers.GameManager;
import fr.senseijuba.survivor.weapons.AbstractWeapon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class WeaponBuyCommand extends SurvivorArgCommand {


    public WeaponBuyCommand() {
        super("weaponsign", "définir un panneau d'achat d'arme", "<arme> <cost>", false, 0);
    }

    @Override
    public void execute(Player p, GameManager gm, String[] args) {

        String Help = "§0------------------------------------------------"//
                + "\n§2-Utilisation de la commande /survivor weaponsign : "//
                + "\n§b-/survivor weaponsign <arme> <cost> §3: Vous donne un panneau à placer pour l'achat d'un arme"//
                + "\n§c Casser le panneau suffit à supprimer le point d'achat";

        if(args.length < 1){
            p.sendMessage(Help);

            return;
        }


        else if(!args[0].isEmpty()) {
            if (args[0].equalsIgnoreCase("help")) {
                p.sendMessage(Help);
            }
            else{
                for (AbstractWeapon weapon : Survivor.getInstance().getWeaponManager().listWeapons()) {
                    try {
                        if (args[0].equalsIgnoreCase(weapon.getName())) {
                            ItemStack sign = new ItemStack(Material.SIGN);
                            ItemMeta meta = sign.getItemMeta();
                            meta.setDisplayName(weapon.getName());

                            List<String> lore = new ArrayList<>();
                            lore.add(Integer.parseInt(args[2])+"");
                            lore.add("§2Pour créer un panneau d'achat :");
                            lore.add("§b-placez le à l'endroit voulu");
                            meta.setLore(lore);
                            meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                            sign.setItemMeta(meta);

                            p.getInventory().addItem(sign);

                            p.sendMessage("§aPlacez le panneau à l'endroit voulu pour créer un panneau d'achat");
                            return;
                        }
                    }
                    catch(Exception e){
                        p.sendMessage("§6" + args[2] + "§c n'est pas un chiffre/nombre");
                    }
                }
                p.sendMessage("§c" + args[1] + " n'est pas une arme connue");
            }
        }
    }

    @Override
    public List<String> getPossibleArgs(Player executer, String[] args) {
        List<String> possibles = new ArrayList<>();

        if (args.length == 2) {
            possibles.add("help");
            for(AbstractWeapon weapon : Survivor.getInstance().getWeaponManager().listWeapons()){
                possibles.add(weapon.getName());
            }
        }

        return possibles;
    }
}
