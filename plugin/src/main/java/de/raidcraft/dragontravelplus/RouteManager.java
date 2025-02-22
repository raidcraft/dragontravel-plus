package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.dragontravelplus.paths.*;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Silthus
 */
public final class RouteManager implements Component {

    private final DragonTravelPlusPlugin plugin;
    private final Map<String, Path> loadedPaths = new CaseInsensitiveMap<>();
    private final Map<Station, Map<Station, DragonStationRoute>> loadedRoutes = new HashMap<>();

    protected RouteManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(RouteManager.class, this);
        load();
    }

    public void reload() {

        loadedPaths.clear();
        loadedRoutes.clear();
        load();
    }

    private void load() {

        int pathAmount = 0;
        int routeAmount = 0;
        List<TPath> paths = plugin.getRcDatabase().find(TPath.class).findList();
        for (TPath path : paths) {
            SavedFlightPath flightPath;
            if (path.getStartStation() != null && path.getEndStation() != null) {
                Optional<Station> startStation = RaidCraft.getComponent(StationManager.class).getStation(path.getStartStation().getName());
                Optional<Station> endStation = RaidCraft.getComponent(StationManager.class).getStation(path.getEndStation().getName());
                flightPath = new SavedFlightPath(startStation.get().getLocation(), endStation.get().getLocation(), path);
                // this path has an end and startStage station so lets loadConfig the route while we are at it
                DragonStationRoute stationRoute = new DragonStationRoute(startStation.get(), endStation.get(), flightPath);
                addDragonStationRoute(stationRoute);
                routeAmount++;
            } else if (path.getWaypoints().size() > 1) {
                Location startLoc = new SavedWaypoint(path.getWaypoints().get(0)).getLocation();
                Location endLoc = new SavedWaypoint(path.getWaypoints().get(path.getWaypoints().size() - 1)).getLocation();
                flightPath = new SavedFlightPath(startLoc, endLoc, path);
            } else {
                plugin.getLogger().warning("Invalid path in the database found: " + path.getName() + "[" + path.getId() + "]!");
                continue;
            }
            loadedPaths.put(path.getName(), flightPath);
            pathAmount++;
        }
        plugin.getLogger().info("Loaded " + pathAmount + "/" + paths.size() + " flight paths from the database...");
        plugin.getLogger().info("Loaded " + routeAmount + "/" + paths.size() + " precached routes from the database...");
    }

    private void addDragonStationRoute(DragonStationRoute route) {

        if (!loadedRoutes.containsKey(route.getStartStation())) {
            loadedRoutes.put(route.getStartStation(), new HashMap<Station, DragonStationRoute>());
        }
        loadedRoutes.get(route.getStartStation()).put(route.getEndStation(), route);
    }

    // TODO: calculate and cache routes based on locations not stations

    public DragonStationRoute getRoute(Station start, Station destination) {

        if (loadedRoutes.containsKey(start) && loadedRoutes.get(start).containsKey(destination)) {
            return loadedRoutes.get(start).get(destination);
        }
        // generate a flight path
        Path path;
        if (plugin.getConfig().useCitizensPathFinding
                && AircraftManager.AircraftType.fromString(plugin.getConfig().aircraftType) == AircraftManager.AircraftType.CITIZENS) {
            path = new StaticFlightPath(start.getLocation(), destination.getLocation());
        } else if (plugin.getConfig().useDynamicRouting) {
            path = new DynamicFlightPath(start.getLocation(), destination.getLocation());
        } else {
            path = new StaticFlightPath(start.getLocation(), destination.getLocation());
        }
        // TODO: maybe we need to trigger a repeating task to calculate this if it laggs the server
        path.calculate();
        // and create a new dragon station route
        addDragonStationRoute(new DragonStationRoute(start, destination, path));
        return getRoute(start, destination);
    }

    public Optional<Path> getPath(String name) {

        return Optional.ofNullable(loadedPaths.get(name));
    }

    public void savePath(Path path, String name) {

        if (loadedPaths.containsKey(name)) {
            return;
        }
        TPath tPath = new TPath();
        tPath.setName(name);
        tPath.save();
        for (int i = 0; i < path.getWaypointAmount(); i++) {
            TWaypoint tWaypoint = new TWaypoint();
            tWaypoint.setPath(tPath);
            Waypoint waypoint = path.getWaypoint(i);
            tWaypoint.setWaypointIndex(i);
            tWaypoint.setWorld(waypoint.getWorld().getName());
            tWaypoint.setX(waypoint.getX());
            tWaypoint.setY(waypoint.getY());
            tWaypoint.setZ(waypoint.getZ());
            tWaypoint.save();
        }
    }

    public void deletePath(String name) {

        TPath tPath = TPath.find.query().where().eq("name", name).findOne();
        if (tPath != null) {
            tPath.delete();
        }
    }
}