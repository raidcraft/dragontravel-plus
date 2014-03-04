package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.Waypoint;

/**
 * @author Silthus
 */
public class AircraftMoverTask implements Runnable {

    private final Aircraft aircraft;
    private final Flight flight;
    private final Waypoint lastWaypoint;
    private boolean landing = false;

    protected AircraftMoverTask(Aircraft aircraft, Flight flight) {

        this.aircraft = aircraft;
        this.flight = flight;
        this.lastWaypoint = new Waypoint(flight.getLastWaypoint());
    }

    @Override
    public void run() {

        if (!landing && !aircraft.hasReachedWaypoint(flight.getCurrentWaypoint())) {
            return;
        }
        if (!aircraft.isSpawned() && aircraft.isFlying()) {
            flight.abortFlight();
            return;
        }
        if (landing || !flight.hasNextWaypoint()) {
            if (!landing) {
                landing = true;
                aircraft.move(lastWaypoint);
            } else if (aircraft.hasReachedWaypoint(lastWaypoint, 3)) {
                flight.endFlight();
            }
        } else {
            aircraft.move(flight.nextWaypoint());
        }
    }
}
