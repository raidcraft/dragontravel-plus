package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.conversations.CheckPlayerAction;
import de.raidcraft.dragontravelplus.conversations.FindDragonstationAction;
import de.raidcraft.dragontravelplus.conversations.FlyControlledAction;
import de.raidcraft.dragontravelplus.conversations.FlyFlightAction;
import de.raidcraft.dragontravelplus.conversations.FlyToStationAction;
import de.raidcraft.dragontravelplus.conversations.ListStationsAction;
import de.raidcraft.dragontravelplus.listener.FlightEditorListener;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TPlayerStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import de.raidcraft.rcconversations.actions.ActionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin {

    private DTPConfig config;
    private de.raidcraft.dragontravelplus.AircraftManager aircraftManager;
    private de.raidcraft.dragontravelplus.FlightManager flightManager;
    private de.raidcraft.dragontravelplus.RouteManager routeManager;
    private StationManager stationManager;

    @Override
    public void enable() {

        config = configure(new DTPConfig(this));

        stationManager = new StationManager(this);
        aircraftManager = new de.raidcraft.dragontravelplus.AircraftManager(this);
        routeManager = new de.raidcraft.dragontravelplus.RouteManager(this);
        flightManager = new de.raidcraft.dragontravelplus.FlightManager(this);

        registerEvents(new FlightEditorListener());

        registerCommands(DTPCommands.class, getName());
        registerCommands(FlightCommands.class, getName());

        // load NPC's
        NPC_Manager.getInstance().loadNPCs(getName());

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

    @Override
    public void disable() {

        FlightManager manager = getFlightManager();
        if (manager != null) {
            for (Flight flight : manager.getActiveFlights()) {
                flight.abortFlight();
            }
        }
        getPluginLoader().disablePlugin(this);
    }

    @Override
    public void reload() {

        getConfig().reload();
        getStationManager().reload();
        getRouteManager().reload();
    }

    public de.raidcraft.dragontravelplus.AircraftManager getAircraftManager() {

        return aircraftManager;
    }

    public de.raidcraft.dragontravelplus.FlightManager getFlightManager() {

        return flightManager;
    }

    public de.raidcraft.dragontravelplus.RouteManager getRouteManager() {

        return routeManager;
    }

    public StationManager getStationManager() {

        return stationManager;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TPath.class);
        tables.add(TPlayerStation.class);
        tables.add(TStation.class);
        tables.add(TWaypoint.class);
        return tables;
    }

    public DTPConfig getConfig() {

        return config;
    }
}
