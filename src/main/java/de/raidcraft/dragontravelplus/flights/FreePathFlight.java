package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.flight.Path;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class FreePathFlight extends RestrictedFlight {

    public FreePathFlight(Aircraft<?> aircraft, Path path, Location startLocation, Location endLocation) {

        super(aircraft, path, startLocation, endLocation);
    }
}
