package de.raidcraft.dragontravelplus.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.FlightManager;
import de.raidcraft.dragontravelplus.RouteManager;
import de.raidcraft.dragontravelplus.listener.FlightEditorListener;
import de.raidcraft.dragontravelplus.tables.TPath;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * @author Philip
 */
public class FlightCommands {

    public FlightCommands(DragonTravelPlusPlugin module) {

    }

    @Command(
            aliases = {"flight"},
            desc = "Flight mode"
    )
    @CommandPermissions("dragontravelplus.flight")
    @NestedCommand(NestedFlightEditorCommands.class)
    public void flight(CommandContext context, CommandSender sender) {

    }

    public static class NestedFlightEditorCommands {

        private final DragonTravelPlusPlugin module;

        public NestedFlightEditorCommands(DragonTravelPlusPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"new"},
                desc = "Open editor mode",
                min = 1,
                usage = "<displayName>"
        )
        @CommandPermissions("dragontravelplus.editor.new")
        public void editor(CommandContext context, CommandSender sender) throws CommandException {

            Player player = (Player) sender;
            String flightName = context.getString(0);

            if (FlightEditorListener.hasEditorMode(player)) {
                throw new CommandException("Du bist bereits im Flugeditor Modus");
            }

            if (RaidCraft.getComponent(RouteManager.class).getPath(flightName).isPresent()) {
                throw new CommandException("Einen Flug mit diesem Namen existiert bereits!");
            }

            FlightEditorListener.addPlayer(player);
            sender.sendMessage(ChatColor.GREEN + "Flugeditor betreten!");
        }

        @Command(
                aliases = {"exit", "end", "close"},
                desc = "Close editor mode"
        )
        @CommandPermissions("dragontravelplus.editor.close")
        public void exit(CommandContext context, CommandSender sender) throws CommandException {

            Player player = (Player) sender;

            if (!FlightEditorListener.hasEditorMode(player)) {
                throw new CommandException("Du bist nicht im Flugeditor!");
            }

            if (FlightEditorListener.editors.get(player).getWaypointAmount() > 0) {
                sender.sendMessage(ChatColor.RED + "Du hast dein Flug noch nicht gespeichert!");
                sender.sendMessage(ChatColor.RED + "Nutze '/dtp editor save'.");
                try {
                    new QueuedCommand(sender, this, "leaveEditor", sender);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                leaveEditor(player);
            }
        }

        @Command(
                aliases = {"save"},
                desc = "Save flight"
        )
        @CommandPermissions("dragontravelplus.editor.save")
        public void save(CommandContext context, CommandSender sender) throws CommandException {

            Player player = (Player) sender;

            if(context.argsLength() < 1) {
                throw new CommandException("Gebe den Name des Fluges an den du speichern möchtest!");
            }

            String flightName = context.getString(0);

            if (!FlightEditorListener.hasEditorMode(player)) {
                throw new CommandException("Du bist nicht im Flugeditor Modus!");
            }

            if (RaidCraft.getComponent(RouteManager.class).getPath(flightName).isPresent()) {
                throw new CommandException("Einen Flug mit diesem Namen existiert bereits!");
            }

            Path path = FlightEditorListener.editors.get(player);
            RaidCraft.getComponent(RouteManager.class).savePath(path, flightName);
            FlightEditorListener.removePlayer(player);
            sender.sendMessage(ChatColor.GREEN + "Dein Flug namens '" + flightName + "' wurde gespeichert!");
            leaveEditor(player);
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Delete flight",
                min = 1,
                usage = "<displayName>"
        )
        @CommandPermissions("dragontravelplus.editor.delete")
        public void delete(CommandContext context, CommandSender sender) {

            String playerName = sender.getName();
            String flightName = context.getString(0);

            try {
                new QueuedCommand(sender, this, "deleteFlight", sender, flightName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        @Command(
                aliases = {"list"},
                desc = "List existing flights"
        )
        @CommandPermissions("dragontravelplus.flights.list")
        public void list(CommandContext context, CommandSender sender) {

            List<TPath> paths = RaidCraft.getDatabase(DragonTravelPlusPlugin.class)
                    .find(TPath.class).where().isNull("start_station_id").findList();

            String out = "";
            boolean colorToggle = false;
            for (TPath path : paths) {
                if (colorToggle) {
                    colorToggle = false;
                    out += ChatColor.YELLOW;
                } else {
                    colorToggle = true;
                    out += ChatColor.WHITE;
                }
                out += path.getName() + ", ";
            }
            sender.sendMessage(out);
        }


        @Command(
                aliases = {"fly"},
                desc = "Fly",
                min = 1,
                usage = "<displayName>"
        )
        @CommandPermissions("dragontravelplus.fly.flight")
        public void fly(CommandContext context, CommandSender sender) throws CommandException {

            String flightName = context.getString(0);
            Optional<Path> path = RaidCraft.getComponent(RouteManager.class).getPath(flightName);

            if (!path.isPresent()) {
                throw new CommandException("Der angegebene Flug exisitert nicht.");
            }

            Flight flight = RaidCraft.getComponent(FlightManager.class).createFlight((Player) sender, path.get());

            flight.startFlight();
        }

        public void leaveEditor(Player player) {

            FlightEditorListener.removePlayer(player);
            player.sendMessage(ChatColor.GREEN + "Flugeditor verlassen!");
        }

        public void deleteFlight(CommandSender sender, String flightName) {

            RaidCraft.getComponent(RouteManager.class).deletePath(flightName);
            sender.sendMessage(ChatColor.GREEN + "Der Flug namens '" + flightName + "' wurde gelöscht!");
        }
    }
}
