package de.raidcraft.dragontravelplus.api.flight;

import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;

/**
 * @author Silthus
 */
public abstract class AbstractFlight implements Flight {

    private final Aircraft<?> aircraft;
    private final Path path;

    public AbstractFlight(Aircraft<?> aircraft, Path path) {

        this.aircraft = aircraft;
        this.path = path;
    }

    @Override
    public Aircraft<?> getAircraft() {

        return aircraft;
    }

    @Override
    public Path getPath() {

        return path;
    }

    @Override
    public boolean isActive() {

        return getAircraft().isFlying();
    }

    @Override
    public void startFlight() throws FlightException {

        if (isActive()) throw new FlightException("Flight was already started. Cannot start again!");
        getAircraft().takeoff();
        for (Passenger passenger : getAircraft().getPassengers()) {
            passenger.setFlight(this);
        }
    }

    @Override
    public void abortFlight() throws FlightException {

        if (!isActive()) throw new FlightException("Flight was not started. Cannot abort flight!");
        getAircraft().abortFlight();
        for (Passenger passenger : getAircraft().getPassengers()) {
            passenger.getEntity().teleport(getStartLocation());
            passenger.setFlight(null);
        }
    }

    @Override
    public void endFlight() throws FlightException {

        if (!isActive()) throw new FlightException("Flight was not started. Cannot end flight!");
        getAircraft().land();
        for (Passenger passenger : getAircraft().getPassengers()) {
            passenger.setFlight(null);
        }
    }
}
