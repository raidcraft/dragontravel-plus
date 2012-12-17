package de.raidcraft.dragontravelplus.dragoncontrol;

import de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement.Flight;
import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * Author: Philip
 * Date: 17.12.12 - 21:56
 * Description:
 */
public class FlightNavigator {
    public final static FlightNavigator INST = new FlightNavigator();
    
    public Flight getFlight(DragonStation start, DragonStation destination) {
        Flight flight = new Flight(start.getName() + "_" + destination.getName());


        flight.addWaypoint(destination.getLocation());

        return flight;
    }
}
