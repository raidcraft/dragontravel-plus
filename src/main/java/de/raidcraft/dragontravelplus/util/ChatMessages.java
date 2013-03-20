package de.raidcraft.dragontravelplus.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:11
 * Description:
 */
public class ChatMessages {

    private final static String CHAT_PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Dragon" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE;

    public static void noDragonGuardSelected(CommandSender player) {

        warn(player, "Du hast kein Dragonguard ausgewählt!");
    }

    public static void tooFewArguments(CommandSender player) {

        warn(player, "Du hast zu wenig Parameter angegeben!");
    }

    public static void stationNameSuccessfullyChanged(CommandSender player) {

        success(player, "Der Stationsname wurde erfolgreich geändert!");
    }

    public static void successfulReloaded(CommandSender player) {

        success(player, "DragonTravelPlus wurde neugeladen!");
    }

    public static void success(CommandSender player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.GREEN + msg
        );
    }

    public static void info(CommandSender player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.YELLOW + msg
        );
    }

    public static void warn(CommandSender player, String msg) {

        player.sendMessage(CHAT_PREFIX + ChatColor.RED + msg
        );
    }
}
