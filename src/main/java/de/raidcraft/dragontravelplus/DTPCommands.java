package de.raidcraft.dragontravelplus;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.dragontravelplus.eceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 25.11.12 - 19:01
 * Description:
 */
public class DTPCommands {
    public DTPCommands(DragonTravelPlusModule module) {

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
                flags = "mec:",
                desc = "Create new station"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if(context.argsLength() < 1) {
                ChatMessages.tooFewArguments((Player)sender);
            }

            int costLevel = 0;
            boolean mainStation = false;
            boolean emergencyTarget = false;

            if(context.hasFlag('c')) {
                costLevel = context.getFlagInteger('c', 0);
            }

            if(context.hasFlag('m')) {
                mainStation = true;
            }

            if(context.hasFlag('e')) {
                emergencyTarget = true;
            }

            try {
                StationManager.INST.addNewStation(
                        new DragonStation(context.getString(0), ((Player) sender).getLocation(), costLevel, mainStation, emergencyTarget));
            } catch (AlreadyExistsException e) {
                ChatMessages.warn(((Player)sender), e.getMessage());
            }

            ChatMessages.success(((Player)sender), "Du hast erfolgreich die Station " + context.getString(0) + " erstellt!");
        }
    }
}
