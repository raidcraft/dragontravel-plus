package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.station.DragonStation;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.Location;
import org.bukkit.Material;
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
        flight.addWaypoint(start.getLocation());

        Location startWPLocation = null;
        for(Location targetWPLocation : route) {
            if(startWPLocation != null) {
                // create unit vector
                int xDif = targetWPLocation.getBlockX() - startWPLocation.getBlockX();
                int zDif = targetWPLocation.getBlockZ() - startWPLocation.getBlockZ();
                Vector unitVector = new Vector(xDif, 0, zDif).normalize();

                int wayPointCount = (int) startWPLocation.distance(targetWPLocation) / DragonTravelPlusModule.inst.config.wayPointDistance;
                double lastFlightHeight = -1;
                for(int i = 1; i < wayPointCount; i++) {
                    Location wpLocation = startWPLocation.clone();
                    Vector unitVectorCopy = unitVector.clone();
                    wpLocation.add(unitVectorCopy.multiply(i * DragonTravelPlusModule.inst.config.wayPointDistance));
                    wpLocation = startWPLocation.getWorld().getHighestBlockAt(wpLocation).getLocation();
                    
                    double flightHeight = wpLocation.getY() + DragonTravelPlusModule.inst.config.flightHeight;
                    if(lastFlightHeight > 0) {
                        double flightHeightDiff = Math.abs(flightHeight - lastFlightHeight);
                        if(flightHeightDiff > 5) {
                            // descent
                            if(flightHeight < lastFlightHeight) {
                                if((lastFlightHeight - flightHeightDiff / 2) > 5) {
                                    flightHeight += 5;
                                }
                                else {
                                    flightHeight += lastFlightHeight - flightHeightDiff / 2;
                                }
                            }
                            // climb
                            else {
                                double propFlightHeight;
                                if(flightHeight > lastFlightHeight) {
                                    propFlightHeight = flightHeight - (flightHeightDiff / 2.);
                                }
                                else {
                                    propFlightHeight = flightHeight + (flightHeightDiff / 2.);
                                }

                                if(wpLocation.getBlock().getWorld()
                                        .getBlockAt(wpLocation.getBlockX(), (int)propFlightHeight, wpLocation.getBlockZ()).getType() == Material.AIR) {
                                    flightHeight = propFlightHeight;
                                }
                            }
                        }
                    }
                    lastFlightHeight = flightHeight;
                    wpLocation.setY(flightHeight);

                    flight.addWaypoint(wpLocation);
                }
            }
            startWPLocation = targetWPLocation;
        }
        
        Location optimizedDestination = destination.getLocation().clone();
        optimizedDestination.setY(destination.getLocation().getY() - 5);
        flight.addWaypoint(optimizedDestination);
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
