package de.raidcraft.dragontravelplus.station;

import de.raidcraft.util.DateUtil;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
    private String creator;
    private String created;

    public DragonStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget, String creator, String created) {

        this.name = name;
        this.location = location;
        this.costLevel = costLevel;
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
        this.creator = creator;
        this.created = created;
    }

    /*
     * Constructor for dummy stations
     */
    public DragonStation(String name, Location location) {

        this(name, location, 0, false, false, "Dummy", DateUtil.getCurrentDateString());
    }

    public String getName() {

        return name;
    }

    public String getFriendlyName() {

        return name.replace("_", " ");
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

    public String getCreator() {

        return creator;
    }

    public String getCreated() {

        return created;
    }

    public int getDistance(DragonStation station) {

        return (int)getLocation().distance(station.getLocation());
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(name).append(isMainStation()).append(costLevel).append(isEmergencyTarget()).toHashCode();
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DragonStation) {

            if(((DragonStation)obj).getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
