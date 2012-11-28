package de.raidcraft.dragontravelplus;

import org.bukkit.Location;

/**
 * Author: Philip
 * Date: 24.11.12 - 13:34
 * Description:
 */
public class DragonStation {
    private String name;
    private Location location;
    private MapLocation mapLocation;
    private int costLevel = 0;
    private boolean mainStation = false;
    private boolean emergencyTarget = false;

    public DragonStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget) {

        this.name = name;
        this.location = location;
        this.mapLocation = MapLocation.getMapLocation(location);
        this.costLevel = costLevel;
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
    }

    public String getName() {

        return name;
    }

    public Location getLocation() {

        return location;
    }

    public MapLocation getMapLocation() {

        return mapLocation;
    }

    public int getCostLevel() {

        return costLevel;
    }

    public boolean isMainStation() {

        return mainStation;
    }

    public boolean isEmergencyTarget() {

        return emergencyTarget;
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
