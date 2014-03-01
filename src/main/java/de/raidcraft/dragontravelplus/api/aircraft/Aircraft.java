package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import org.bukkit.Location;

import java.util.Set;

/**
 * @author Silthus
 */
public interface Aircraft<T> {

    /**
     * Gets the flight of the aircraft.
     * @return flight
     */
    public Flight getFlight();

    /**
     * Gets entity that is the aircraft if it was spawned.
     * @return null if {@link #spawn()} was not called
     */
    public T getEntity();

    /**
     * Gets the current location of the aircraft.
     * @return null if aircraft was not spawned
     */
    public Location getCurrentLocation();

    /**
     * Checks if the entity was spawned.
     * @return true if entity exists and was spawned
     */
    public boolean isSpawned();

    /**
     * Spawns the aircraft allowing it to take off and to accept passengers.
     * @return spanwed aircraft
     */
    public T spawn();

    /**
     * Despawns the entity after landing or aborting the flight.
     */
    public void despawn();

    /**
     * Checks if the aircraft is flying and {@link #takeoff()} was called and no {@link #land()} was done.
     * @return true if aircraft is flying
     */
    public boolean isFlying();

    /**
     * Will switch the aircraft into flying mode and strap on all the seatbelts for passengers on the aircraft.
     * Activates the flight mode which can abort user interaction with anything and so on.
     * If the aircraft {@link #isFlying()} it will not do anything
     * Please keep your seatbelt on if you are seated :)
     */
    public void takeoff();

    /**
     * Will abort the flight if {@link #isFlying()}
     * and teleport all passengers to the {@link de.raidcraft.dragontravelplus.api.flight.Flight#getStartLocation()}
     */
    public void abortFlight();

    /**
     * Lands the aircraft safely on the ground removing restrictions from the passengers.
     * Will not do anything if the aircraft !{@link #isFlying()}
     */
    public void land();

    /**
     * Returns a copy of all passengers attached to the aircraft.
     * @return unique set of passengers on the aircraft
     */
    public Set<Passenger<?>> getPassengers();

    /**
     * Adds a passenger to the aircraft if it is not already on it.
     * Passengers can only be on one aircraft at a time so if will {@link #removePassenger(de.raidcraft.dragontravelplus.api.passenger.Passenger)}
     * the passenger from his old aircraft if he is on one.
     * @param passenger to add to the aircraft
     * @return false if passenger could not be removed from his old aircraft or if he is already on the aircraft
     */
    public boolean addPassenger(Passenger<?> passenger);

    /**
     * Removes the given passenger from the aircraft. If a flight is in progress it will {@link de.raidcraft.dragontravelplus.api.flight.Flight#abortFlight()} the flight.
     * @param passenger to remove from the aircraft
     * @return removed passenger or null if passenger could not be removed or wasnt on the aircraft
     */
    public Passenger<?> removePassenger(Passenger<?> passenger);

    /**
     * Mounts all attached passengers onto the aircraft.
     * @throws de.raidcraft.dragontravelplus.api.flight.FlightException if {@link #spawn()} was not called
     */
    public void mountPassengers() throws FlightException;

    /**
     * Unmounts all passengers from the aircraft.
     */
    public void unmountPassengers();
}
