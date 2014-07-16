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
    public boolean isActive() {

        return true;
    }

    @Override
    public void startFlight() {

        try {
            onStartFlight();
            getPassenger().getEntity().teleport(getEndLocation().add(0, 3, 0));
            endFlight();
        } catch (FlightException e) {
            getPassenger().sendMessage(e.getMessage());
        }
    }
    @Override
    public void abortFlight() {

        try {
            onAbortFlight();
            getPassenger().getEntity().teleport(getStartLocation());
        } catch (FlightException e) {
            getPassenger().sendMessage(e.getMessage());
        }
    }

    @Override
    public void endFlight() {

        try {
            onEndFlight();
        } catch (FlightException e) {
            getPassenger().sendMessage(e.getMessage());
        }
    }
}
