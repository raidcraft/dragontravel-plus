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
    private int costLevel;

    public DragonStation(String name, Location location, int costLevel) {

        this.name = name;
        this.location = location;
        this.costLevel = costLevel;
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
}
