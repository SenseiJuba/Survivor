package fr.senseijuba.survivor.utils;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DeadBodies {

    public DeadBodies(Player p) throws Exception {
        playFakeBed(p);
    }

    void playFakeBed(Player p) throws Exception {
        BlockPosition pos =
                new BlockPosition(p.getLocation().getBlockX(), 0, p.getLocation().getBlockZ());
        playFakeBed(p, pos);
    }

    @Getter
    HashMap<UUID, Integer> playerId = new HashMap<>();
    HashMap<UUID, Location> corpsLoc = new HashMap<>();
    int entityId = 0;

    @SuppressWarnings("deprecation")
    void playFakeBed(Player p, BlockPosition pos) throws Exception {

        PacketPlayOutNamedEntitySpawn packetEntitySpawn = new PacketPlayOutNamedEntitySpawn();

        CraftPlayer p1 = (CraftPlayer) p;

        double locY = ((EntityHuman) p1.getHandle()).locY;

        DataWatcher dw = clonePlayerDatawatcher(p, entityId);
        dw.watch(10, p1.getHandle().getDataWatcher().getByte(10));

        playerId.put(p.getUniqueId(), entityId);
        corpsLoc.put(p.getUniqueId(), p.getLocation());

        GameProfile prof = new GameProfile(p1.getUniqueId(), p1.getName());

        PacketPlayOutPlayerInfo packetInfo =
                new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        PacketPlayOutPlayerInfo.PlayerInfoData data = packetInfo.new PlayerInfoData(prof, 0,
                WorldSettings.EnumGamemode.SURVIVAL, new ChatMessage("", new Object[0]));
        List<PacketPlayOutPlayerInfo.PlayerInfoData> dataList = Lists.newArrayList();
        dataList.add(data);
        setValue(packetInfo, "b", dataList);

        setValue(packetEntitySpawn, "a", entityId);
        setValue(packetEntitySpawn, "b", prof.getId());
        setValue(packetEntitySpawn, "c", MathHelper.floor(((EntityHuman) p1.getHandle()).locX * 32D));
        setValue(packetEntitySpawn, "d", MathHelper.floor(locY * 32D));
        setValue(packetEntitySpawn, "e", MathHelper.floor(((EntityHuman) p1.getHandle()).locZ * 32D));
        setValue(packetEntitySpawn, "f",
                (byte) ((int) (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F)));
        setValue(packetEntitySpawn, "g",
                (byte) ((int) (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F)));
        setValue(packetEntitySpawn, "i", dw);

        PacketPlayOutBed packetBed = new PacketPlayOutBed();

        setValue(packetBed, "a", entityId);
        setValue(packetBed, "b", pos);

        PacketPlayOutEntityTeleport packetTeleport = new PacketPlayOutEntityTeleport();
        setValue(packetTeleport, "a", entityId);
        setValue(packetTeleport, "b", MathHelper.floor(((EntityHuman) p1.getHandle()).locX * 32.0D));
        setValue(packetTeleport, "c", MathHelper.floor(locY * 32.0D));
        setValue(packetTeleport, "d", MathHelper.floor(((EntityHuman) p1.getHandle()).locZ * 32.0D));
        setValue(packetTeleport, "e",
                (byte) ((int) (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F)));
        setValue(packetTeleport, "f",
                (byte) ((int) (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F)));
        setValue(packetTeleport, "g", true);

        PacketPlayOutEntityTeleport packetTeleportDown = new PacketPlayOutEntityTeleport();
        setValue(packetTeleportDown, "a", entityId);
        setValue(packetTeleportDown, "b",
                MathHelper.floor(((EntityHuman) p1.getHandle()).locX * 32.0D));
        setValue(packetTeleportDown, "c", 0);
        setValue(packetTeleportDown, "d",
                MathHelper.floor(((EntityHuman) p1.getHandle()).locZ * 32.0D));
        setValue(packetTeleportDown, "e",
                (byte) ((int) (((EntityHuman) p1.getHandle()).yaw * 256.0F / 360.0F)));
        setValue(packetTeleportDown, "f",
                (byte) ((int) (((EntityHuman) p1.getHandle()).pitch * 256.0F / 360.0F)));
        setValue(packetTeleportDown, "g", true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = p.getLocation().clone();
            player.sendBlockChange(loc.subtract(0, ((Location) loc).getY(), 0), Material.BED_BLOCK, (byte) 0);

            CraftPlayer pl = ((CraftPlayer) player);
            if (player != p) {
                pl.getHandle().playerConnection.sendPacket(packetInfo);
                pl.getHandle().playerConnection.sendPacket(packetEntitySpawn);
                pl.getHandle().playerConnection.sendPacket(packetTeleportDown);
                pl.getHandle().playerConnection.sendPacket(packetBed);
                pl.getHandle().playerConnection.sendPacket(packetTeleport);
            }
        }

        dataList.clear();

        entityId++;
    }


    public static DataWatcher clonePlayerDatawatcher(Player player, int currentEntId) {

        EntityHuman h = new EntityHuman(((CraftWorld) player.getWorld()).getHandle(),

                ((CraftPlayer) player).getProfile()) {

            public void sendMessage(IChatBaseComponent arg0) {

                return;

            }


            public boolean a(int arg0, String arg1) {

                return false;

            }


            public BlockPosition getChunkCoordinates() {

                return null;

            }


            public boolean isSpectator() {

                return false;

            }

        };

        h.d(currentEntId);

        return h.getDataWatcher();

    }

    void setValue(Object instance, String fieldName, Object value) throws Exception {

        Field field = instance.getClass().getDeclaredField(fieldName);

        field.setAccessible(true);

        field.set(instance, value);

    }

    public void destroyDeadBodies(Player deadbodie){
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy();

        int id = playerId.get(deadbodie.getUniqueId());

        try {
            setValue(destroy, "a", id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Player player : Bukkit.getOnlinePlayers()){
            CraftPlayer pl = ((CraftPlayer) player);

            pl.getHandle().playerConnection.sendPacket(destroy);

        }

        corpsLoc.remove(deadbodie.getUniqueId());
        playerId.remove(deadbodie.getUniqueId());
    }

    public UUID nearDeadCorps(Location loc){

        UUID playerUUID = null;

        for(UUID uuid: corpsLoc.keySet()){
            Location loc2 = corpsLoc.get(uuid);
            playerUUID = (loc.getX() > loc2.getX()-1 && loc.getX() < loc2.getX()+1 && loc.getY() > loc2.getY()-1 && loc.getY() < loc2.getY()+1 && loc.getZ() > loc2.getZ()-1 && loc.getZ() < loc2.getZ()+1) ? uuid : null;

        }

        return playerUUID;
    }
}
