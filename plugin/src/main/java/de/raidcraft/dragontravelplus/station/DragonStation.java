package de.raidcraft.dragontravelplus.station;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.FlightManager;
import de.raidcraft.dragontravelplus.RouteManager;
import de.raidcraft.dragontravelplus.tables.TPlayerStation;
import de.raidcraft.dragontravelplus.tables.TStation;
import de.raidcraft.rctravel.api.station.AbstractStation;
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Discoverable;
import de.raidcraft.rctravel.api.station.Station;
import io.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Author: Philip
 * Date: 24.11.12 - 13:34
 * Description:
 */
public class DragonStation extends AbstractStation implements Chargeable, Discoverable {

    private boolean mainStation = false;
    private boolean emergencyTarget = false;
    private double costMultiplier;

    public DragonStation(String name, String displayName, Location location, double costMultiplier, boolean mainStation, boolean emergencyTarget) {

        super(name, location);
        setDisplayName(displayName);
        this.mainStation = mainStation;
        this.emergencyTarget = emergencyTarget;
        this.costMultiplier = costMultiplier;
    }

    /*
     * Constructor for dummy stations
     */
    public DragonStation(String name, Location location) {

        super(name, location);
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
    public boolean hasDiscovered(UUID player) {

        if (emergencyTarget || mainStation) {
            return true;
        }

        List< TPlayerStation > stations = RaidCraft.getDatabase(DragonTravelPlusPlugin.class)
                .find(TPlayerStation.class).where().eq("player_id", player).isNotNull("discovered").findList();
        for (TPlayerStation station : stations) {
            if (station.getStation().getName().equalsIgnoreCase(getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setDiscovered(UUID player, boolean discovered) {

        if (discovered) {

            EbeanServer database = RaidCraft.getDatabase(DragonTravelPlusPlugin.class);
            TStation station = save();
            TPlayerStation playerStation = database.find(TPlayerStation.class)
                    .where().eq("station_id", station.getId()).eq("player_id", player).findOne();
            if (playerStation == null) {
                playerStation = new TPlayerStation();
                playerStation.setPlayerId(player);
                if(Bukkit.getPlayer(player) != null) {
                    playerStation.setPlayer(Bukkit.getPlayer(player).getName());
                }
                playerStation.setStation(station);
            }
            if (playerStation.getDiscovered() == null) {
                playerStation.setDiscovered(new Timestamp(System.currentTimeMillis()));
            }
            database.save(playerStation);
        }
    }

    @Override
    public void travelFrom(Player player, Station sourceStation) {
        RaidCraft.getComponent(RouteManager.class)
                .getRoute(sourceStation, this)
                .createFlight(RaidCraft.getComponent(FlightManager.class).getPassenger(player)).startFlight();
    }

    @Override
    public void travelTo(Player player, Station targetStation) {
        RaidCraft.getComponent(RouteManager.class)
                .getRoute(this, targetStation)
                .createFlight(RaidCraft.getComponent(FlightManager.class).getPassenger(player)).startFlight();
    }

    @Override
    public void travelTo(Player player) {
        // TODO:
    }

    @Override
    public void travel(Player player, Location from, Location to) {

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
        TStation station = database.find(TStation.class).where().eq("name", getName()).findOne();
        if (station == null) {
            station = new TStation();
        }
        station.setName(getName());
        station.setDisplayName(getDisplayName());
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
