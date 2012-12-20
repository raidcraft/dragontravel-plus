package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.Bukkit;
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
    
    public Flight getFlight(DragonStation start, DragonStation destination) {
        Flight flight = new Flight(start.getName() + "_" + destination.getName());

        List<Location> route = getRoute(StationManager.INST.getAllStationLocations(), start.getLocation(), destination.getLocation());
        Bukkit.broadcastMessage("Route contains: " + route.size() + " Stations!");
        flight.addWaypoint(start.getLocation());

        Location startWPLocation = null;
        for(Location targetWPLocation : route) {
            if(startWPLocation != null) {
                // create unit vector
                int xDif = targetWPLocation.getBlockX() - startWPLocation.getBlockX();
                int zDif = targetWPLocation.getBlockZ() - startWPLocation.getBlockZ();
                Vector unitVector = new Vector(xDif, 0, zDif).normalize();

                int wayPointCount = (int) startWPLocation.distance(targetWPLocation) / DragonTravelPlusModule.inst.config.wayPointDistance;

                for(int i = 1; i < wayPointCount; i++) {
                    Location wpLocation = startWPLocation.clone();
                    Vector unitVectorCopy = unitVector.clone();
                    wpLocation.add(unitVectorCopy.multiply(i * DragonTravelPlusModule.inst.config.wayPointDistance));
                    wpLocation = startWPLocation.getWorld().getHighestBlockAt(wpLocation).getLocation();
                    wpLocation.setY(wpLocation.getY() + DragonTravelPlusModule.inst.config.flightHeight);

                    flight.addWaypoint(wpLocation);
                }
            }
            startWPLocation = targetWPLocation;
        }

        flight.addWaypoint(destination.getLocation());
        return flight;
    }

    private List<Location> getRoute(List<Location> availableLocations, Location start, Location destination) {

        SortedMap<Integer, Location> ratedLocations = new TreeMap<>();
        List<Location> route = new ArrayList<>();
        Location nextStarpoint = start;
        route.add(nextStarpoint);
        availableLocations.remove(nextStarpoint);
        
        while(nextStarpoint != destination) {
            ratedLocations.clear();
            
            for(Location location : availableLocations) {
                
                Integer rating = new Integer((int) (2 * Math.sqrt(Math.pow(nextStarpoint.distance(location), 2) + Math.pow(location.distance(destination), 2))));
                if(nextStarpoint.distance(location) < location.distance(destination)) {
                    rating += 1000;
                }
                
                ratedLocations.put(rating, location);
            }
            nextStarpoint = ratedLocations.get(ratedLocations.firstKey());
            availableLocations.remove(nextStarpoint);
            route.add(nextStarpoint);
        }
        
        return route;
    }
}
