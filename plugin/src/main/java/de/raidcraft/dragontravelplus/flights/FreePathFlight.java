package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Path;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class FreePathFlight extends PayedFlight {

    public FreePathFlight(Aircraft<?> aircraft, Path path, Location startLocation, Location endLocation) {

        super(startLocation, endLocation, aircraft, path);
    }
}
