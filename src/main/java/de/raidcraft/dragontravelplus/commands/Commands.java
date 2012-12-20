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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            DragonTravelPlusModule.inst.loadConfig();
            StationManager.INST.loadExistingStations();
            ChatMessages.successfulReloaded((Player)sender);
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

            if(DragonGuardTrait.dragonGuards.containsKey(context.getString(0))) {
                DragonGuardTrait trait = DragonGuardTrait.dragonGuards.get(context.getString(0));
                trait.getNPC().destroy();
            }

            StationManager.INST.deleteStation(station);
            DynmapManager.INST.removeMarker(station);
            StationManager.INST.loadExistingStations();


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
            ChatMessages.success((Player)sender, "Du wurdest erfolgreich zur Station '" + station.getName() + "' geportet!");
        }
    }
}
