package fr.lumin0u.vertix;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.lumin0u.vertix.managers.GameManager;
import fr.lumin0u.vertix.managers.PlayerManager;
import fr.lumin0u.vertix.weapons.AbstractWeapon;
import fr.lumin0u.vertix.weapons.Barbecue;
import fr.lumin0u.vertix.weapons.blocks.C4;
import fr.lumin0u.vertix.weapons.blocks.CanonMontable;
import fr.lumin0u.vertix.weapons.blocks.Mine;
import fr.lumin0u.vertix.weapons.blocks.Trampoline;
import fr.lumin0u.vertix.weapons.corpsACorps.Batte;
import fr.lumin0u.vertix.weapons.corpsACorps.CleMolette;
import fr.lumin0u.vertix.weapons.corpsACorps.Hache;
import fr.lumin0u.vertix.weapons.corpsACorps.Poignard;
import fr.lumin0u.vertix.weapons.corpsACorps.PoingsAmericains;
import fr.lumin0u.vertix.weapons.corpsACorps.ScieAmputation;
import fr.lumin0u.vertix.weapons.guns.Defenseur;
import fr.lumin0u.vertix.weapons.guns.Defoncator;
import fr.lumin0u.vertix.weapons.guns.FuseeDeDetresse;
import fr.lumin0u.vertix.weapons.guns.LaTornade;
import fr.lumin0u.vertix.weapons.guns.MitrailletteLourde;
import fr.lumin0u.vertix.weapons.guns.PistoletAutomatique;
import fr.lumin0u.vertix.weapons.guns.PistoletTranquilisant;
import fr.lumin0u.vertix.weapons.guns.Revolver;
import fr.lumin0u.vertix.weapons.guns.Sniper;
import fr.lumin0u.vertix.weapons.guns.shotguns.Blaoups;
import fr.lumin0u.vertix.weapons.guns.shotguns.CanonScie;
import fr.lumin0u.vertix.weapons.guns.shotguns.FusilAPompe;
import fr.lumin0u.vertix.weapons.launchableItem.Dynamite;
import fr.lumin0u.vertix.weapons.launchableItem.Fumigene;
import fr.lumin0u.vertix.weapons.launchableItem.GrenadeFlash;
import fr.lumin0u.vertix.weapons.notDangerous.HealthPotion;
import fr.lumin0u.vertix.weapons.notDangerous.MedecinePortable;
import fr.lumin0u.vertix.weapons.notDangerous.MontreInvi;
import fr.lumin0u.vertix.weapons.thingsLauncher.RocketLauncher;
import fr.lumin0u.vertix.weapons.ultimateWeapons.AbstractUltimateWeapon;
import fr.lumin0u.vertix.weapons.ultimateWeapons.corpsACorps.Kukri;
import fr.lumin0u.vertix.weapons.ultimateWeapons.guns.Scavenger;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.BeastFury;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.Disguise;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.RedButton;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.ScoutRace;
import fr.lumin0u.vertix.weapons.ultimateWeapons.notDangerous.UberCharge;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.CoktailMolotov;
import fr.lumin0u.vertix.weapons.ultimateWeapons.thingsLaunchers.Striker;

