package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.AbstractPath;
import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class SavedFlightPath extends AbstractPath {

    public SavedFlightPath(Location start, Location end, TPath path) {

        super(start, end);
        for (TWaypoint waypoint : path.getWaypoints()) {
            addWaypoint(waypoint.getIndex(), new SavedWaypoint(waypoint));
        }
    }

    @Override
    public void calculate() {

        // do nothing
    }
}
