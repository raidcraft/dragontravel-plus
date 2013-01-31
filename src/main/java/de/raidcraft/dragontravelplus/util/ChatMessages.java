package de.raidcraft.dragontravelplus.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:11
 * Description:
 */
public class ChatMessages {

    private final static String CHAT_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "DTPlus" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;

    public static void noDragonGuardSelected(Player player) {

        warn(player, "Du hast kein Dragonguard ausgewählt!");
    }

    public static void tooFewArguments(Player player) {

        warn(player, "Du hast zu wenig Parameter angegeben!");
    }

    public static void stationNameSuccessfullyChanged(Player player) {

        success(player, "Der Stationsname wurde erfolgreich geändert!");
    }

    public static void successfulReloaded(Player player) {

        success(player, "DragonTravelPlus wurde neugeladen!");
    }

    public static void success(Player player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + msg
        );
    }

    public static void info(Player player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + msg
        );
    }

    public static void warn(Player player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.RED + msg
        );
    }
}
