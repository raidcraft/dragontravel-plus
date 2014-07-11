package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Path;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.AircraftManager;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.flights.DragonStationFlight;
import de.raidcraft.dragontravelplus.flights.TeleportFlight;
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

    // TODO: performance - for each player
    // wenn man schon für jeden Spieler einen eigenen Listener erstellt, dann sollte man auch alle
    // nicht Spieler relevanten Events abfangen ...
    public Flight createFlight(Passenger<?> passenger) {

        Flight flight;
        Aircraft<?> aircraft = RaidCraft.getComponent(AircraftManager.class).getAircraft(passenger);
        if (RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightTeleportFallback) {
            flight = new TeleportFlight(getStartStation(), getEndStation(), null, getPath());
        } else {
            flight = new DragonStationFlight(getStartStation(), getEndStation(), aircraft, getPath());
        }
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
