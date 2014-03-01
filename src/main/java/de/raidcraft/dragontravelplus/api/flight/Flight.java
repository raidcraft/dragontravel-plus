package de.raidcraft.dragontravelplus.api.flight;

import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface Flight {

    /**
     * Gets the aircraft attached to this flight.
     * Can be a dragon or anything else in the future.
     * @return aircraft mountable by a passenger
     */
    public Aircraft getAircraft();

    /**
     * Gets the flight path the aircraft will take.
     * @return flight path
     */
    public Path getPath();

    /**
     * Checks if the flight is active and {@link de.raidcraft.dragontravelplus.api.aircraft.Aircraft#isFlying()}
     * @return true if flight is active
     */
    public boolean isActive();

    /**
     * Gets the first waypoint of the path and therefor the flight.
     * @return starting location of the flight
     */
    public Location getStartLocation();

    /**
     * Sets the end location of the flight. Usually done when the aircraft lands or before hand.
     * @param endLocation of the flight after landing
     */
    public void setEndLocation(Location endLocation);

    /**
     * Gets the last waypoint of the path and therefor the flight.
     * @return end location of the flight
     */
    public Location getEndLocation();

    /**
     * Checks if the passenger list contains a passenger that matches the given entity.
     * @param entity to match passenger list against
     * @return true if passenger list contains this entity
     */
    public boolean hasPassenger(LivingEntity entity);

    /**
     * Returns the current passenger of this aircraft
     * @return passenger of the aircraft
     */
    public Passenger getPassenger();

    /**
     * Sets the passenger of the aircraft.
     * @param passenger to add to the aircraft
     */
    public void setPassenger(Passenger<?> passenger);

    /**
     * Removes the given passenger from the aircraft. If a flight is in progress it will {@link de.raidcraft.dragontravelplus.api.flight.Flight#abortFlight()} the flight.
     * @return removed passenger or null if passenger could not be removed or wasnt on the aircraft
     */
    public Passenger<?> removePassenger();

    /**
     * Starts the flight, mounting the passenger and flying to the first waypoint.
     * @throws de.raidcraft.dragontravelplus.api.flight.FlightException if flight is already started
     */
    public void startFlight() throws FlightException;

    /**
     * Aborts the flight, unmounts the passenger and returns him to the start location of the flight.
     * @throws de.raidcraft.dragontravelplus.api.flight.FlightException if flight has not started
     */
    public void abortFlight() throws FlightException;

    /**
     * Ends the flight gracefully, unmounting the passenger without returning him to the start.
     * @throws de.raidcraft.dragontravelplus.api.flight.FlightException if flight has not started
     */
    public void endFlight() throws FlightException;

    /**
     * Checks if the flight path still has a next waypoint
     * @return false if the path is at the end
     */
    public boolean hasNextWaypoint();

    /**
     * Gets the next waypoint of the flight path.
     * @return next waypoint
     */
    public Waypoint nextWaypoint();

    /**
     * Gets the current last waypoint of the flight path.
     * @return current waypoint of the path
     */
    public Waypoint getCurrentWaypoint();
}
