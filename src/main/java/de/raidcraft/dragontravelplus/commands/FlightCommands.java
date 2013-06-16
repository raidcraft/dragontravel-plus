package de.raidcraft.dragontravelplus.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.dragontravelplus.flight.FlightEditorListener;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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
    public void flight(CommandContext context, CommandSender sender) throws CommandException {
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
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.editor.new")
        public void editor(CommandContext context, CommandSender sender) throws CommandException {

            String playerName = sender.getName();
            String flightName = context.getString(0);

            if(FlightEditorListener.hasEditorMode(playerName)) {
                throw new CommandException("Du bist bereits im Flugeditor Modus");
            }

            if(Database.getTable(FlightWayPointsTable.class).exists(flightName)) {
                throw new CommandException("Einen Flug mit diesem Namen existiert bereits!");
            }

            FlightEditorListener.addPlayer(playerName, flightName);
            ChatMessages.success(sender, "Flugeditor betreten!");
        }

        @Command(
                aliases = {"exit", "end", "close"},
                desc = "Close editor mode"
        )
        @CommandPermissions("dragontravelplus.editor.close")
        public void exit(CommandContext context, CommandSender sender) throws CommandException {

            String playerName = sender.getName();

            if(!FlightEditorListener.hasEditorMode(playerName)) {
                throw new CommandException("Du bist nicht im Flugeditor!");
            }

            if(FlightEditorListener.editors.get(playerName).size() > 0) {
                ChatMessages.warn(sender, "Du hast dein Flug noch nicht gespeichert!");
                ChatMessages.warn(sender, "Nutze '/dtp editor save'.");
                try {
                    new QueuedCommand(sender, this, "leaveEditor", sender);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            else {
                leaveEditor(sender);
            }
        }

        @Command(
                aliases = {"save"},
                desc = "Save flight"
        )
        @CommandPermissions("dragontravelplus.editor.save")
        public void save(CommandContext context, CommandSender sender) throws CommandException {

            String playerName = sender.getName();

            if(!FlightEditorListener.hasEditorMode(playerName)) {
                throw new CommandException("Du bist nicht im Flugeditor Modus!");
            }

            Flight fly = FlightEditorListener.editors.get(playerName);
            fly.save(playerName);
            String flightName = fly.getName();
            FlightEditorListener.removePlayer(playerName);
            ChatMessages.success(sender, "Dein Flug namens '" + flightName + "' wurde gespeichert!");
            leaveEditor(sender);
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Delete flight",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.editor.delete")
        public void delete(CommandContext context, CommandSender sender) throws CommandException {

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
        public void list(CommandContext context, CommandSender sender) throws CommandException {

            ChatMessages.success(sender, "Alle gespeicherten Rundflüge:");
            List<String> flightNames = Database.getTable(FlightWayPointsTable.class).getExistingFlightNames();

            String out = "";
            boolean colorToggle = false;
            for(String name : flightNames) {
                if(colorToggle) {
                    colorToggle = false;
                    out += ChatColor.YELLOW;
                }
                else {
                    colorToggle = true;
                    out += ChatColor.WHITE;
                }
                out += name + ", ";
            }
            sender.sendMessage(out);
        }


        @Command(
                aliases = {"fly"},
                desc = "Fly",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.fly.flight")
        public void fly(CommandContext context, CommandSender sender) throws CommandException {

            String flightName = context.getString(0);
            Flight flight = Flight.loadFlight(flightName);
            Player player = (Player)sender;

            if(flight == null) {
                throw new CommandException("Es existiert kein Flug mit diesem Namen!");
            }

            FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

            if(flyingPlayer != null && flyingPlayer.isInAir()) {
                ChatMessages.warn(player, "Du befindest dich bereits im Flug!");
                return;
            }

            FlightTravel.flyFlight(flight, player, RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightSpeed);
        }

        public void leaveEditor(CommandSender player) {
            FlightEditorListener.removePlayer(player.getName());
            ChatMessages.info(player, "Flugeditor verlassen!");
        }

        public void deleteFlight(CommandSender sender, String flightName) {
            Flight.removeFlight(flightName);
            ChatMessages.info(sender, "Der Flug namens '" + flightName + "' wurde gelöscht!");
        }
    }
}
