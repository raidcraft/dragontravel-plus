package de.raidcraft.dragontravelplus.station;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.tables.TPlayerStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.rctravel.api.station.AbstractStation;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;

/**
 * Author: Philip
 * Date: 24.11.12 - 13:34
 * Description:
 */
public class DragonStation extends AbstractStation implements Chargeable, Discoverable {

    private boolean mainStation = false;
    private boolean emergencyTarget = false;
    private double costMultiplier;

    public DragonStation(String name, Location location, double costMultiplier, boolean mainStation, boolean emergencyTarget) {

        super(name, location);
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
        this.costMultiplier = costMultiplier;
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

        return (int) getLocation().distance(station.getLocation());
    }

    @Override
    public boolean hasDiscovered(String player) {

        if (emergencyTarget || mainStation) {
            return true;
        }
        List<TPlayerStation> stations = RaidCraft.getDatabase(DragonTravelPlusPlugin.class)
                .find(TPlayerStation.class).where().eq("player", player).isNotNull("discovered").findList();
        for (TPlayerStation station : stations) {
            if (station.getStation().getName().equals(getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDiscovered(String player, boolean discovered) {

        if (discovered) {

            EbeanServer database = RaidCraft.getDatabase(DragonTravelPlusPlugin.class);
            TStation station = save();
            TPlayerStation playerStation = database.find(TPlayerStation.class)
                    .where().eq("station_id", station.getId()).eq("player", player).findUnique();
            if (playerStation == null) {
                playerStation = new TPlayerStation();
                playerStation.setPlayer(player);
                playerStation.setStation(station);
            }
            if (playerStation.getDiscovered() == null) {
                playerStation.setDiscovered(new Timestamp(System.currentTimeMillis()));
            }
            database.save(playerStation);
        }
    }

    @Override
    public void travel(Player player, Station targetStation) {

        // dragontravelplus doesn't support this form off takeoff
    }

    @Override
    public double getPrice() {

        return costMultiplier;
    }

    @Override
    public double getPrice(int distance) {

        return Math.abs(distance * RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().pricePerBlock * 100D) / 100D;
    }

    public double getPrice(DragonStation destination) {

        if (destination.isEmergencyTarget() || (getPrice() == 0 && destination.getPrice() == 0)) {
            return 0;
        }

        int costLevel;
        if (destination.getPrice() == 0) {
            costLevel = (int) getPrice();
        } else {
            costLevel = (int) destination.getPrice();
        }

        return Math.abs(Math.round(costLevel * getLocation().distance(destination.getLocation())
                * RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().pricePerBlock * 100.) / 100.);
    }

    public TStation save() {

        EbeanServer database = RaidCraft.getDatabase(DragonTravelPlusPlugin.class);
        TStation station = database.find(TStation.class).where().eq("name", getName()).findUnique();
        if (station == null) {
            station = new TStation();
        }
        station.setName(getName());
        station.setDisplayName(getFriendlyName());
        station.setWorld(getLocation().getWorld().getName());
        station.setX(getLocation().getBlockX());
        station.setY(getLocation().getBlockY());
        station.setZ(getLocation().getBlockZ());
        station.setMainStation(isMainStation());
        station.setEmergencyStation(isEmergencyTarget());
        database.save(station);
        return station;
    }
}
