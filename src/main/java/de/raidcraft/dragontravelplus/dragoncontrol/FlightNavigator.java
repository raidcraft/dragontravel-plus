package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.events.RoutingFinishedEvent;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.Bukkit;
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
    
    public void calculateFlight(FlyingPlayer flyingPlayer, Location start, Location destination) {

        if(start.getWorld() != destination.getWorld()) return;

        Flight flight = new Flight("dragontravelplus_flight");
        flight.addWaypoint(start);

        if(DragonTravelPlusModule.inst.config.useDynamicRouting && !start.getWorld().getName().equalsIgnoreCase("nether")) {
            List<Location> stationRoute = getRoute(StationManager.INST.getAllStationLocations(), start, destination);
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
                if(i == 0) preY = start.getBlockY() + DragonTravelPlusModule.inst.config.flightHeight;
                else preY = optimizedRoute.get(i-1).getBlockY();
                yDiff = location.getBlockY() - preY;
                
                if(yDiff > 20) {
                    boolean isle = false;
                    boolean airType = true;
                    for(int j = optimizedRoute.size(); j > i; j--) {
                        Location currLocation = destination;
                        if(j < optimizedRoute.size()) {
                            currLocation = optimizedRoute.get(j);
                        }

                        if(j > i+15) continue;
                        // look if extreme height was only temporary  (island)
                        if(!isle
                                && (Math.abs(currLocation.getBlockY() - preY) < 50
                                        || currLocation.getBlockY() <= preY
                                        || currLocation.getBlockY() < location.getBlockY()-20)
                                && location.getWorld()
                                    .getBlockAt(location.getBlockX(), preY, location.getBlockZ())
                                    .getType() == Material.AIR) {
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
        
        Location optimizedDestination = destination.clone();
        optimizedDestination.setY(destination.getY() + 2);
        flight.addWaypoint(optimizedDestination);

        Bukkit.getPluginManager().callEvent(new RoutingFinishedEvent(flyingPlayer, flight));
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
                if(location.getWorld() != start.getWorld()) continue;
                
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
