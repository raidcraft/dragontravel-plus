package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Waypoint;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface Aircraft<T> {

    /**
     * Gets entity that is the aircraft if it was spawned.
     *
     * @return null if {@link #spawn(org.bukkit.Location)} was not called
     */
    public T getEntity();

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     *
     * @return true if aircraft is near the waypoint
     */
    public boolean hasReachedWaypoint(Waypoint waypoint);

    /**
     * Checks if the aircraft reached the given waypoint.
     *
     * @param waypoint to reach
     * @param radius to check around the waypoint
     *
     * @return true if aircraft is near the waypoint
     */
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius);

    /**
     * Gets the current location of the aircraft.
     *
     * @return null if aircraft was not spawned
     */
    public Location getCurrentLocation();

    /**
     * Checks if the entity was spawned.
     *
     * @return true if entity exists and was spawned
     */
    public boolean isSpawned();

    /**
     * Moves the aircraft to the given waypoint
     *
     * @param waypoint to move to
     */
    public void move(Waypoint waypoint);

    /**
     * Stops the movement of the aircraft.
     */
    public void stopMoving();

    /**
     * Spawns the aircraft allowing it to take off and to accept passengers.
     *
     * @return spanwed aircraft
     */
    public T spawn(Location location);

    /**
     * Despawns the entity after landing or aborting the flight.
     */
    public void despawn();

    /**
     * Checks if the aircraft is flying and {@link #takeoff(de.raidcraft.dragontravelplus.api.flight.Flight)}
     * was called and no {@link #land(de.raidcraft.dragontravelplus.api.flight.Flight)} was done.
     *
     * @return true if aircraft is flying
     */
    public boolean isFlying();

    /**
     * Will switch the aircraft into flying mode and strap on all the seatbelts for passengers on the aircraft.
     * Activates the flight mode which can abort user interaction with anything and so on.
     * If the aircraft {@link #isFlying()} it will not do anything
     * Please keep your seatbelt on if you are seated :)
     *
     * @param flight that triggered the takeoff
     */
    public void takeoff(Flight flight);

    /**
     * Will abort the flight if {@link #isFlying()}
     * and teleport all passengers to the {@link de.raidcraft.dragontravelplus.api.flight.Flight#getFirstWaypoint()}
     *
     * @param flight that triggered the abort
     */
    public void abortFlight(Flight flight);

    /**
     * Lands the aircraft safely on the ground removing restrictions from the passengers.
     * Will not do anything if the aircraft !{@link #isFlying()}
     *
     * @param flight that triggered the landing
     */
    public void land(Flight flight);

    /**
     * Mounts all attached passengers onto the aircraft.
     *
     * @throws de.raidcraft.dragontravelplus.api.flight.FlightException if entity was not spawned
     */
    public void mountPassenger(Flight flight) throws FlightException;

    /**
     * Unmounts all passengers from the aircraft.
     */
    public void unmountPassenger(Flight flight);
}
