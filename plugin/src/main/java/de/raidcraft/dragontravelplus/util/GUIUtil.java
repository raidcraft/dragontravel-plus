package de.raidcraft.dragontravelplus.util;

import de.raidcraft.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Philip on 12.06.2015.
 */
public class GUIUtil {

    public static void setTitleBarText(Player player, String message){

        try {
            Class<?> craftPlayer = ReflectionUtil.getNmsClass("org.bukkit.craftbukkit", "entity", "CraftPlayer");
            Object p = craftPlayer.cast(player);
            Object ppoc = null;
            Class<?> playerOutChat = ReflectionUtil.getNmsClass("net.minecraft.server", "PacketPlayOutChat");
            Class<?> packet = ReflectionUtil.getNmsClass("net.minecraft.server", "Packet");

            String nmsver = Bukkit.getServer().getClass().getPackage().getName();
            nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

            if (nmsver.equalsIgnoreCase("v1_8_R1") || !nmsver.startsWith("v1_8_")) {
                Class<?> chatSerializer = ReflectionUtil.getNmsClass("net.minecraft.server", "ChatSerializer");
                Class<?> chatBaseComponent = ReflectionUtil.getNmsClass("net.minecraft.server", "IChatBaseComponent");
                Method m3 = chatSerializer.getDeclaredMethod("a", new Class<?>[] {String.class});
                Object cbc = chatBaseComponent.cast(m3.invoke(chatSerializer, "{\"text\": \"" + message + "\"}"));
                ppoc = playerOutChat.getConstructor(new Class<?>[] {chatBaseComponent, byte.class}).newInstance(new Object[] {cbc, (byte) 2});
            } else {
                Class<?> chatComponentText = ReflectionUtil.getNmsClass("net.minecraft.server", "ChatComponentText");
                Class<?> chatBaseComponent = ReflectionUtil.getNmsClass("net.minecraft.server", "IChatBaseComponent");
                Object o = chatComponentText.getConstructor(new Class<?>[] {String.class}).newInstance(new Object[] {message});
                ppoc = playerOutChat.getConstructor(new Class<?>[] {chatBaseComponent, byte.class}).newInstance(new Object[] {o, (byte) 2});
            }
            Method m1 = craftPlayer.getDeclaredMethod("getHandle", new Class<?>[] {});
            Object handle = m1.invoke(p);
            Field playerConnection = handle.getClass().getDeclaredField("playerConnection");
            Object pc = playerConnection.get(handle);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket",new Class<?>[] {packet});
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
