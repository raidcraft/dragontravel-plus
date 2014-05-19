package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.AircraftManager;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.flights.DragonStationFlight;
import de.raidcraft.rctravel.api.station.Station;

/**
 * @author Silthus
 */
public class DragonStationRoute {

    private final Station startStation;
    private final Station endStation;
    private final Path path;

    public DragonStationRoute(Station startStation, Station endStation, Path path) {

        this.startStation = startStation;
        this.endStation = endStation;
        this.path = path;
    }

    public Station getStartStation() {

        return startStation;
    }

    public Station getEndStation() {

        return endStation;
    }

    public Path getPath() {

        return path;
    }

    public Flight createFlight(Passenger<?> passenger) {

        DragonStationFlight flight = new DragonStationFlight(getStartStation(), getEndStation(),
                RaidCraft.getComponent(AircraftManager.class).getAircraft(passenger), getPath());
        flight.setPassenger(passenger);
        return flight;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof DragonStationRoute)) return false;

        DragonStationRoute that = (DragonStationRoute) o;

        if (!endStation.equals(that.endStation)) return false;
        if (!path.equals(that.path)) return false;
        return startStation.equals(that.startStation);

    }

    @Override
    public int hashCode() {

        int result = startStation.hashCode();
        result = 31 * result + endStation.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
