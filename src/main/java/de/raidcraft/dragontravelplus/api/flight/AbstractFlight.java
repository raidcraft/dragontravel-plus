package de.raidcraft.dragontravelplus.api.flight;

import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public abstract class AbstractFlight implements Flight {

    private final Aircraft<?> aircraft;
    private final Path path;
    private final Location startLocation;
    private Passenger<?> passenger;
    private int currentIndex = 0;
    private Location endLocation;

    public AbstractFlight(Aircraft<?> aircraft, Path path, Location startLocation) {

        this.aircraft = aircraft;
        this.path = path;
        this.startLocation = startLocation;
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
    public Location getStartLocation() {

        return startLocation;
    }

    @Override
    public Location getEndLocation() {

        return endLocation;
    }

    @Override
    public void setEndLocation(Location endLocation) {

        this.endLocation = endLocation;
    }

    @Override
    public boolean hasPassenger(LivingEntity entity) {

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
    public boolean isActive() {

        return getAircraft().isFlying();
    }

    @Override
    public boolean hasNextWaypoint() {

        return currentIndex < getPath().getWaypoints().size();
    }

    @Override
    public Waypoint nextWaypoint() {

        return getPath().getWaypoints().get(currentIndex++);
    }

    @Override
    public Waypoint getCurrentWaypoint() {

        return getPath().getWaypoints().get(currentIndex);
    }

    @Override
    public void startFlight() throws FlightException {

        if (isActive()) throw new FlightException("Flight was already started. Cannot start again!");
        getAircraft().takeoff(this);
        getPassenger().setFlight(this);
    }

    @Override
    public void abortFlight() throws FlightException {

        if (!isActive()) throw new FlightException("Flight was not started. Cannot abort flight!");
        getAircraft().abortFlight(this);
        getPassenger().getEntity().teleport(getStartLocation());
    }

    @Override
    public void endFlight() throws FlightException {

        if (!isActive()) throw new FlightException("Flight was not started. Cannot end flight!");
        getAircraft().land(this);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractFlight)) return false;

        AbstractFlight that = (AbstractFlight) o;

        if (currentIndex != that.currentIndex) return false;
        if (!aircraft.equals(that.aircraft)) return false;
        if (!path.equals(that.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = aircraft.hashCode();
        result = 31 * result + path.hashCode();
        result = 31 * result + currentIndex;
        return result;
    }
}