public enum Kit
{
	NOKIT(new AbstractWeapon[0], Kukri.getInstance(), 20, 0.2f, 1, 1, Material.BARRIER, 0, false, 0, 'a'),
	DEBUG(new AbstractWeapon[] {new MitrailletteLourde(), new Defoncator(), new Blaoups()}, Striker.getInstance(), 20, 0.2f, 1000, 20, Material.CARPET, 0, false, 0, 'z'),
	SCOUT(new AbstractWeapon[] {new CanonScie(), new Batte(), new Defenseur()}, new ScoutRace(), 16, 0.32f, 2, 1, Material.STAINED_CLAY, 8, true, 1, '░'),
	SOLDIER(new AbstractWeapon[] {new RocketLauncher(), new FusilAPompe(), new GrenadeFlash()}, new Scavenger(), 20, 0.26f, 1, 1, Material.STAINED_CLAY, 7, true, 2, '▒'),
	DEMOMAN(new AbstractWeapon[] {new Dynamite(), new Fumigene(), new FusilAPompe(), new FuseeDeDetresse()}, new Striker(), 20, 0.26f, 1, 1, Material.STAINED_CLAY, 5, true, 4, '│'),
	HEAVY(new AbstractWeapon[] {new LaTornade(), new FusilAPompe(), new PoingsAmericains()}, new BeastFury(), 32, 0.21f, 1, 2, Material.STAINED_CLAY, 4, true, 5, '┤'),
	SNIPER(new AbstractWeapon[] {new Sniper(),new  PistoletAutomatique(), new HealthPotion()}, new Kukri(), 18, 0.26f, 1, 1, Material.STAINED_CLAY, 1, true, 8, '╖'),
	SPY(new AbstractWeapon[] {new Poignard(), new C4(), new Revolver(), new MontreInvi()}, new Disguise(), 16, 0.3f, 2, 1, Material.STAINED_CLAY, 0, true, 9, '╕'),
	ENGINEER(new AbstractWeapon[] {new CanonMontable(), new Defenseur(), new Trampoline(), new Mine(), new CleMolette()}, new RedButton(), 20, 0.26f, 1, 1, Material.STAINED_CLAY, 3, true, 6, '╡'), 
	MEDIC(new AbstractWeapon[] {new MedecinePortable(), new PistoletTranquilisant(), new ScieAmputation()}, new UberCharge(), 20, 0.26f, 1, 2, Material.STAINED_CLAY, 2, true, 7, '╢'),
	PYRO(new AbstractWeapon[] {new Barbecue(), new FusilAPompe(), new Hache()}, new CoktailMolotov(), 24, 0.26f, 1, 1, Material.STAINED_CLAY, 6, true, 3, '▓');

	private AbstractWeapon[] weapons;
	private AbstractUltimateWeapon special;
	private int maxHealth;
	private float speed;
	private Material blockOnHead;
	private int dataBlockOnHead;
	private boolean realKit;
	private int place;
	private char symbole;
	private int valeurCart, valeurCap;
	private static ItemStack compass;
	private static ItemStack blazeRod;
	private static ItemStack nameTag;
	
	private Kit(AbstractWeapon[] weapons, AbstractUltimateWeapon special, int maxHealth, float speed, int valeurCart, int valeurCap, Material blockOnHead, int dataBlockOnHead, boolean realKit, int place, char symbole)
	{
		this.weapons = weapons;
		this.special = special;
		this.maxHealth = maxHealth;
		this.speed = speed;
		this.blockOnHead = blockOnHead;
		this.dataBlockOnHead = dataBlockOnHead;
		this.realKit = realKit;
		this.place = place;
		this.symbole = symbole;
		this.valeurCart = valeurCart;
		this.valeurCap = valeurCap;
	}

	public AbstractWeapon[] getWeapons()
	{
		return weapons;
	}
	
	public AbstractUltimateWeapon getSpecial()
	{
		return special;
	}
	
	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
	public ItemStack getBlockOnHead()
	{
		return new ItemStack(blockOnHead, 1, (short)dataBlockOnHead);
	}
	
	public boolean isReal()
	{
		return realKit;
	}
	
	public int placeInInventory()
	{
		return place;
	}
	
	public String getName()
	{
		return name().toCharArray()[0]+name().replaceFirst(String.valueOf(name().toCharArray()[0]), "").toLowerCase();
	}
	
	public char getSymbole()
	{
		return symbole;
	}
	
	public int getValeurCart()
	{
		return valeurCart;
	}
	
	public int getValeurCap()
	{
		return valeurCap;
	}
	
	public static Inventory getChangeKitInventory(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 6 * 9, "Choisissez un kit");
		
		for(int i = 0; i < Kit.values().length; i++)
		{
			if(!Kit.values()[i].isReal())
			{
				continue;
			}
			
			Kit k = Kit.values()[i];
			inv.setItem(8+k.placeInInventory(), k.getRepItem(GameManager.getInstance(p.getWorld()).getGameType()));
		}
		
		ItemStack dye1 = new ItemStack(Material.INK_SACK, 1, (short)3);
		ItemMeta dye1Meta = dye1.getItemMeta();
		dye1Meta.setDisplayName("§6ATTAQUE");
		List<String> dye1Lore = new ArrayList<>();
		dye1Lore.add("§7Classes efficaces en attaque");
		dye1Meta.setLore(dye1Lore);
		dye1.setItemMeta(dye1Meta);
		
