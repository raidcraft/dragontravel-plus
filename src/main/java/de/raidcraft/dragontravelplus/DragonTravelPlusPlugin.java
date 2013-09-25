package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.conversations.*;
import de.raidcraft.dragontravelplus.dragoncontrol.DragonManager;
import de.raidcraft.dragontravelplus.dragoncontrol.FlyingPlayer;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.RCDragon;
import de.raidcraft.dragontravelplus.flight.FlightEditorListener;
import de.raidcraft.dragontravelplus.listener.ChunkListener;
import de.raidcraft.dragontravelplus.listener.DragonListener;
import de.raidcraft.dragontravelplus.listener.PlayerListener;
import de.raidcraft.dragontravelplus.npc.DragonGuardTrait;
import de.raidcraft.dragontravelplus.station.StationManager;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.dragontravelplus.tables.StationTable;
import de.raidcraft.dragontravelplus.util.ChatMessages;
import de.raidcraft.rcconversations.actions.ActionManager;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.minecraft.server.v1_6_R3.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin {

    private Citizens citizens;
    private LocalDTPConfiguration config;

    @Override
    public void enable() {

        // Add our new entity to minecraft entities
        try {
            Method method = EntityTypes.class.getDeclaredMethod("a", new Class[]{Class.class, String.class, int.class});
            method.setAccessible(true);
            method.invoke(EntityTypes.class, RCDragon.class, "RCDragon", 63);
            getLogger().warning("[DragonTravelPlus] Successfully registered RCDragon entity!");
        } catch (Exception e) {
            getLogger().warning("[DragonTravelPlus] Error registering Entity! DISABLING!");
            disable();
            return;
        }

        loadConfig();

        registerEvents(new PlayerListener());
        registerEvents(new ChunkListener());
        registerEvents(new DragonListener());
        registerEvents(new FlightEditorListener());

        registerCommands(DTPCommands.class);
        registerCommands(FlightCommands.class);

        registerTable(StationTable.class, new StationTable());
        registerTable(PlayerStationsTable.class, new PlayerStationsTable());
        registerTable(FlightWayPointsTable.class, new FlightWayPointsTable());

        StationManager.INST.loadExistingStations();

        try {
//            registerEvents(new NPCListener());
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(DragonGuardTrait.class).withName("dragonguard"));
            citizens = (Citizens)Bukkit.getPluginManager().getPlugin("Citizens");
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("[DTP] Can't load NPC stuff! Citizens not found!");
        }

        // lets trigger a delayed load to make sure all plugins are loaded
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {

                try {
                    ActionManager.registerAction(new FlyFlightAction());
                    ActionManager.registerAction(new FlyControlledAction());
                    ActionManager.registerAction(new FlyToStationAction());
                    ActionManager.registerAction(new ListStationsAction());
                    ActionManager.registerAction(new FindDragonstationAction());
                    ActionManager.registerAction(new CheckPlayerAction());
                } catch (Exception e) {
                    RaidCraft.LOGGER.warning("[DTP] Can't load Actions! RCConversations not found!");
                }
            }
        }, 1L);
    }

    @Override
    public void disable() {
        // remove all dragons in the world
        for (Map.Entry<Player, FlyingPlayer> entry : DragonManager.INST.getFlyingPlayers().entrySet()) {
            if (entry.getValue().isInAir()) {
                DragonManager.INST.abortFlight(entry.getKey());
            }
        }
    }

    public void loadConfig() {

        config = configure(new LocalDTPConfiguration(this));
    }

    @Override
    public void reload() {

        config.reload();
        // remove all dragons in the world
        Map<Player, FlyingPlayer> flyingPlayersCopy = new HashMap<>(DragonManager.INST.getFlyingPlayers());
        for (Map.Entry<Player, FlyingPlayer> entry : flyingPlayersCopy.entrySet()) {
            if (entry.getValue().isInAir()) {
                DragonManager.INST.abortFlight(entry.getKey());
                ChatMessages.warn(entry.getKey(), "Der Flug musste aus technischen Gruenden abgebrochen werden!");
            }
        }

        DragonManager.INST.getFlyingPlayers().clear();
        StationManager.INST.loadExistingStations();
    }

    public Citizens getCitizens() {

        return citizens;
    }

    public LocalDTPConfiguration getConfig() {

        return config;
    }

    public class LocalDTPConfiguration extends ConfigurationBase<DragonTravelPlusPlugin> {

        @Setting("disabled")
        public boolean disabled = false;
        @Setting("error-prevention-flight-timeout")
        public int flightTimeout = 30;
        @Setting("flight-cost-per-block")
        public double pricePerBlock = 0.1;
        @Setting("flight-warmup-time")
        public int flightWarmup = 1;
        @Setting("flight-height")
        public int flightHeight = 15;
        @Setting("flight-speed")
        public double flightSpeed = 0.3;
        @Setting("controlled-flight-speed")
        public double controlledFlightSpeed = 0.3;
        @Setting("dynamic-flight-speed")
        public double dynamicFlightSpeed = 0.7;
        @Setting("controlled-target-distance")
        public int controlledTargetDistance = 30;
        @Setting("dynamic-flight-route")
        public boolean useDynamicRouting = true;
        @Setting("flight-waypoint-distance")
        public int wayPointDistance = 10;
        @Setting("flight-editor-item")
        public int flightEditorItem = 122;
        @Setting("forbidden-commands")
        public String[] forbiddenCommands = new String[]{
                "spawn",
                "home",
                "cast",
                "town spawn",
                "tutorial",
                "tp"
        };

        @Setting("npc-search-radius")
        public int npcStationSearchRadius = 3;
        @Setting("npc-name")
        public String npcDefaultName = "Drachenmeister";
        @Setting("npc-conversation-auto-exit-distance")
        public int autoExitDistance = 10;
        @Setting("npc-conversation-max-stations-per-page")
        public int maxStationPerPage = 5;
        @Setting("npc-conversation-chat-delimiter")
        public String chatDelimiter = " ";
        @Setting("npc-conversation-exit-words")
        public String[] exitWords = new String[]{
                "exit",
                "ende",
                "beenden",
                "stop"
        };

        @Setting("conversation-name")
        public String conversationName = "drachenmeister";

        public LocalDTPConfiguration(DragonTravelPlusPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
