package de.raidcraft.dragontravelplus;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Philip
 * Date: 25.11.12 - 13:57
 * Description:
 */
public class StationManager {
    public static final StationManager INST = new StationManager();

    private Map<MapLocation, List<DragonStation>> registeredStations = new HashMap<>();
    
    public void addStation(String name, Location location, int costLevel) {
        DragonStation station = new DragonStation(name, location.clone(), costLevel);

        List<DragonStation> existingStations = registeredStations.get(MapLocation.getMapLocation(location));
        
        if(existingStations == null) {
            existingStations = new ArrayList<>();
        }
        existingStations.add(station);
        registeredStations.put(MapLocation.getMapLocation(location), existingStations);
    }
    
    public List<DragonStation> getDragonStations(MapLocation mapLocation) {
        return registeredStations.get(mapLocation);
    }

    public enum MapLocation {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST;
        
        public static MapLocation getMapLocation(Location location) {
            if(location.getBlockX() >= 0 && location.getZ() >= 0) {
                return SOUTH_EAST;
            }
            if(location.getBlockX() <= 0 && location.getZ() >= 0) {
                return SOUTH_WEST;
            }
            if(location.getBlockX() <= 0 && location.getZ() <= 0) {
                return NORTH_WEST;
            }
            return NORTH_EAST;
        }
    }
}
