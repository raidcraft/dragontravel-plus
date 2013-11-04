package de.raidcraft.dragontravelplus.station;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.rctravel.api.station.AbstractChargeableStation;
import de.raidcraft.rctravel.api.station.Discoverable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 24.11.12 - 13:34
 * Description:
 */
public class DragonStation extends AbstractChargeableStation implements Discoverable {

    private boolean mainStation = false;
    private boolean emergencyTarget = false;

    public DragonStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget) {

        super(name, location, costLevel);
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
    }

    /*
     * Constructor for dummy stations
     */
    public DragonStation(String name, Location location) {

        this(name, location, 0, false, false);
    }

    public String getFriendlyName() {

        return getName().replace("_", " ");
    }

    public boolean isMainStation() {

        return mainStation;
    }

    public boolean isEmergencyTarget() {

        return emergencyTarget;
    }

    public int getDistance(DragonStation station) {

        return (int)getLocation().distance(station.getLocation());
    }

    @Override
    public boolean hasDiscovered(String player) {

        if(emergencyTarget || mainStation) return true;
        return RaidCraft.getTable(PlayerStationsTable.class).playerIsFamiliar(player, this);
    }

    @Override
    public void setDiscovered(String player, boolean discovered) {

        if(discovered) {
            RaidCraft.getTable(PlayerStationsTable.class).addStation(player, this);
        }
        else {
            // do nothing
        }
    }

    @Override
    @Deprecated
    public void travel(Player player) {

        // dragontravelplus doesn't support this form off takeoff
    }
}
