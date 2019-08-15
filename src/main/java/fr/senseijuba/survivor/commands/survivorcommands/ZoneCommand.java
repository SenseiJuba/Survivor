package fr.senseijuba.survivor.commands.survivorcommands;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.map.Zone;
import fr.senseijuba.survivor.managers.GameManager;
import fr.senseijuba.survivor.utils.Cuboid;
import fr.senseijuba.survivor.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ZoneCommand extends SurvivorArgCommand {

    public ZoneCommand()
    {
        super("zone", "définir la zone en posant des blocks dans les coins", "<barrier|door> <nom de la zone>", false, 0);
    }

    @Override
    public void execute(Player p, GameManager gm, String[] args) {

        String zoneHelp = "§0------------------------------------------------"//
                + "\n§2-Utilisation de la commande /survivor zone : "//
                + "\n§b-/survivor zone create <nom de la zone> §3: créer une nouvelle zone"//
                + "\n§b-/survivor zone mobspawn <nom de la zone> §3: créer un nouveau point de spawn pour les mob dans une zone"//
                + "\n§b-/survivor zone rmmobspawn <nom de la zone> §3: donne un bloc barrier au joueur pour supprimer un endroit de spawn en le clickant droit sur l'endroit"//
                + "\n§b-/survivor zone sign <nom de la zone> <coût de la zone> §3: donne un panneau au joueur à placer sur le monde lui permettand d'acheter une zone"//
                + "\n§b-/survivor zone rmsign <nom de la zone> §3: supprime le panneau ainsi que le coût de la zone"//
                + "\n§b-/survivor zone barricades <nom de la zone> §3: donne un bloc barrier au joueur pour créer une barricade en le posant dans les coins"//
                + "\n§b-/survivor zone rmbarricades <nom de la zone> §3: donne un bloc barrier au joueur pour supprimer une barricade en le clickant droit sur une barricade"//
                + "\n§b-/survivor zone door <nom de la zone> §3: donne un bloc d'emeraude au joueur pour créer une porte en le posant dans les coins"//
                + "\n§b-/survivor zone rmdoor <nom de la zone> §3: retire la zone attribué à la porte"//
                + "\n§c Dropper l'item reçu permet d'arrêter la commande";

        if(args.length < 1){
            p.sendMessage(zoneHelp);

            return;
        }


        else if(!args[1].isEmpty())
        {

            if(args[0].equalsIgnoreCase("create")){
                if(Survivor.getInstance().worldtoMap(p.getWorld()).getZoneByName(args[1]) == null) {
                    Survivor.getInstance().worldtoMap(p.getWorld()).createZone(args[1]);
                    p.sendMessage("§aLa zone " + args[1] + "a été créée");
                }
                else{
                    p.sendMessage("§cCette zone existe déjà");
                }
                return;
            }
            else if(Survivor.getInstance().worldtoMap(p.getWorld()).getZoneByName(args[1]) != null){

                Zone zone = Survivor.getInstance().worldtoMap(p.getWorld()).getZoneByName(args[1]);

                if(args[0].equalsIgnoreCase("mobspawn")){
                    zone.getSpawnMobZones().add(p.getLocation());
                    p.sendMessage("§a" + Utils.locToString(p.getLocation()) + " est maintenant un point de spawn de mob");
                }
                else if(args[0].equalsIgnoreCase("rmmobspawn")){

                    if(!zone.isSomeoneDestructMobZones()) {
                        ItemStack barrier = new ItemStack(Material.BARRIER);
                        ItemMeta meta = barrier.getItemMeta();
                        meta.setDisplayName("§6Destruction d'un point de spawn de mob");

                        List<String> lore = new ArrayList<>();
                        lore.add("§2Pour détruire une zone :");
                        lore.add("§b-clicker droit avec le bloc sur un bloc barrier");
                        lore.add("§cAttention, ceci est irréversible");
                        meta.setLore(lore);
                        meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        barrier.setItemMeta(meta);

                        p.getInventory().addItem(barrier);
                        zone.setDestructMobZones(p);

                        p.sendMessage("§aClicker droit avec le bloc sur un bloc barrier pour supprimer un point de spawn");

                        for(Location mobspawn : zone.getSpawnMobZones()){
                            mobspawn.getWorld().getBlockAt(mobspawn).setType(Material.BARRIER);
                        }

                    }
                    else if(zone.isDestructMobZones(p)){
                        p.sendMessage("Vous êtes déjà en train de detruire des zones de mobs");
                    }
                    else{
                        p.sendMessage("Quelqu'un est déjà en train de detruire des zones de mobs");
                    }
                }
                else if(args[0].equalsIgnoreCase("sign")){
                    if(!args[2].isEmpty()){
                        if(zone.getBuyingSign() != null) {
                            if(!zone.isSomeoneModifyingSign()) {
                                try {
                                    ItemStack sign = new ItemStack(Material.SIGN);
                                    ItemMeta meta = sign.getItemMeta();
                                    meta.setDisplayName("§6Création d'un panneau d'achat");

                                    List<String> lore = new ArrayList<>();
                                    lore.add("§2Pour créer un panneau de zone :");
                                    lore.add("§b-placez le à l'endroit voulu");
                                    meta.setLore(lore);
                                    meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                                    sign.setItemMeta(meta);

                                    p.getInventory().addItem(sign);

                                    zone.setCost(Integer.parseInt(args[2]));
                                    zone.setSignModifier(p);

                                    p.sendMessage("§aPlacez le panneau à l'endroit voulu pour créer un panneau d'achat");
                                } catch (Exception e) {
                                    p.sendMessage("§6" + args[2] + "§c n'est pas un chiffre/nombre");
                                }
                            }
                            else if(zone.isModifyingSign(p)){
                                p.sendMessage("Vous êtes déjà en train de modifier le panneau");
                            }
                            else{
                                p.sendMessage("Quelqu'un est déjà en train de modifier le panneau");
                            }
                        }
                        else {
                            p.sendMessage("§cLa zone " + zone.getName() + " possède déjà un panneau, faire /survivor zone rmsign <nom de la zone> pour le retirer");
                        }
                    }
                    else{
                        p.sendMessage("§cVous devez indiqué un prix");
                    }
                }
                else if(args[0].equalsIgnoreCase("rmsign")){
                    if(zone.getBuyingSign() != null) {
                        zone.setCost(0);
                        zone.getBuyingSign().getWorld().getBlockAt(zone.getBuyingSign()).setType(Material.AIR);
                        zone.setBuyingSign(null);

                        p.sendMessage("§aLe panneau a été supprimé");
                    }
                    else {
                        p.sendMessage("§cIl n'existe pas de panneau");
                    }
                }
                else if(args[0].equalsIgnoreCase("barricades"))
                {
                    if(!zone.isSomeoneModifyingBarricades()) {
                        ItemStack barrier = new ItemStack(Material.BARRIER);
                        ItemMeta meta = barrier.getItemMeta();
                        meta.setDisplayName("§6Création d'une barricade");

                        List<String> lore = new ArrayList<>();
                        lore.add("§2Pour créer une zone :");
                        lore.add("§b-placer un bloc dans un des coins de la barricades");
                        lore.add("§b-placer un bloc dans le coin opposé");
                        lore.add("§cAttention, une barricade doit faire 3x2 blocs");
                        meta.setLore(lore);
                        meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        barrier.setItemMeta(meta);

                        p.getInventory().addItem(barrier);

                        zone.setBarricadesModifier(p);

                        p.sendMessage("§aPlacez un premier block");
                    }
                    else if(zone.isModifyingBarricades(p)){
                        p.sendMessage("Vous êtes déjà en train de modifier une barricade");
                    }
                    else{
                        p.sendMessage("Quelqu'un est déjà en train de modifier une barricade");
                    }
                }
                else if(args[0].equalsIgnoreCase("door"))
                {
                    if(zone.getDoor() == null) {
                        if (!zone.isSomeoneModifyingDoor()) {
                            ItemStack barrier = new ItemStack(Material.EMERALD_BLOCK);
                            ItemMeta meta = barrier.getItemMeta();
                            meta.setDisplayName("§6Création d'une porte");

                            List<String> lore = new ArrayList<>();
                            lore.add("§2Pour créer une zone :");
                            lore.add("§b-placer un bloc dans un des coins de la porte");
                            lore.add("§b-placer un bloc dans le coin opposé");
                            lore.add("§cAttention, il ne peut n'y avoir qu'une seul porte");
                            meta.setLore(lore);
                            meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                            barrier.setItemMeta(meta);

                            p.getInventory().addItem(barrier);

                            zone.setBarricadesModifier(p);

                            p.sendMessage("§aPlacez un premier block");
                        } else if (zone.isModifyingDoor(p)) {
                            p.sendMessage("§cVous êtes déjà en train de modifier la porte");
                        } else {
                            p.sendMessage("§cQuelqu'un est déjà en train de modifier la porte");
                        }
                    }
                    else {
                        p.sendMessage("§cIl existe déjà une porte, écriver /survivor zone rmdoor <nom de la zone> pour la supprimer");
                    }
                }
                else if(args[0].equalsIgnoreCase("rmdoor")){
                    zone.setDoor(null);

                    p.sendMessage("§aLa porte a été supprimée");
                }
                else if(args[0].equalsIgnoreCase("rmbarricades")){

                    if(!zone.isSomeoneDestructBarricades()) {
                        ItemStack barrier = new ItemStack(Material.BARRIER);
                        ItemMeta meta = barrier.getItemMeta();
                        meta.setDisplayName("§6Destruction d'une barricade");

                        List<String> lore = new ArrayList<>();
                        lore.add("§2Pour détruire une zone :");
                        lore.add("§b-clicker droit avec le bloc sur une zone défini par des blocks de barrier");
                        lore.add("§cAttention, ceci est irréversible");
                        meta.setLore(lore);
                        meta.addEnchant(Enchantment.SILK_TOUCH, 2, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                        barrier.setItemMeta(meta);

                        p.getInventory().addItem(barrier);
                        zone.setDestructBarricades(p);

                        p.sendMessage("§aClicker droit avec le bloc sur un bloc barrier pour supprimer une barricade");

                        for(Cuboid barricade : zone.getBarricades()){
                            for(Block block : barricade.blocksInside()) {
                                block.setType(Material.BARRIER);
                            }
                        }
                    }
                    else if(zone.isDestructBarricades(p)){
                        p.sendMessage("Vous êtes déjà en train de détruire une barricade");
                    }
                    else{
                        p.sendMessage("Quelqu'un est déjà en train de détruire une barricade");
                    }
                }
                else
                {
                    p.sendMessage("§cCette commande n'existe pas");
                    p.sendMessage(zoneHelp);
                }
            }
            else {
                p.sendMessage("§cCette zone n'existe pas");
            }
        }
        else {
            p.sendMessage("§cVous devez indiquer le nom de la zone où faire l'action");
        }
    }

    @Override
    public List<String> getPossibleArgs(Player executer, String[] args)
    {
        List<String> possibles = new ArrayList<>();

        if(args.length == 2)
        {
            possibles.add("help");
            possibles.add("create");
            possibles.add("mobspawn");
            possibles.add("rmmobspawn");
            possibles.add("sign");
            possibles.add("rmsign");
            possibles.add("barricades");
            possibles.add("rmbarricades");
            possibles.add("door");
            possibles.add("rmdoor");
        }

        if(args.length == 3 && !args[1].equalsIgnoreCase("create"))
        {
            for(Zone zone : Survivor.getInstance().worldtoMap(executer.getWorld()).getZones()){
                possibles.add(zone.getName());
            }
        }

        return possibles;
    }
}
