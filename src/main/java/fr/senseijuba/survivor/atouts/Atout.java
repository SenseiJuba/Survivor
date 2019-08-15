package fr.senseijuba.survivor.atouts;

import fr.senseijuba.survivor.Survivor;
import fr.senseijuba.survivor.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Atout {

    MASTODONTE("Mastodonte", 2500, 0, Material.DIAMOND_CHESTPLATE, Arrays.asList(
            "   §7● Armure en fer",
            "   §7● Une barre de vie en plus")),
    DOUBLE_COUP("Double coup", 2000, 1, Material.BOW, Arrays.asList(
            "§2Avantages:",
            "   §7● Vous tirez deux balles en une fois",
            "   §7● Cela ne consomme qu'une munition")),
    MARATHON("Marathon", 2000, 2, Material.FEATHER, Arrays.asList(
            "   §7● Votre vitesse augmente de 100%")),
    SPEED_COLA("Speed Cola", 3000, 3, Material.BRICK, Arrays.asList(
            "   §7● Vous rechargez vos armes deux fois plus vite")),
    THREE_WEAPON("3 armes", 4000, 4, Material.COAL, Arrays.asList(
            "   §7● Vous pouvez tenir trois armes différentes")),
    QUICK_REVIVE("Quick revive", 1500, 5, Material.NETHER_STAR, Arrays.asList(
            "   §7● Vous réanimez en une seconde")),
    GRAVE("Pierre tombale", 2500, 6, Material.BEACON, Arrays.asList(
            "   §7● Vous pouvez recupérer vos armes/atouts " +
                    "à la fin de la vague si vous êtes mort",
            "   §c● Cela consomme l'atout"));

    @Getter @Setter String name;
    @Getter @Setter int money;
    @Getter @Setter int placeInInventory;
    @Getter @Setter Material repMaterial;
    @Getter @Setter List<String> lore;
    private static ItemStack nameTag;

    Atout(String name, int money, int placeInInventory, Material repMaterial, List<String> lore){
        this.name = name;
        this.money = money;
        this.placeInInventory = placeInInventory;
    }

    public static Inventory getInventory(Player p)
    {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, "Choisissez un atout");

        for(ItemStack item : inv.getContents()){
            item = new ItemBuilder(Material.STAINED_GLASS_PANE)
                    .name("")
                    .data(8)
                    .build();
        }

        for(int i = 0; i < Atout.values().length; i++)
        {
            boolean enchant = false;
            Atout k = Atout.values()[i];
            if(Survivor.getInstance().getPlayerAtout().get(p.getUniqueId()).contains(k)){
                enchant = true;
            }
            inv.setItem(8+k.getPlaceInInventory(), k.getRepItem(p, enchant));
        }

        ItemStack tips;

        if(Survivor.getInstance().getPlayerManager().hasTipsActived(p))
        {
            tips = new ItemStack(Material.COOKED_CHICKEN);
            ItemMeta tipsMeta = tips.getItemMeta();
            tipsMeta.setDisplayName("§6Tips : §aActivés");
            tips.setItemMeta(tipsMeta);
        }

        else
        {
            tips = new ItemStack(Material.RAW_CHICKEN);
            ItemMeta tipsMeta = tips.getItemMeta();
            tipsMeta.setDisplayName("§6Tips : §cDésactivés");
            tips.setItemMeta(tipsMeta);
        }

        inv.setItem(27, tips);

        return inv;
    }

    public ItemStack getRepItem(Player p, boolean enchant)
    {
        int pmoney = Survivor.getInstance().getPlayerManager().getMoney().get(p.getUniqueId());

        ItemStack itemRep = new ItemStack(repMaterial, 1);
        ItemMeta itemRepMeta = itemRep.getItemMeta();
        itemRepMeta.setDisplayName("§6"+getName());
        List<String> itemRepLore = new ArrayList<>();

        itemRepLore.add("§2Avantages:");

        for(String str : lore)
            itemRepLore.add(str);

        itemRepLore.add("");
        itemRepLore.add("§6Coût: §e" + money + "$");
        itemRepLore.add("");

        if(pmoney>=money)
            itemRepLore.add("§2Clicker droit pour l'acheter");
        else
            itemRepLore.add("§4Vous n'avez pas assez d'argent");

        if(enchant){
            itemRepMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            itemRepMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemRepMeta.setLore(itemRepLore);
        itemRep.setItemMeta(itemRepMeta);

        return itemRep;
    }

    public ItemStack getRepItemChange(Player p, boolean enchant)
    {
        int pmoney = Survivor.getInstance().getPlayerManager().getMoney().get(p.getUniqueId());

        ItemStack itemRep = new ItemStack(repMaterial, 1);
        ItemMeta itemRepMeta = itemRep.getItemMeta();
        itemRepMeta.setDisplayName("§6"+getName());
        List<String> itemRepLore = new ArrayList<>();

        itemRepLore.add("§2Avantages:");

        for(String str : lore)
            itemRepLore.add(str);

        itemRepLore.add("§2Clicker droit pour l'acheter");

        if(enchant){
            itemRepMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            itemRepMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        itemRepMeta.setLore(itemRepLore);
        itemRep.setItemMeta(itemRepMeta);

        return itemRep;
    }

    public static Atout byRepItem(Material material)
    {
        for(Atout k : values())
        {
            if(k.getRepMaterial().equals(material))
                return k;
        }

        return null;
    }

    public static ItemStack nameTag()
    {
        nameTag = new ItemStack(Material.NAME_TAG);
        ItemMeta nameTagMeta = nameTag.getItemMeta();
        nameTagMeta.setDisplayName("§6Menu des atouts");
        List<String> nameTagLore = new ArrayList<>();
        nameTagLore.add("§7Clicker droit pour ouvrir");
        nameTagMeta.setLore(nameTagLore);
        nameTag.setItemMeta(nameTagMeta);

        return nameTag;
    }

}
