package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import de.raidcraft.dragontravelplus.flights.DragonStationFlight;
import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * @author Silthus
 */
public class DragonStationRoute {

    private final DragonStation startStation;
    private final DragonStation endStation;
    private final Path path;

    public DragonStationRoute(DragonStation startStation, DragonStation endStation, Path path) {

        this.startStation = startStation;
        this.endStation = endStation;
        this.path = path;
    }

    public DragonStation getStartStation() {

        return startStation;
    }

    public DragonStation getEndStation() {

        return endStation;
    }

    public Path getPath() {

        return path;
    }

    public Flight createFlight(Aircraft<?> aircraft, Passenger<?> passenger) {

        DragonStationFlight flight = new DragonStationFlight(getStartStation(), getEndStation(), aircraft, getPath());
        flight.getAircraft().setPassenger(passenger);
        return flight;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof DragonStationRoute)) return false;

        DragonStationRoute that = (DragonStationRoute) o;

        if (!endStation.equals(that.endStation)) return false;
        if (!path.equals(that.path)) return false;
        if (!startStation.equals(that.startStation)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = startStation.hashCode();
        result = 31 * result + endStation.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
