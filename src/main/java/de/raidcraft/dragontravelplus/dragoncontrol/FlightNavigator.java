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
        flight.addWaypoint(start.getLocation());

        if(DragonTravelPlusModule.inst.config.useDynamicRouting) {
            List<Location> stationRoute = getRoute(StationManager.INST.getAllStationLocations(), start.getLocation(), destination.getLocation());
            List<Location> optimizedRoute = new ArrayList<>();

            // get all waypoints (without interpolation)
            Location startWPLocation = null;
            for(Location targetWPLocation : stationRoute) {
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
                        wpLocation.setY(wpLocation.getY() + DragonTravelPlusModule.inst.config.flightHeight);

//                        double flightHeight = wpLocation.getY() + DragonTravelPlusModule.inst.config.flightHeight;
//                        if(lastFlightHeight > -1) {
//                            double flightHeightDiff = Math.abs(flightHeight - lastFlightHeight);
//
//                            if(flightHeightDiff > 5) {
//                                // descent
//                                if(flightHeight < lastFlightHeight) {
//                                    if((lastFlightHeight - flightHeightDiff / 2) > 15) {
//                                        flightHeight = lastFlightHeight - 15;
//                                    }
//                                    else {
//                                        flightHeight += flightHeightDiff / 2;
//                                    }
//                                }
//                                // climb
//                                else {
//                                    double propFlightHeight;
//                                    propFlightHeight = flightHeight - (flightHeightDiff / 2);
//
//                                    if(wpLocation.getBlock().getWorld()
//                                            .getBlockAt(wpLocation.getBlockX(), (int)propFlightHeight, wpLocation.getBlockZ()).getType() == Material.AIR) {
//                                        flightHeight = propFlightHeight;
//                                    }
//                                }
//                            }
//                        }
//                        lastFlightHeight = flightHeight;
//                        if(flightHeight > start.getLocation().getWorld().getMaxHeight()) {
//                            flightHeight = start.getLocation().getWorld().getMaxHeight();
//                        }
//                        if(flightHeight < 0) {
//                            flightHeight = 0;
//                        }
//                        wpLocation.setY(flightHeight);

                        optimizedRoute.add(wpLocation);
                    }
                }
                startWPLocation = targetWPLocation;
            }

            // interpolation variables
            int yDiff;
            int i = 0;
            
            // interpolate sky islands
            boolean skyIsle = false;
            for(Location location : optimizedRoute) {
                int preY;
                if(i == 0) preY = start.getLocation().getBlockY() + DragonTravelPlusModule.inst.config.flightHeight;
                else preY = optimizedRoute.get(i-1).getBlockY();
                yDiff = location.getBlockY() - preY;
                
                if(yDiff > 20) {
                    boolean isle = false;
                    boolean airType = true;
                    for(int j = optimizedRoute.size(); j > i; j--) {
                        Location currLocation = destination.getLocation();
                        if(j < optimizedRoute.size()) {
                            currLocation = optimizedRoute.get(j);
                        }

                        if(j > i+15) continue;
                        // look if extreme height was only temporary  (island)
                        if(!isle && (Math.abs(currLocation.getBlockY() - preY) < 10 || currLocation.getBlockY() <= preY)) {
                            isle = true;
                        }
                        // if height was temporary, inspect previous waypoints if we can reduce height
                        else if(isle) {
                            if(location.getWorld()
                                    .getBlockAt(currLocation.getBlockX(), preY, currLocation.getBlockZ())
                                    .getType() != Material.AIR) {
                                airType = false;
                            }
                        }
                    }
                    // set new height
                    if(airType && isle) {
                        skyIsle = true;
                        location.setY(preY);
                    }
                }

                if(!skyIsle) {
                    // interpolate climb
                    if(i > 1 && yDiff > 10) {

                        int prepreY = optimizedRoute.get(i-2).getBlockY();
                        int newpreY = preY;
                        // calculate average flight height
                        if(prepreY > location.getY()) {
                            newpreY = location.getBlockY() + Math.abs((prepreY - location.getBlockY()) / 2);
                        }
                        else {
                            newpreY = location.getBlockY() - Math.abs((prepreY - location.getBlockY()) / 2);
                        }
                        optimizedRoute.get(i-1).setY(newpreY);
                    }

                    // interpolate descent
                    if(i > 1 && yDiff < -15) {
                        location.setY(preY - 15);
                    }
                }
                skyIsle = false;
                i++;
            }

            // add all waypoint locations to flight
            for(Location location : optimizedRoute) {
                flight.addWaypoint(location);
            }
        }
        
        Location optimizedDestination = destination.getLocation().clone();
        optimizedDestination.setY(destination.getLocation().getY() - 8);
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
