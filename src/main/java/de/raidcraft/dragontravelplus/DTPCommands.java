package de.raidcraft.dragontravelplus;

import com.silthus.raidcraft.util.component.DateUtil;
import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.dragontravelplus.eceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
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
                        new DragonStation(context.getString(0), ((Player) sender).getLocation(), costLevel, mainStation, emergencyTarget, sender.getName(), DateUtil.getCurrentDateString()));
            } catch (AlreadyExistsException e) {
                ChatMessages.warn(((Player)sender), e.getMessage());
            }

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, DragonTravelPlusModule.inst.config.npcDefaultName);
            npc.spawn(((Player) sender).getLocation());
            npc.addTrait(LookClose.class);
            npc.getTrait(LookClose.class).lookClose(true);
            npc.addTrait(Text.class);
            npc.addTrait(DragonGuardTrait.class);

            ChatMessages.success(((Player)sender), "Du hast erfolgreich die Drachenstation '" + context.getString(0) + "' erstellt!");
        }
    }
}
