package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractAircraft<T> implements Aircraft<T> {

    private final Flight flight;
    private final Set<Passenger<?>> passengers = new HashSet<>();
    private boolean flying = false;

    protected AbstractAircraft(Flight flight) {

        this.flight = flight;
    }

    @Override
    public Flight getFlight() {

        return flight;
    }

    @Override
    public boolean isFlying() {

        return flying;
    }

    @Override
    public void takeoff() {

        if (!flying) {
            try {
                this.flying = true;
                if (!isSpawned()) spawn();
                mountPassengers();
                getFlight().setStartLocation(getCurrentLocation());
            } catch (FlightException ignored) {
            }
        }
    }

    @Override
    public void abortFlight() {

        if (flying) {
            this.flying = false;
            unmountPassengers();
            if (isSpawned()) despawn();
        }
    }

    @Override
    public void land() {

        if (flying) {
            this.flying = false;
            unmountPassengers();
            getFlight().setEndLocation(getCurrentLocation());
            if (isSpawned()) despawn();
        }
    }

    @Override
    public Set<Passenger<?>> getPassengers() {

        return new HashSet<>(passengers);
    }

    @Override
    public boolean addPassenger(Passenger<?> passenger) {

        return passengers.add(passenger);
    }

    @Override
    public Passenger removePassenger(Passenger passenger) {

        if (passengers.remove(passenger)) {
            return passenger;
        }
        return null;
    }
}
