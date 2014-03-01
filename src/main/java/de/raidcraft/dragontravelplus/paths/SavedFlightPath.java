package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class SavedFlightPath extends StaticFlightPath {

    public SavedFlightPath(Location start, Location end, TPath path) {

        super(start, end);
        for (TWaypoint waypoint : path.getWaypoints()) {
            addWaypoint(waypoint.getWaypointIndex(), new SavedWaypoint(waypoint));
        }
    }

    @Override
    public void calculate() {

        // do nothing
    }
}
