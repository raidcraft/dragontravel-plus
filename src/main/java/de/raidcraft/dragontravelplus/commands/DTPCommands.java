package de.raidcraft.dragontravelplus.commands;

import com.sk89q.minecraft.util.commands.*;
import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.ControlledFlight;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.exceptions.AlreadyExistsException;
import de.raidcraft.dragontravelplus.npc.NPCManager;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.dragontravelplus.util.DynmapManager;
import de.raidcraft.rcconversations.RCConversationsPlugin;
import de.raidcraft.rcconversations.npc.NPCRegistry;
import de.raidcraft.rcconversations.util.ChunkLocation;
import de.raidcraft.util.DateUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

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

        public NestedDragonGuardCommands(DragonTravelPlusPlugin module) {

            this.plugin = module;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reload config and database"
        )
        @CommandPermissions("dragontravelplus.reload")
        public void reload(CommandContext context, CommandSender sender) throws CommandException {

            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();
            if (sender instanceof Player) ChatMessages.successfulReloaded( sender);
            if (sender instanceof ConsoleCommandSender) sender.sendMessage("[DTP] DragonTravelPlus config successfully reloaded!");
        }

        @Command(
                aliases = {"create", "new", "add"},
                flags = "mec:",
                desc = "Create new station",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.create")
        public void create(CommandContext context, CommandSender sender) throws CommandException {

            if (context.argsLength() < 1) {
                ChatMessages.tooFewArguments( sender);
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
                ChatMessages.warn(( sender), e.getMessage());
                return;
            }

            NPCManager.createDragonGuard(station);

            // dynmap
            DynmapManager.INST.addStationMarker(station);

            ChatMessages.success((sender), "Du hast erfolgreich die Drachenstation '" + station.getFriendlyName() + "' erstellt!");
        }

        @Command(
                aliases = {"delete", "remove"},
                desc = "Remove dragon station",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.remove")
        public void remove(CommandContext context, CommandSender sender) throws CommandException {

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if (station == null) {
                ChatMessages.warn(sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            NPCManager.removeDragonGuard(station);

            StationManager.INST.deleteStation(station);
            DynmapManager.INST.removeMarker(station);
            RaidCraft.getComponent(DragonTravelPlusPlugin.class).reload();


            ChatMessages.success(sender, "Die Drachenstation '" + context.getString(0) + "' wurde gelöscht!");
        }

        @Command(
                aliases = {"warp", "tp"},
                desc = "Teleports a player to a station",
                min = 1,
                usage = "<name>"
        )
        @CommandPermissions("dragontravelplus.warp")
        public void warp(CommandContext context, CommandSender sender) throws CommandException {

            DragonStation station = StationManager.INST.getDragonStation(context.getString(0));

            if (station == null) {
                ChatMessages.warn(sender, "Es gibt keine Station mit diesem Namen!");
                return;
            }

            ((Player) sender).teleport(station.getLocation());
            ChatMessages.success(sender, "Du wurdest zur Station '" + station.getFriendlyName() + "' geportet!");
        }

        @Command(
                aliases = {"list"},
                flags = "ec:",
                desc = "Shows a list of all Stations"
        )
        public void list(CommandContext context, CommandSender sender) throws CommandException {

            ChatMessages.success( sender, "Alle verfügbaren Drachenstationen in dieser Welt:");
            String list = "";

            for (Map.Entry<String, DragonStation> entry : StationManager.INST.existingStations.entrySet()) {

                if (context.hasFlag('e')) {
                    if (!entry.getValue().isEmergencyTarget()) continue;
                }

                if (context.hasFlag('c')) {
                    if (!String.valueOf(entry.getValue().getCostLevel()).equalsIgnoreCase(context.getFlag('c'))) continue;
                }

                if(!entry.getValue().getLocation().getWorld().getName().equalsIgnoreCase(((Player) sender).getLocation().getWorld().getName())) {
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
            FlightTravel.flyControlled(controlledFlight, player);
            ChatMessages.success(player, "Freier Flug gestartet!");
            if(duration > 0) {
                ChatMessages.info(player, "Flugzeit: " + duration + "s");
            }
        }

        @Command(
                aliases = {"markers"},
                desc = "Recreate dynmap markers"
        )
        @CommandPermissions("dragontravelplus.markers")
        public void markers(CommandContext context, CommandSender sender) throws CommandException {

            ChatMessages.info(sender, "Dynmap Stationsmarker werden neu erstellt...");
            int i = 0;
            for(DragonStation station : StationManager.INST.getStations()) {

                DynmapManager.INST.addStationMarker(station);
                i++;
            }
            ChatMessages.success(sender, "Es wurden " + i + " Marker neu erstellt!");
        }

        @Command(
                aliases = {"inair"},
                desc = "Shows all players currently in air"
        )
        @CommandPermissions("dragontravelplus.markers")
        public void inAir(CommandContext context, CommandSender sender) throws CommandException {

            Collection<FlyingPlayer> flyingPlayers = DragonManager.INST.getFlyingPlayers();

            if(flyingPlayers.size() == 0) {
                throw new CommandException("Es befinden sich derzeit alle Spieler am Boden!");
            }

            sender.sendMessage(ChatColor.YELLOW + "Folgende Spieler fliegen gerade mit einem Drache:");
            String msg = "";
            for(FlyingPlayer flyingPlayer : flyingPlayers) {

                if(!flyingPlayer.isInAir()) continue;

                msg += flyingPlayer.getPlayer().getName() + ", ";
            }
            sender.sendMessage(ChatColor.YELLOW + msg);
        }

        @Command(
                aliases = {"debug"},
                desc = "Debug"
        )
        @CommandPermissions("dragontravelplus.debug")
        public void debug(CommandContext context, CommandSender sender) throws CommandException {

            Player player = (Player)sender;
            Chunk chunk = player.getLocation().getChunk();

            int entityCount = 0;
            int npcMethodCount = 0;
            int npcMetaCount = 0;

            for(ChunkLocation cl : NPCRegistry.INST.getAffectedChunkLocations(chunk)) {
                for(Entity entity : chunk.getWorld().getChunkAt(cl.getX(), cl.getZ()).getEntities()) {
                    if(!(entity instanceof LivingEntity)) continue;
                    entityCount++;
                    NPC npc = RaidCraft.getComponent(RCConversationsPlugin.class).getCitizens().getNPCRegistry().getNPC(entity);
                    if(npc != null) npcMethodCount++;
                    if(entity.hasMetadata("NPC")) npcMetaCount++;
                }
            }

            player.sendMessage("Living-Entities in affected chunks: " + entityCount);
            player.sendMessage("NPC-Entities according to getNPC(): " + npcMethodCount);
            player.sendMessage("NPC-Entities according to MetaData: " + npcMetaCount);
            player.sendMessage("NPC-Entities according to Registry: " + NPCRegistry.INST.getSpawnedNPCs(chunk).size());
        }
    }
}
