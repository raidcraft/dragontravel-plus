package de.raidcraft.dragontravelplus.npc;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.dragontravelplus.DragonStation;
import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.StationManager;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:01
 * Description:
 */
public class NPCCommands {
    public NPCCommands(DragonTravelPlusModule module) {

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
                aliases = {"create", "new", "add"},
                desc = "Create new station"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

        }

        @Command(
                aliases = {"stationname", "sn"},
                desc = "Set the name of the dragon station"
        )
        @CommandPermissions("dragontravelplus.guard.stationname")
        public void stationname(CommandContext context, CommandSender sender) throws CommandException {

            DragonStation station = StationManager.INST.getSelectedDragonStation(sender.getName());

            if(station == null) {
                ChatMessages.noDragonGuardSelected((Player)sender);
            }

            if(context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player)sender);
            }

            station.setName(context.getString(0));
            ChatMessages.stationNameSuccessfullyChanged((Player)sender);
        }
    }
}
