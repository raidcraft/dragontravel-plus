package de.raidcraft.dragontravelplus.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.api.language.TranslationProvider;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.npc.NPCManager;
import de.raidcraft.dragontravelplus.paths.DynamicFlightPath;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.dragontravelplus.util.DynmapManager;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.rctravel.api.station.UnknownStationException;
import de.raidcraft.reference.Colors;
import de.raidcraft.util.CommandUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:01
 * Description:
 */
public class DTPCommands {

    public DTPCommands(DragonTravelPlusPlugin module) {

    }

    @Command(
            aliases = {"dragontravelplus", "dtp"},
            desc = "Control Dragonguard settings"
    )
    @NestedCommand(NestedDragonGuardCommands.class)
    public void dragontravelplus(CommandContext context, CommandSender sender) throws CommandException {

    }

    public static class NestedDragonGuardCommands {

        private final DragonTravelPlusPlugin plugin;
        private final TranslationProvider tr;
        private final StationManager stationManager;

        public NestedDragonGuardCommands(DragonTravelPlusPlugin module) {

            this.plugin = module;
            this.tr = plugin.getTranslationProvider();
            this.stationManager = plugin.getStationManager();
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload config and database"
        )
        @CommandPermissions("dragontravelplus.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            plugin.reload();
            tr.msg(sender, "cmd.reload", "Plugin was sucessfully reloaded!");
        }

        @Command(
                aliases = {"create", "new", "add"},
                flags = "mec:",
                desc = "Create new station",
                min = 2,
                usage = "<name> <displayname>"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            int costLevel = 1;
            boolean mainStation = false;
            boolean emergencyTarget = false;

            if (context.hasFlag('c')) {
                costLevel = context.getFlagInteger('c', 1);
            }

            if (context.hasFlag('m')) {
                mainStation = true;
            }

            if (context.hasFlag('e')) {
                emergencyTarget = true;
            }

            DragonStation station;
            try {
                station = stationManager.createNewStation(context.getString(0)
                        , context.getJoinedStrings(1)
                        , ((Player) sender).getLocation()
                        , costLevel
                        , mainStation
                        , emergencyTarget);
            } catch (UnknownStationException e) {
                throw new CommandException(e.getMessage());
            }

            NPCManager.createDragonGuard(station);

            // dynmap
            DynmapManager.INST.addStationMarker(station);

            tr.msg(sender, "cmd.station.create", "You have created a dragon station with the name: %s", station.getDisplayName());
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Remove dragon station",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.remove")
        public void remove(CommandContext context, CommandSender sender) throws CommandException {

            DragonStation station;
            try {
                station = (DragonStation) stationManager.getStation(context.getString(0));
            } catch (UnknownStationException e) {
                throw new CommandException(e.getMessage());
            }

            NPCManager.removeDragonGuard(station);

            stationManager.deleteStation(station);
            DynmapManager.INST.removeMarker(station);
            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();

            tr.msg(sender, "cmd.station.delete", "You deleted the dragon station: %s", station.getDisplayName());
        }

        @Command(
                aliases = {"warp", "tp"},
                desc = "Teleports a player to a station",
                min = 1,
                usage = "<player> <station>"
        )
        @CommandPermissions("dragontravelplus.warp")
        public void warp(CommandContext context, CommandSender sender) throws CommandException {

            DragonStation station;
            try {
                station = (DragonStation) stationManager.getStation(context.getString(0));
            } catch (UnknownStationException e) {
                throw new CommandException(e.getMessage());
            }
            Player player = CommandUtil.warp(context, sender, station.getLocation(), 1);
            tr.msg(player, "cmd.station.warp", "You have been teleported to the dragon station: %s", station.getDisplayName());
        }

        @Command(
                aliases = {"list"},
                flags = "ec:",
                desc = "Shows a list of all Stations"
        )
        public void list(CommandContext context, CommandSender sender) throws CommandException {

            String list = "";

            for (Station station : stationManager.getAllStations()) {

                if (station instanceof DragonStation) {
                    if (context.hasFlag('e')) {
                        if (!((DragonStation) station).isEmergencyTarget()) continue;
                    }
                    if (context.hasFlag('c')) {
                        if (!String.valueOf((int) ((DragonStation) station).getPrice()).equalsIgnoreCase(context.getFlag('c'))) continue;
                    }
                }

                ChatColor color = ChatColor.AQUA;
                if (station instanceof DragonStation && ((DragonStation) station).getPrice() > 0) color = ChatColor.GOLD;
                if (station instanceof DragonStation && ((DragonStation) station).isEmergencyTarget()) color = ChatColor.DARK_RED;
                list += color + station.getDisplayName() + ChatColor.WHITE + ", ";
            }

            sender.sendMessage(list);
        }

        @Command(
                aliases = {"discovered", "explored", "visited"},
                desc = "Show all discovered Dragonsations"
        )
        public void discovered(CommandContext context, CommandSender sender) throws CommandException {

            Player player = (Player) sender;
            String list = "";
            List<TStation> stations = plugin.getDatabase().find(TStation.class)
                    .fetch("playerStations")
                    .where().eq("player_id", player.getUniqueId().toString())
                    .isNotNull("discovered")
                    .findList();
            for (TStation station : stations) {
                list += Colors.Chat.INFO + station.getName() + ChatColor.WHITE + ", ";
            }
            if (list.equals("")) {
                list = "Du hast keine Drachenmeister gefunden.";
            } else {
                sender.sendMessage(Colors.Chat.SUCCESS + "Folgende Drachenmeister hast du gefunden:");
            }
            sender.sendMessage(list);
        }

        @Command(
                aliases = {"markers"},
                desc = "Recreate dynmap markers"
        )
        @CommandPermissions("dragontravelplus.markers")
        public void markers(CommandContext context, CommandSender sender) throws CommandException {

            int i = 0;
            for (Station station : stationManager.getAllStations()) {

                DynmapManager.INST.addStationMarker(station);
                i++;
            }
            tr.msg(sender, "cmd.dynmap", "Created %s dynmap markers.", i);
        }

        @Command(
                aliases = {"abortflights"},
                desc = "Aborts all active flights"
        )
        @CommandPermissions("dragontravelplus.abortflights")
        public void abortFlights(CommandContext context, CommandSender sender) throws CommandException {

            for (Flight flight : plugin.getFlightManager().getActiveFlights()) {
                flight.abortFlight();
            }
            tr.msg(sender, "cmd.abort-flights", "Aborted all active flights.");
        }

        @Command(
                aliases = {"debug"},
                desc = "Debug a flight",
                min = 2,
                usage = "<start_station> <end_station>"
        )
        @CommandPermissions("dragontravelplus.debug")
        public void debug(CommandContext context, CommandSender sender) throws CommandException {

            try {
                Location start = plugin.getStationManager().getStation(context.getString(0)).getLocation();
                Location end = plugin.getStationManager().getStation(context.getString(1)).getLocation();
                DynamicFlightPath path = new DynamicFlightPath(start, end);
                path.calculate();
                List<Waypoint> points = path.getWaypoints();
                for (Waypoint point : points) {
                    sender.sendMessage(point.getX() + ":" + point.getY() + ":" + point.getZ());
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    for (Waypoint point : points) {
                        player.sendBlockChange(point.getLocation(), Material.GLOWSTONE, (byte) 0);
                    }
                }
            } catch (UnknownStationException e) {
                sender.sendMessage(e.getMessage());
            }
        }
    }
}