package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.conversations.CheckPlayerAction;
import de.raidcraft.dragontravelplus.conversations.FindDragonstationAction;
import de.raidcraft.dragontravelplus.conversations.FlyControlledAction;
import de.raidcraft.dragontravelplus.conversations.FlyFlightAction;
import de.raidcraft.dragontravelplus.conversations.FlyToStationAction;
import de.raidcraft.dragontravelplus.conversations.ListStationsAction;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.dragontravelplus.tables.StationTable;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TPlayerStations;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import de.raidcraft.rcconversations.actions.ActionManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin {

    private LocalDTPConfiguration config;
    private AircraftManager aircraftManager;
    private FlightManager flightManager;
    private RouteManager routeManager;
    private StationManager stationManager;

    @Override
    public void enable() {

        config = configure(new LocalDTPConfiguration(this));

        registerTable(StationTable.class, new StationTable());
        registerTable(PlayerStationsTable.class, new PlayerStationsTable());
        registerTable(FlightWayPointsTable.class, new FlightWayPointsTable());

        stationManager = new StationManager(this);
        aircraftManager = new AircraftManager(this);
        routeManager = new RouteManager(this);
        flightManager = new FlightManager(this);

        registerCommands(DTPCommands.class);
        registerCommands(FlightCommands.class);

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

        for (Flight flight : getFlightManager().getActiveFlights()) {
            try {
                flight.abortFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void reload() {

        getStationManager().reload();
        getRouteManager().reload();
    }

    public AircraftManager getAircraftManager() {

        return aircraftManager;
    }

    public FlightManager getFlightManager() {

        return flightManager;
    }

    public RouteManager getRouteManager() {

        return routeManager;
    }

    public StationManager getStationManager() {

        return stationManager;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TPath.class);
        tables.add(TPlayerStations.class);
        tables.add(TStation.class);
        tables.add(TWaypoint.class);
        return tables;
    }

    public LocalDTPConfiguration getConfig() {

        return config;
    }

    public class LocalDTPConfiguration extends ConfigurationBase<DragonTravelPlusPlugin> {

        @Setting("disabled")
        public boolean disabled = false;
        @Setting("aircraft.type")
        public String aircraftType = "REMOTE_ENTITY";
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
        @Setting("flight.waypoint-radius")
        public int wayPointRadius = 4;
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
