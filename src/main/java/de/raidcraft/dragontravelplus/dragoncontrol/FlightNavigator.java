package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Author: Philip
 * Date: 17.12.12 - 21:56
 * Description:
 */
public class FlightNavigator {

    public final static FlightNavigator INST = new FlightNavigator();

    public void calculateFlight(FlyingPlayer flyingPlayer, Location start, Location destination) {

        if (start.getWorld() != destination.getWorld()) return;

        List<Location> stationRoute = getRoute(StationManager.INST.getAllStationLocations(), start, destination);
        List<Location> checkpointRoute = new ArrayList<>();

        // get all way points (without interpolation)
        Location startWPLocation = null;
        for (Location targetWPLocation : stationRoute) {
            if (startWPLocation != null) {
                // create unit vector
                int xDif = targetWPLocation.getBlockX() - startWPLocation.getBlockX();
                int yDif = targetWPLocation.getBlockY() - startWPLocation.getBlockY();
                int zDif = targetWPLocation.getBlockZ() - startWPLocation.getBlockZ();
                Vector unitVector = new Vector(xDif, yDif, zDif).normalize();

                int wayPointCount = (int) startWPLocation.distance(targetWPLocation) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance;
                for (int i = 1; i < wayPointCount; i++) {
                    Location wpLocation = startWPLocation.clone();
                    Vector unitVectorCopy = unitVector.clone();
                    wpLocation.add(unitVectorCopy.multiply(i * RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance));

                    checkpointRoute.add(wpLocation);
                }
            }
            // first checkpoint
            else {
                checkpointRoute.add(targetWPLocation);
            }
            startWPLocation = targetWPLocation;
        }


        Location optimizedDestination = destination.clone();
        optimizedDestination.setY(destination.getY() - 5);
        checkpointRoute.add(optimizedDestination);

        // optimize
        optimizeCheckpoint(1, checkpointRoute);
        optimizeCheckpoint(2, checkpointRoute);

        DragonManager.INST.calculationFinished(flyingPlayer, checkpointRoute);
    }

    private List<Location> getRoute(List<Location> availableLocations, Location start, Location destination) {

        SortedMap<Integer, Location> ratedLocations = new TreeMap<>();
        List<Location> route = new ArrayList<>();
        Location nextStarPoint = start;
        route.add(nextStarPoint);
        availableLocations.remove(nextStarPoint);

        while (nextStarPoint != destination) {
            ratedLocations.clear();

            for (Location location : availableLocations) {
                if (location.getWorld() != start.getWorld()) continue;

                Integer rating = new Integer((int) (2 * Math.sqrt(Math.pow(nextStarPoint.distance(location), 2) + Math.pow(location.distance(destination), 2))));
                if (nextStarPoint.distance(location) < location.distance(destination)) {
                    rating += 1000;
                }

                ratedLocations.put(rating, location);
            }
            nextStarPoint = ratedLocations.get(ratedLocations.firstKey());
            availableLocations.remove(nextStarPoint);
            route.add(nextStarPoint);
        }

        return route;
    }

    public void optimizeCheckpoint(int wayPointIndex, List<Location> route) {

        if(wayPointIndex == 0) {
            return;
        }
        if(wayPointIndex >= route.size() - 1) {
            return;
        }

        Location start = route.get(0);
        Location location = route.get(wayPointIndex);

        // interpolation variables
        int yDiff;

        // first step take highest block + flight height for way point
        location.setY(start.getWorld().getHighestBlockAt(location).getY() +
                RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightHeight);

        // get previous way point height
        int preY = route.get(wayPointIndex - 1).getBlockY();

        // get height difference between previous and current way point
        yDiff = location.getBlockY() - preY;

        // interpolate climb
        if (wayPointIndex > 1 && yDiff > 10) {

            int prepreY = route.get(wayPointIndex - 2).getBlockY();
            int newpreY = preY;
            // calculate average flight height
            if (prepreY > location.getY()) {
                newpreY = location.getBlockY() + Math.abs((prepreY - location.getBlockY()) / 2);
            } else {
                newpreY = location.getBlockY() - Math.abs((prepreY - location.getBlockY()) / 2);
            }
            route.get(wayPointIndex - 1).setY(newpreY);
        }

        // interpolate descent
        if (wayPointIndex > 1 && yDiff < -15) {
            location.setY(preY - 15);
        }
    }
}
