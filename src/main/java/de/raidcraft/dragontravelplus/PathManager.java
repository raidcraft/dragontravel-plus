package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.dragontravelplus.paths.SavedFlightPath;
import de.raidcraft.dragontravelplus.paths.SavedWaypoint;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.rctravel.StationManager;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class PathManager implements Component {

    private final DragonTravelPlusPlugin plugin;
    private final Map<String, Path> loadedPaths = new HashMap<>();

    protected PathManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(PathManager.class, this);
        load();
    }

    public void reload() {

        loadedPaths.clear();
        load();
    }

    private void load() {

        int amount = 0;
        List<TPath> paths = plugin.getDatabase().find(TPath.class).findList();
        for (TPath path : paths) {
            Location startLoc;
            Location endLoc;
            if (path.getStartStation() != null && path.getEndStation() != null) {
                Station startStation = RaidCraft.getComponent(StationManager.class).getStation(path.getStartStation().getName());
                Station endStation = RaidCraft.getComponent(StationManager.class).getStation(path.getEndStation().getName());
                startLoc = startStation.getLocation();
                endLoc = endStation.getLocation();
            } else if (path.getWaypoints().size() > 1) {
                startLoc = new SavedWaypoint(path.getWaypoints().get(0)).getLocation();
                endLoc = new SavedWaypoint(path.getWaypoints().get(path.getWaypoints().size() - 1)).getLocation();
            } else {
                plugin.getLogger().warning("Invalid path in the database found: " + path.getName() + "[" + path.getId() + "]!");
                continue;
            }
            SavedFlightPath flightPath = new SavedFlightPath(startLoc, endLoc);
            loadedPaths.put(path.getName(), flightPath);
            amount++;
        }
        plugin.getLogger().info("Loaded " + amount + "/" + paths.size() + " flight paths from the database...");
    }
}
