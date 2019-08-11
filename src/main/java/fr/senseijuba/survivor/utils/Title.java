package fr.senseijuba.survivor.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Title
{
	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String message)
	{
		sendTitle(player, fadeIn, stay, fadeOut, message, null);
	}

	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle)
	{
		try {//0 : title         //1 : subtitle              //2 : times            //3 : clear             //4 : reset
			
			Object packetPlayOutTimes = NMSUtils.getClass("PacketPlayOutTitle").getDeclaredConstructor(NMSUtils.getClass("EnumTitleAction"), NMSUtils.getClass("IChatBaseComponent"), int.class, int.class, int.class).newInstance(NMSUtils.getClass("EnumTitleAction").getEnumConstants()[2], null, fadeIn, stay, fadeOut);//(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
			PacketUtils.sendPacket(player, packetPlayOutTimes);
			
			if(subtitle != null)
			{
				subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
				subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
				
				Object subTitleMain = NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");
				Object packetPlayOutsubTitle = NMSUtils.getClass("PacketPlayOutTitle").getDeclaredConstructor(NMSUtils.getClass("EnumTitleAction"), NMSUtils.getClass("IChatBaseComponent")).newInstance(NMSUtils.getClass("EnumTitleAction").getEnumConstants()[1], subTitleMain);
				PacketUtils.sendPacket(player, packetPlayOutsubTitle);
			}
			
			if(title != null)
			{
				title = title.replaceAll("%player%", player.getDisplayName());
				title = ChatColor.translateAlternateColorCodes('&', title);
				
				
				Object titleMain = NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");//IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
				Object packetPlayOutTitle = NMSUtils.getClass("PacketPlayOutTitle").getDeclaredConstructor(NMSUtils.getClass("EnumTitleAction"), NMSUtils.getClass("IChatBaseComponent")).newInstance(NMSUtils.getClass("EnumTitleAction").getEnumConstants()[0], titleMain);//PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
				PacketUtils.sendPacket(player, packetPlayOutTitle);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void sendActionBar(Player player, String message)
	{
		try {
			
			Object cbc = NMSUtils.getClass("ChatSerializer").getDeclaredMethod("a", String.class).invoke(null, "{\"text\": \"" + message + "\"}");
			Object ppoc = NMSUtils.getClass("PacketPlayOutChat").getDeclaredConstructor(NMSUtils.getClass("IChatBaseComponent"), byte.class).newInstance(cbc, (byte) 2);
			
			PacketUtils.sendPacket(PacketUtils.getPlayerConnection(player), ppoc);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void sendActionBarAll(String message){
		for(Player p : Bukkit.getOnlinePlayers()){
			sendActionBar(p, message);
		}
	}
}

