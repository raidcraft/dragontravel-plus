package de.raidcraft.dragontravelplus.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.commands.QueuedCommand;
import de.raidcraft.api.database.Database;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.ControlledFlight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightEditorListener;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.dragontravelplus.util.DynmapManager;
import de.raidcraft.util.DateUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:01
 * Description:
 */
public class Commands {

    public Commands(DragonTravelPlusPlugin module) {

    }

    @Command(
            aliases = {"dragontravelplus", "dtp"},
            desc = "Control Dragonguard settings"
    )
    @NestedCommand(NestedDragonGuardCommands.class)
    public void dragontravelplus(CommandContext context, CommandSender sender) throws CommandException {

    }

    public static class NestedDragonGuardCommands {

        private final DragonTravelPlusPlugin module;

        public NestedDragonGuardCommands(DragonTravelPlusPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload config and database"
        )
        @CommandPermissions("dragontravelplus.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();
            if (sender instanceof Player) ChatMessages.successfulReloaded((Player) sender);
            if (sender instanceof ConsoleCommandSender) sender.sendMessage("[DTP] DragonTravelPlus config successfully reloaded!");
        }

        @Command(
                aliases = {"create", "new", "add"},
                flags = "mec:",
                desc = "Create new station"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if (context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player) sender);
            }

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

            DragonStation station = new DragonStation(context.getString(0)
                    , ((Player) sender).getLocation()
                    , costLevel
                    , mainStation
                    , emergencyTarget
                    , sender.getName()
                    , DateUtil.getCurrentDateString());
            ;
            try {
                StationManager.INST.addNewStation(station);
            } catch (AlreadyExistsException e) {
                ChatMessages.warn(((Player) sender), e.getMessage());
                return;
            }

            DragonGuardTrait.createDragonGuard(((Player) sender).getLocation(), station);

            // dynmap
            DynmapManager.INST.addStationMarker(station);

            ChatMessages.success(((Player) sender), "Du hast erfolgreich die Drachenstation '" + context.getString(0) + "' erstellt!");
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Remove dragon station"
        )
        @CommandPermissions("dragontravelplus.remove")
        public void remove(CommandContext context, CommandSender sender) throws CommandException {

            if (context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player) sender);
                return;
            }

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if (station == null) {
                ChatMessages.warn((Player) sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            DragonGuardTrait trait = DragonGuardTrait.getDragonGuard(context.getString(0));
            if (trait != null) {
                trait.getNPC().destroy();
            }

            StationManager.INST.deleteStation(station);
            DynmapManager.INST.removeMarker(station);
            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();


            ChatMessages.success((Player) sender, "Die Drachenstation '" + context.getString(0) + "' wurde gelöscht!");
        }

        @Command(
                aliases = {"warp", "tp"},
                desc = "Teleports a player to a station"
        )
        @CommandPermissions("dragontravelplus.warp")
        public void warp(CommandContext context, CommandSender sender) throws CommandException {

            if (context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player) sender);
            }

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if (station == null) {
                ChatMessages.warn((Player) sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            ((Player) sender).teleport(station.getLocation());
            ChatMessages.success((Player) sender, "Du wurdest zur Station '" + station.getName() + "' geportet!");
        }

        @Command(
                aliases = {"list"},
                flags = "ec:",
                desc = "Shows a list of all Stations"
        )
        public void list(CommandContext context, CommandSender sender) throws CommandException {

            ChatMessages.success((Player) sender, "Alle verfügbaren Drachenstationen in dieser Welt:");
            String list = "";

            for (Map.Entry<String, DragonStation> entry : StationManager.INST.existingStations.entrySet()) {

                if (context.hasFlag('e')) {
                    if (!entry.getValue().isEmergencyTarget()) continue;
                }

                if (context.hasFlag('c')) {
                    if (!String.valueOf(entry.getValue().getCostLevel()).equalsIgnoreCase(context.getFlag('c'))) continue;
                }

                if(!entry.getValue().getLocation().getWorld().getName().equalsIgnoreCase(((Player)sender).getLocation().getWorld().getName())) {
                    continue;
                }

                ChatColor color = ChatColor.AQUA;
                if (entry.getValue().getCostLevel() > 0) color = ChatColor.GOLD;
                if (entry.getValue().isEmergencyTarget()) color = ChatColor.DARK_RED;
                list += color + entry.getKey() + ChatColor.WHITE + ", ";
            }

            sender.sendMessage(list);
        }

        @Command(
                aliases = {"fly"},
                desc = "Start controlledflyght"
        )
        @CommandPermissions("dragontravelplus.fly.controlled")
        public void controlledFlight(CommandContext context, CommandSender sender) throws CommandException {

            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage("Player context required!");
                return;
            }
            Player player = (Player)sender;

            int duration = 0;

            if(context.argsLength() > 0) {
                duration = context.getInteger(0);
            }

            FlyingPlayer flyingPlayer = DragonManager.INST.getFlyingPlayer(player.getName());

            if(flyingPlayer != null && flyingPlayer.isInAir()) {
                ChatMessages.warn(player, "Du befindest dich bereits im Flug!");
                return;
            }

            ControlledFlight controlledFlight = new ControlledFlight(duration);
            flyingPlayer = new FlyingPlayer(player, player.getLocation());
            flyingPlayer.setInAir(true);
            DragonManager.INST.flyingPlayers.put(player, flyingPlayer);
            FlightTravel.flyControlled(controlledFlight, player);
            ChatMessages.success(player, "Freier Flug gestartet!");
            if(duration > 0) {
                ChatMessages.info(player, "Flugzeit: " + duration + "s");
            }
        }

        @Command(
                aliases = {"flight"},
                desc = "Fly flight",
                min = 1
        )
        @CommandPermissions("dragontravelplus.fly.flight")
        public void flight(CommandContext context, CommandSender sender) throws CommandException {

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

            flyingPlayer = new FlyingPlayer(player, player.getLocation());
            flyingPlayer.setInAir(true);
            DragonManager.INST.flyingPlayers.put(player, flyingPlayer);
            FlightTravel.flyFlight(flight, player);
        }

        @Command(
                aliases = {"editor"},
                desc = "Editor mode"
        )
        @CommandPermissions("dragontravelplus.editor")
        @NestedCommand(NestedFlightEditorCommands.class)
        public void editor(CommandContext context, CommandSender sender) throws CommandException {
        }
    }

    public static class NestedFlightEditorCommands {

        private final DragonTravelPlusPlugin module;

        public NestedFlightEditorCommands(DragonTravelPlusPlugin module) {

            this.module = module;
        }

        @Command(
                aliases = {"new"},
                desc = "Open editor mode",
                min = 1
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

            if(FlightEditorListener.editors.get(playerName).waypointCount() > 0) {
                ChatMessages.warn(sender, "Du hast dein Flug noch nicht gespeichert!");
                ChatMessages.warn(sender, "Nutze '/dtp editor save'.");
                ChatMessages.info(sender, "Mit '/rcconfirm' kannst du den Flugeditor verlassen!");
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
                throw new CommandException("Du bist nicht im Flugeditor und kannst deshalb keinen Flug speichern!");
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
                min = 1
        )
        @CommandPermissions("dragontravelplus.editor.delete")
        public void delete(CommandContext context, CommandSender sender) throws CommandException {

            String playerName = sender.getName();
            String flightName = context.getString(0);

            ChatMessages.warn(sender, "Bestätige das Löschen mit '/rcconfirm'!");
            try {
                new QueuedCommand(sender, this, "deleteFlight", sender, flightName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
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
