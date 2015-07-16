package de.raidcraft.dragontravelplus.routes;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.AircraftManager;
import de.raidcraft.dragontravelplus.flights.FreePathFlight;
import de.raidcraft.rctravel.api.station.Station;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class LocationToDragonStationRoute implements Route {

    private final Location startLocation;
    private final Station endStation;
    private final Path path;

    public LocationToDragonStationRoute(Location startLocation, Station endStation, Path path) {

        this.startLocation = startLocation;
        this.endStation = endStation;
        this.path = path;
    }

    public Location getStartLocation() {

        return startLocation;
    }

    public Station getEndStation() {

        return endStation;
    }

    public Path getPath() {

        return path;
    }

    // TODO: performance - for each player
    // wenn man schon fuer jeden Spieler einen eigenen Listener erstellt, dann sollte man auch alle
    // nicht Spieler relevanten Events abfangen ...
    public Flight createFlight(Passenger<?> passenger) {

        Flight flight;
        Aircraft<?> aircraft = RaidCraft.getComponent(AircraftManager.class).getAircraft(passenger);
        flight = new FreePathFlight(aircraft, getPath(), getStartLocation(), getEndStation().getLocation(), 0.0);
        flight.setPassenger(passenger);
        return flight;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof LocationToDragonStationRoute)) return false;

        LocationToDragonStationRoute that = (LocationToDragonStationRoute) o;

        if (!endStation.equals(that.endStation)) return false;
        if (!path.equals(that.path)) return false;
        return startLocation.equals(that.startLocation);

    }

    @Override
    public int hashCode() {

        int result = startLocation.hashCode();
        result = 31 * result + endStation.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
