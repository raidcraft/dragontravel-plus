package de.raidcraft.dragontravelplus.navigator;

import de.raidcraft.dragontravelplus.flight.Flight;
import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * @author Philip
 */
public class RouteSection {

    private DragonStation start;
    private DragonStation destination;
    private Flight flight;

    public RouteSection(DragonStation start, DragonStation destination) {

        this.start = start;
        this.destination = destination;

        String flightName = "dtp_" + start.getName().toLowerCase() + "@" + destination.getName().toLowerCase();
        flight = Flight.loadFlight(flightName);
        if(flight == null) {
            flight = new Flight(flightName);
        }
    }

    public DragonStation getStart() {

        return start;
    }

    public DragonStation getDestination() {

        return destination;
    }

    public Flight getFlight() {

        return flight;
    }
}
