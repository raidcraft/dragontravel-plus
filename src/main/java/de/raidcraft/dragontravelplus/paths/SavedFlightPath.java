package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.AbstractPath;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class SavedFlightPath extends AbstractPath {

    public SavedFlightPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public void calculate() {

        // do nothing
    }
}
