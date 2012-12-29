package de.raidcraft.dragontravelplus.commands;

import com.silthus.raidcraft.util.component.DateUtil;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.dragontravelplus.util.DynmapManager;
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
    public Commands(DragonTravelPlusModule module) {

    }

    @Command(
            aliases = {"dragontravelplus", "dtp"},
            desc = "Control Dragonguard settings"
    )
    @NestedCommand(NestedDragonGuardCommands.class)
    public void dragontravelplus(CommandContext context, CommandSender sender) throws CommandException {
    }

    public static class NestedDragonGuardCommands {

        private final DragonTravelPlusModule module;

        public NestedDragonGuardCommands(DragonTravelPlusModule module) {

            this.module = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload config and database"
        )
        @CommandPermissions("dragontravelplus.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            DragonTravelPlusModule.inst.reloadAll();
            if(sender instanceof Player) ChatMessages.successfulReloaded((Player)sender);
            if(sender instanceof ConsoleCommandSender) sender.sendMessage("[DTP] DragonTravelPlus config successfully reloaded!");
        }

        @Command(
                aliases = {"create", "new", "add"},
                flags = "mec:",
                desc = "Create new station"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if(context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player)sender);
            }

            int costLevel = 1;
            boolean mainStation = false;
            boolean emergencyTarget = false;

            if(context.hasFlag('c')) {
                costLevel = context.getFlagInteger('c', 1);
            }

            if(context.hasFlag('m')) {
                mainStation = true;
            }

            if(context.hasFlag('e')) {
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
                ChatMessages.warn(((Player)sender), e.getMessage());
                return;
            }

            DragonGuardTrait.createDragonGuard(((Player) sender).getLocation(), station);

            // dynmap
            DynmapManager.INST.addStationMarker(station);

            ChatMessages.success(((Player)sender), "Du hast erfolgreich die Drachenstation '" + context.getString(0) + "' erstellt!");
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Remove dragon station"
        )
        @CommandPermissions("dragontravelplus.remove")
        public void remove(CommandContext context, CommandSender sender) throws CommandException {

            if(context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player)sender);
                return;
            }

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if(station == null) {
                ChatMessages.warn((Player)sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            DragonGuardTrait trait = DragonGuardTrait.getDragonGuard(context.getString(0));
            if(trait != null) {
                trait.getNPC().destroy();
            }

            StationManager.INST.deleteStation(station);
            DynmapManager.INST.removeMarker(station);
            DragonTravelPlusModule.inst.reloadAll();


            ChatMessages.success((Player) sender, "Die Drachenstation '" + context.getString(0) + "' wurde gelöscht!");
        }

        @Command(
                aliases = {"warp", "tp"},
                desc = "Teleports a player to a station"
        )
        @CommandPermissions("dragontravelplus.warp")
        public void warp(CommandContext context, CommandSender sender) throws CommandException {

            if(context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player)sender);
            }

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if(station == null) {
                ChatMessages.warn((Player)sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            ((Player)sender).teleport(station.getLocation());
            ChatMessages.success((Player)sender, "Du wurdest zur Station '" + station.getName() + "' geportet!");
        }

        @Command(
                aliases = {"list"},
                flags = "ec:",
                desc = "Shows a list of all Stations"
        )
        public void list(CommandContext context, CommandSender sender) throws CommandException {

            String list = "Alle verfügbaren Drachenstationen: ";
            
            for(Map.Entry<String, DragonStation> entry : StationManager.INST.existingStations.entrySet()) {

                if(context.hasFlag('e')) {
                    if(!entry.getValue().isEmergencyTarget()) continue;
                }

                if(context.hasFlag('c')) {
                    if(!String.valueOf(entry.getValue().getCostLevel()).equalsIgnoreCase(context.getFlag('c'))) continue;
                }

                ChatColor color = ChatColor.AQUA;
                if(entry.getValue().getCostLevel() > 0) color = ChatColor.GOLD;
                if(entry.getValue().isEmergencyTarget()) color = ChatColor.DARK_RED;
                list += color + entry.getKey() + ChatColor.WHITE + ", ";
            }
          
            ChatMessages.success((Player)sender, list);
        }
    }
}
