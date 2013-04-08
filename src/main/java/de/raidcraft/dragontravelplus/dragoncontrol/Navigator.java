package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.dragontravelplus.station.StationManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Philip
 */
public class Navigator {

    private Player player;
    private Location start;
    private Location destination;
    private double price;

    private boolean started = false;
    private BukkitTask task;
    private Flight flight = null;
    private List<Location> stationRoute;
    private List<Location> optimizedRoute = new ArrayList<>();

    private int processStage = 0;
    private int processedRouteEntry = 1; //!!! set to 1 - first entry must be ignored

    public Navigator(Player player, Location start, Location destination, double price) {

        this.player = player;
        this.start = start;
        this.destination = destination;
        this.price = price;
    }

    public void startFlight() {

        // start only once
        if(started) return;
        started = true;

        // get the route between the dragon stations
        stationRoute = getStationRoute(StationManager.INST.getAllStationLocations(), start, destination);

        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new Runnable() {
            @Override
            public void run() {

                switch(processStage) {
                    case 0:

                        if(processedRouteEntry >= stationRoute.size()) {
                            processedRouteEntry = 1; //!!! set to 1 - first entry must be ignored
                            processStage++;
                            return;
                        }

                        Location lastLocation = stationRoute.get(processedRouteEntry - 1);
                        Location thisLocation = stationRoute.get(processedRouteEntry);
                        processedRouteEntry++;

                        calculateSectionWayPoints(lastLocation, thisLocation);
                        break;

                    case 1:

                        if(processedRouteEntry >= optimizedRoute.size()) {
                            processStage++;
                            return;
                        }
                        interpolateSection(processedRouteEntry);
                        processedRouteEntry++;

                        break;

                    case 2:

                        flight = new Flight();

                        for(Location wayPoint : optimizedRoute) {
                            flight.addWaypoint(wayPoint);
                        }

                        Location optimizedDestination = destination.clone();
//                        optimizedDestination.setY(optimizedDestination.getY() + 5);
                        flight.addWaypoint(optimizedDestination);

                        takeoff();
                        break;
                }
            }
        }, 0,1);
    }

    /*
     * Split given section into way points and set standard flight height for each of the way points
     */
    private void calculateSectionWayPoints(Location lastLocation, Location thisLocation) {

        World world = lastLocation.getWorld();
        Chunk chunk = lastLocation.getChunk();
        boolean unload = true;
        if(chunk.isLoaded()) {
            unload = false;
        }

        // create unit vector to get the way point direction
        int xDif = thisLocation.getBlockX() - lastLocation.getBlockX();
        int zDif = thisLocation.getBlockZ() - lastLocation.getBlockZ();
        Vector unitVector = new Vector(xDif, 0, zDif).normalize();

        int wayPointCount = (int) lastLocation.distance(thisLocation) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance;
        for (int i = 1; i < wayPointCount; i++) {
            Location wpLocation = lastLocation.clone();
            Vector unitVectorCopy = unitVector.clone();
            wpLocation.add(unitVectorCopy.multiply(i * RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance));
            wpLocation = world.getHighestBlockAt(wpLocation).getLocation();
            if(optimizedRoute.size() > 0) {
                wpLocation.setY(wpLocation.getY() + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightHeight);
            }

            // add way point to route map
            optimizedRoute.add(wpLocation);
        }

        if(unload) {
            chunk.unload();
        }
    }

    private void interpolateSection(int currentIndex) {

    }

    private List<Location> getStationRoute(List<Location> availableLocations, Location start, Location destination) {

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

    private void takeoff() {

        task.cancel(); // if takeoff is called cancel calculation task
        if(flight == null) return;
        FlightTravel.flyFlight(flight, player);
        RaidCraft.getEconomy().withdrawPlayer(player.getName(), price);
        player.sendMessage(ChatColor.GRAY + "Schreibe '" + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords[0] + "' in den Chat um den Flug abzubrechen!");
    }

}
