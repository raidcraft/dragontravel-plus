package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.dragontravelplus.commands.DTPCommands;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.conversations.*;
import de.raidcraft.dragontravelplus.listener.FlightEditorListener;
import de.raidcraft.dragontravelplus.npc.DragonGuardManager;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TPlayerStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import de.raidcraft.rcconversations.actions.ActionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Philip
 * Date: 22.11.12 - 06:01
 * Description:
 */
public class DragonTravelPlusPlugin extends BasePlugin implements Listener {

    private DTPConfig config;
    private de.raidcraft.dragontravelplus.AircraftManager aircraftManager;
    private de.raidcraft.dragontravelplus.FlightManager flightManager;
    private de.raidcraft.dragontravelplus.RouteManager routeManager;
    private StationManager stationManager;

    @Override
    public void enable() {

        config = configure(new DTPConfig(this));

        // hotfix spawn
        Bukkit.getPluginManager().registerEvents(this, this);

        stationManager = new StationManager(this);
        aircraftManager = new de.raidcraft.dragontravelplus.AircraftManager(this);
        routeManager = new de.raidcraft.dragontravelplus.RouteManager(this);
        flightManager = new de.raidcraft.dragontravelplus.FlightManager(this);

        registerEvents(new FlightEditorListener());

        registerCommands(DTPCommands.class);
        registerCommands(FlightCommands.class);

        // loadConfig NPC's
        DragonGuardManager.spawnAllDragonGuardNPCs(stationManager);

        try {
            ActionManager.registerAction(new FlyFlightAction());
            ActionManager.registerAction(new FlyControlledAction());
            ActionManager.registerAction(new FlyToStationAction());
            ActionManager.registerAction(new ListStationsAction());
            ActionManager.registerAction(new FindDragonstationAction());
            ActionManager.registerAction(new CheckPlayerAction());
        } catch (Exception e) {
            RaidCraft.LOGGER.warning("[DTP] Can't loadConfig Actions! RCConversations not found!");
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
        DragonGuardManager.removeAllDragonGuards();
        DragonGuardManager.spawnAllDragonGuardNPCs(stationManager);
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

    // hotfix for WorldEdit, otherwise Dragons cannot spawn
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) {
            return;
        }
        event.setCancelled(false);
    }
}