		ItemStack dye2 = new ItemStack(Material.INK_SACK, 1, (short)5);
		ItemMeta dye2Meta = dye2.getItemMeta();
		dye2Meta.setDisplayName("§3DEFENSE");
		List<String> dye2Lore = new ArrayList<>();
		dye2Lore.add("§7Classes efficaces en défense");
		dye2Meta.setLore(dye2Lore);
		dye2.setItemMeta(dye2Meta);
		
		ItemStack dye3 = new ItemStack(Material.INK_SACK, 1, (short)6);
		ItemMeta dye3Meta = dye3.getItemMeta();
		dye3Meta.setDisplayName("§5SOUTIEN");
		List<String> dye3Lore = new ArrayList<>();
		dye3Lore.add("§7Classes efficaces en soutien");
		dye3Meta.setLore(dye3Lore);
		dye3.setItemMeta(dye3Meta);
		
		ItemStack dyeRandom = new ItemStack(Material.INK_SACK);
		ItemMeta dyeRandomMeta = dyeRandom.getItemMeta();
		dyeRandomMeta.setDisplayName("§c§kmm§r §fAléatoire §c§kmm");
		List<String> dyeRandomLore = new ArrayList<>();
		dyeRandomLore.add("§7Classe Aléatoire");
		dyeRandomMeta.setLore(dyeRandomLore);
		dyeRandom.setItemMeta(dyeRandomMeta);
		
		if(!GameManager.getInstance(p.getWorld()).isGameStarted())
		{
			ItemStack configKits = new ItemStack(Material.PAPER);
			ItemMeta configKitsMeta = configKits.getItemMeta();
			configKitsMeta.setDisplayName("§aChanger la configuration");
			List<String> configKitsLore = new ArrayList<>();
			configKitsLore.add("§7Modifiez la place de vos armes");
			configKitsMeta.setLore(configKitsLore);
			configKits.setItemMeta(configKitsMeta);
			
			inv.setItem(45, configKits);
		}
		
		if(GameManager.getInstance(p.getWorld()).getGameType().areTeamsActive())
		{
			for(Team t : GameManager.getInstance(p.getWorld()).getTeams())
			{
				inv.setItem(t.getPlace(), t.getRepItem());
			}
		}
		
		ItemStack tips;
		
		if(PlayerManager.getInstance().getTFPlayer(p).hasTipsActive())
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
		
		inv.setItem(1, dye1);
		inv.setItem(4, dye2);
		inv.setItem(7, dye3);
		inv.setItem(35, dyeRandom);
		inv.setItem(27, tips);
		
