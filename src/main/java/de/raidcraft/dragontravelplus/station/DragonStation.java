package de.raidcraft.dragontravelplus.station;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.commands.FlightCommands;
import de.raidcraft.dragontravelplus.tables.PlayerStationsTable;
import de.raidcraft.dragontravelplus.util.FlightCosts;
import de.raidcraft.rctravel.api.station.AbstractStation;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Author: Philip
 * Date: 24.11.12 - 13:34
 * Description:
 */
public class DragonStation extends AbstractStation implements Chargeable, Discoverable {

    private boolean mainStation = false;
    private boolean emergencyTarget = false;
    private int costLevel;

    public DragonStation(String name, Location location, int costLevel, boolean mainStation, boolean emergencyTarget) {

        super(name, location);
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
        this.costLevel = costLevel;
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
    public void travel(Player player, Station targetStation) {

        // dragontravelplus doesn't support this form off takeoff
    }

    @Override
    public double getPrice() {

        return 1;
    }

    @Override
    public double getPrice(int distance) {

        //TODO
    }
}
