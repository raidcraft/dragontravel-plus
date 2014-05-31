package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.rctravel.api.station.Station;

/**
 * @author Silthus
 */
public class TeleportFlight extends DragonStationFlight {

    public TeleportFlight(Station startStation, Station endStation, Aircraft<?> aircraft, Path path) {

        super(startStation, endStation, aircraft, path);
    }

    @Override
    public long getMoveInterval() {

        return -1;
    }

    @Override
    public void onStartFlight() throws FlightException {

        super.onStartFlight();
        getPassenger().getEntity().teleport(getLastWaypoint());
        endFlight();
    }
}