		return inv;
	}
	
	public static Inventory getConfigKitInventory()
	{
		Inventory inv = Bukkit.createInventory(null, 2 * 9, "Choisissez un kit à configurer");
		
		for(int i = 0; i < Kit.values().length; i++)
		{
			if(!Kit.values()[i].isReal())
				continue;
			
			Kit k = Kit.values()[i];
			inv.setItem(8+k.placeInInventory(), k.getRepItem());
		}
		
		ItemStack dye1 = new ItemStack(Material.INK_SACK, 1, (short)3);
		ItemMeta dye1Meta = dye1.getItemMeta();
		dye1Meta.setDisplayName("§6ATTAQUE");
		List<String> dye1Lore = new ArrayList<>();
		dye1Lore.add("§7Classes efficaces en attaque");
		dye1Meta.setLore(dye1Lore);
		dye1.setItemMeta(dye1Meta);
		
		ItemStack dye2 = new ItemStack(Material.INK_SACK, 1, (short)5);
		ItemMeta dye2Meta = dye2.getItemMeta();
		dye2Meta.setDisplayName("§3DEFENSE");
		List<String> dye2Lore = new ArrayList<>();
		dye2Lore.add("§7Classes efficaces en défense");
		dye2Meta.setLore(dye2Lore);
		dye2.setItemMeta(dye2Meta);
		
		ItemStack dye3 = new ItemStack(Material.INK_SACK, 1, (short)6);
		ItemMeta dye3Meta = dye3.getItemMeta();
		dye3Meta.setDisplayName("§5SOUTIEN");
		List<String> dye3Lore = new ArrayList<>();
		dye3Lore.add("§7Classes efficaces en soutien");
		dye3Meta.setLore(dye3Lore);
		dye3.setItemMeta(dye3Meta);
		
		return inv;
	}
	
	public ItemStack getRepItem(GameType gt)
	{
		ItemStack itemRep = new ItemStack(blockOnHead, 1, (short)dataBlockOnHead);
		ItemMeta itemRepMeta = itemRep.getItemMeta();
		itemRepMeta.setDisplayName("§6"+getName());
		List<String> itemRepLore = new ArrayList<>();
		
		for(AbstractWeapon weapon : weapons)
			itemRepLore.add("§9"+weapon.getName());
		
		itemRepLore.add("");
		itemRepLore.add("§7Vie : §6"+maxHealth);
		itemRepLore.add("§7Vitesse : §6"+(int)(speed*100));
		
		if(gt.isCarts())
			itemRepLore.add("§7Valeur minecart : §6"+valeurCart);
		
		if(gt.isKoth())
			itemRepLore.add("§7Valeur capture : §6"+valeurCap);
		
		itemRepMeta.setLore(itemRepLore);
		itemRep.setItemMeta(itemRepMeta);
		
		return itemRep;
	}
	
	public ItemStack getRepItem()
	{
		ItemStack itemRep = new ItemStack(blockOnHead, 1, (short)dataBlockOnHead);
		ItemMeta itemRepMeta = itemRep.getItemMeta();
		itemRepMeta.setDisplayName("§6"+getName());
		List<String> itemRepLore = new ArrayList<>();
		
		for(AbstractWeapon weapon : weapons)
			itemRepLore.add("§9"+weapon.getName());
		
		itemRepMeta.setLore(itemRepLore);
		itemRep.setItemMeta(itemRepMeta);
		
		return itemRep;
	}
	
	public static ItemStack cadenas()
	{
		blazeRod = new ItemStack(Material.BLAZE_ROD);
		ItemMeta blazeRodMeta = blazeRod.getItemMeta();
		blazeRodMeta.setDisplayName("§5VEROUILLE");
		List<String> blazeRodLore = new ArrayList<>();
		blazeRodLore.add("§7Effectuez un kill pour récupérer");
		blazeRodLore.add("§7votre capacité spéciale");
		blazeRodMeta.setLore(blazeRodLore);
		blazeRod.setItemMeta(blazeRodMeta);
		
		return blazeRod;
	}
	
	public static ItemStack boussole(Player p)
	{
		GameManager gm = GameManager.getInstance(p.getWorld());
		
		compass = new ItemStack(Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();
		List<String> compassLore = new ArrayList<>();
		
		if(gm.getGameType().isCarts())
		{
			compassMeta.setDisplayName(PlayerManager.getInstance().getTFPlayer(p).getTeamCartDirection().getPrefix()+"Minecart "+PlayerManager.getInstance().getTFPlayer(p).getTeamCartDirection().getName(false));
			compassLore.add("§7Indique la direction du minecart "+PlayerManager.getInstance().getTFPlayer(p).getTeamCartDirection().getName(false));
		}
		
		if(gm.getGameType().isTDM())
		{
			compassMeta.setDisplayName(p.getName());
			
			for(Entity ent : p.getWorld().getNearbyEntities(p.getCompassTarget(), 10, 10, 10))
				if(ent instanceof Player)
					compassMeta.setDisplayName(((Player)ent).getPlayerListName());
			
			compassLore.add("§7Indique la direction de "+compassMeta.getDisplayName());
		}
		
		if(gm.getGameType().isKoth())
		{
			compassMeta.setDisplayName("§bZone à capturer");
			compassLore.add("§7Indique la direction de la zone à capturer");
		}
		
		compassMeta.setLore(compassLore);
		compass.setItemMeta(compassMeta);
		
		return compass;
	}
	
	public static ItemStack nameTag()
	{
		nameTag = new ItemStack(Material.NAME_TAG);
		ItemMeta nameTagMeta = nameTag.getItemMeta();
		nameTagMeta.setDisplayName("§6Changer de classe");
		List<String> nameTagLore = new ArrayList<>();
		nameTagLore.add("§7Changer votre classe à votre prochaine mort");
		nameTagMeta.setLore(nameTagLore);
		nameTag.setItemMeta(nameTagMeta);
		
		return nameTag;
	}
	
	public static Kit byRepItem(ItemStack item)
	{
		for(Kit k : values())
		{
			if(k.getRepItem().getType().equals(item.getType()) && item.getItemMeta().getDisplayName().equals(k.getRepItem().getItemMeta().getDisplayName()))
				return k;
		}
		
		return null;
	}
}
