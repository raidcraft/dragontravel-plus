package de.raidcraft.dragontravelplus.routes;

import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.passenger.Passenger;

/**
 * @author mdoering
 */
public interface Route {

    Flight createFlight(Passenger<?> passenger);
}
