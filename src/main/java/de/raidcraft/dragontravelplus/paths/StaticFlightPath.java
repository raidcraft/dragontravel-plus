package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.AbstractPath;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class StaticFlightPath extends AbstractPath {

    public StaticFlightPath(Location start, Location end) {

        super(start, end);
    }

    public StaticFlightPath() {

        super();
    }

    @Override
    public void calculate() {

        // do nothing
    }
}
