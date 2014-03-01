package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public abstract class AbstractAircraft<T> implements Aircraft<T> {

    private final Flight flight;
    private Passenger<?> passenger;
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
                mountPassenger();
                getFlight().setStartLocation(getCurrentLocation());
            } catch (FlightException ignored) {
            }
        }
    }

    @Override
    public void abortFlight() {

        if (flying) {
            this.flying = false;
            unmountPassenger();
            if (isSpawned()) despawn();
        }
    }

    @Override
    public void land() {

        if (flying) {
            this.flying = false;
            unmountPassenger();
            getFlight().setEndLocation(getCurrentLocation());
            if (isSpawned()) despawn();
        }
    }

    @Override
    public boolean containsPassenger(LivingEntity entity) {

        return getPassenger() != null && getPassenger().getEntity().equals(entity);
    }

    @Override
    public Passenger<?> getPassenger() {

        return passenger;
    }

    @Override
    public Passenger<?> removePassenger() {

        Passenger<?> passenger = this.passenger;
        this.passenger = null;
        return passenger;
    }

    @Override
    public void setPassenger(Passenger<?> passenger) {

        this.passenger = passenger;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractAircraft)) return false;

        AbstractAircraft that = (AbstractAircraft) o;

        if (!flight.equals(that.flight)) return false;
        if (passenger != null ? !passenger.equals(that.passenger) : that.passenger != null) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = flight.hashCode();
        result = 31 * result + (passenger != null ? passenger.hashCode() : 0);
        return result;
    }
}
