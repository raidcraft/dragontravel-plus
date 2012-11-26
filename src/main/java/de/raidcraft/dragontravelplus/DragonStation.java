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
    private int costLevel = 0;
    private boolean mainStation = false;
    private boolean emergencyTarget = false;

    public DragonStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget) {

        this.name = name;
        this.location = location;
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

    public int getCostLevel() {

        return costLevel;
    }

    public boolean isMainStation() {

        return mainStation;
    }

    public boolean isEmergencyTarget() {

        return emergencyTarget;
    }
}
