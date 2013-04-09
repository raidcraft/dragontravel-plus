package de.raidcraft.dragontravelplus.flight;

import de.raidcraft.api.database.Database;
import de.raidcraft.dragontravelplus.tables.FlightWayPointsTable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flight {

    HashMap<Integer, WayPoint> waypoints = new HashMap<Integer, WayPoint>();
    int currentwp = 0;
    public int wpcreatenum = 0;
    String name;

    public static void removeFlight(String flightName) {
        Database.getTable(FlightWayPointsTable.class).deleteFlight(flightName);
    }

    public static Flight loadFlight(String flightName) {
        return Database.getTable(FlightWayPointsTable.class).getFlight(flightName);
    }

    /**
     * Flight object, containing a flight-name and waypoints
     *
     * @param name
     */
    public Flight(String name) {

        this.name = name;
    }

    public Flight() {

        this.name = "Flight";
    }

    /**
     * Adds a waypoint to the db as a key/keyvalue
     *
     * @param wp
     */
    public void addWaypoint(WayPoint wp) {

        waypoints.put(wpcreatenum, wp);
        wpcreatenum++;
    }

    public void addWaypoint(Location location) {

        addWaypoint(new WayPoint(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    public void addFlight(Flight flight) {

        for(WayPoint wayPoint : flight.getWayPoints()) {
            addWaypoint(wayPoint.clone());
        }
    }

    /**
     * Removes the last waypoint
     */
    public WayPoint removeWaypoint() {

        if (wpcreatenum == 0)
            return null;
        wpcreatenum--;
        return waypoints.remove(wpcreatenum);
    }

    public WayPoint getFirstWaypointCopy() {

        return waypoints.get(0);
    }

    /**
     * Gets the firstwaypoint
     *
     * @return
     */
    public WayPoint getFirstWaypoint() {

        WayPoint wp = waypoints.get(currentwp);
        currentwp++;
        return wp;
    }

    /**
     * Gets the next waypoint for this flight
     */
    public WayPoint getNextWaypoint() {

        WayPoint wp = waypoints.get(currentwp);
        currentwp++;
        if (wp == null) {
            return null;
        }
        return wp;
    }

    public String getName() {

        return name;
    }

    public int size() {
        return waypoints.size();
    }

    public List<WayPoint> getWayPoints() {

        List<WayPoint> wayPointList = new ArrayList<>();
        for(Map.Entry<Integer, WayPoint> entry : waypoints.entrySet()) {
            wayPointList.add(entry.getValue());
        }
        return wayPointList;
    }

    public String getFlightWorld() {
        return waypoints.get(0).getWorld();
    }

    public void save(String creator) {
        Database.getTable(FlightWayPointsTable.class).addFlight(this, creator);

        removeMarkers();
    }

    public void removeMarkers() {

        for(Map.Entry<Integer, WayPoint> entry : waypoints.entrySet()) {
            Block block = entry.getValue().getLocation().getBlock();
            if(block.getType() == FlightEditorListener.MARKER_MATERIAL) {
                block.setType(Material.AIR);
            }
        }
    }
}
