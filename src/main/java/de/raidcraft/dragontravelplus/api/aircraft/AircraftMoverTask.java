package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;

/**
 * @author Silthus
 */
public class AircraftMoverTask implements Runnable {

    private final Aircraft aircraft;
    private final Flight flight;

    protected AircraftMoverTask(Aircraft aircraft, Flight flight) {

        this.aircraft = aircraft;
        this.flight = flight;
    }

    @Override
    public void run() {

        if (!aircraft.hasReachedWaypoint(flight.getCurrentWaypoint())) {
            return;
        }
        if (!flight.hasNextWaypoint()) {
            try {
                flight.endFlight();
            } catch (FlightException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        } else {
            aircraft.move(flight.nextWaypoint());
        }
    }
}
