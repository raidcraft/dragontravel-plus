package de.raidcraft.dragontravelplus.navigator;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.FlightTravel;
import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.dragontravelplus.station.DragonStation;
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
    private DragonStation start;
    private DragonStation destination;
    private double price;

    private boolean started = false;
    private BukkitTask task;
    private Flight flight = null;
    private List<RouteSection> stationRoute;

    private int processStage = 0;
    private int processedSection = 0;
    private int processedRouteEntry = 0;

    public Navigator(Player player, DragonStation start, DragonStation destination, double price) {

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
        stationRoute = getStationRoute();

        task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new Runnable() {
            @Override
            public void run() {

                RouteSection section;
                switch(processStage) {
                    case 0:

                        if(processedSection >= stationRoute.size()) {
                            processStage++;
                            processedSection = 0;
                            return;
                        }

                        section = stationRoute.get(processedSection);
                        if(processedRouteEntry == 0) {
                            // skip if route already load from db
                            if(section.getFlight().size() == 0) {
                                processedSection++;
                                return;
                            }
                        }

                        calculateSectionWayPoints(section);
                        processedSection++;
                        break;

                    case 1:

                        if(processedSection >= stationRoute.size()) {
                            processStage++;
                            processedSection = 0;
                            return;
                        }

                        section = stationRoute.get(processedSection);
                        if(processedRouteEntry >= section.getFlight().size()) {
                            processedSection++;
                            return;
                        }

                        interpolateSection(section, processedRouteEntry);
                        processedRouteEntry++;
                        break;

                    case 2:

                        flight = new Flight();

                        for(RouteSection routeSection : stationRoute) {
                            flight.addFlight(routeSection.getFlight());
                        }

                        Location optimizedDestination = destination.getLocation().clone();
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
    private void calculateSectionWayPoints(RouteSection section) {

        Location sectionStart = section.getStart().getLocation();
        Location sectionDestination = section.getDestination().getLocation();

        World world = sectionStart.getWorld();
        Chunk chunk = sectionStart.getChunk();
        boolean unload = true;
        if(chunk.isLoaded()) {
            unload = false;
        }

        // create unit vector to get the way point direction
        int xDif = sectionDestination.getBlockX() - sectionStart.getBlockX();
        int zDif = sectionDestination.getBlockZ() - sectionStart.getBlockZ();
        Vector unitVector = new Vector(xDif, 0, zDif).normalize();

        int wayPointCount = (int) sectionStart.distance(sectionDestination) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance;
        for (int i = 1; i < wayPointCount; i++) {
            Location wpLocation = sectionStart.clone();
            Vector unitVectorCopy = unitVector.clone();
            wpLocation.add(unitVectorCopy.multiply(i * RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.wayPointDistance));
            wpLocation = world.getHighestBlockAt(wpLocation).getLocation();
            wpLocation.setY(wpLocation.getY() + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.flightHeight);

            section.getFlight().addWaypoint(wpLocation);
        }

        if(unload) {
            chunk.unload();
        }
    }

    private void interpolateSection(RouteSection section, int currentIndex) {

    }

    private List<RouteSection> getStationRoute() {

        List<DragonStation> availableStations = StationManager.INST.getStations();
        SortedMap<Integer, DragonStation> ratedStations = new TreeMap<>();
        List<RouteSection> route = new ArrayList<>();
        DragonStation lastStarPoint = start;
        DragonStation nextStarPoint;
        availableStations.remove(lastStarPoint);

        while (lastStarPoint != destination) {
            ratedStations.clear();

            for (DragonStation station : availableStations) {
                if(!station.getLocation().getWorld().getName().equalsIgnoreCase(start.getLocation().getWorld().getName())) {
                    continue;
                }

                Integer rating = new Integer((int) (2 * Math.sqrt(Math.pow(lastStarPoint.getLocation().distance(station.getLocation()), 2) + Math.pow(station.getLocation().distance(destination.getLocation()), 2))));
                if (lastStarPoint.getLocation().distance(station.getLocation()) < station.getLocation().distance(destination.getLocation())) {
                    rating += 1000;
                }

                ratedStations.put(rating, station);
            }
            nextStarPoint = ratedStations.get(ratedStations.firstKey());
            route.add(new RouteSection(lastStarPoint, nextStarPoint));
            lastStarPoint = nextStarPoint;
            availableStations.remove(lastStarPoint);
        }

        return route;
    }

    private void takeoff() {

        task.cancel(); // if takeoff is called cancel calculation task
        if(flight == null) return;
        FlightTravel.flyFlight(flight, player, RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.dynamicFlightSpeed);
        RaidCraft.getEconomy().withdrawPlayer(player.getName(), price);
        player.sendMessage(ChatColor.GRAY + "Schreibe '" + RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.exitWords[0] + "' in den Chat um den Flug abzubrechen!");
    }

}
