package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.tables.TPath;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import org.bukkit.Location;

import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
public class SavedFlightPath extends StaticFlightPath {

    public SavedFlightPath(Location start, Location end, TPath path) {

        super(start, end);
        List<TWaypoint> waypoints = path.getWaypoints();
        Collections.sort(waypoints);
        for (TWaypoint waypoint : waypoints) {
            addWaypoint(waypoint.getWaypointIndex(), new SavedWaypoint(waypoint));
        }
    }

    @Override
    public void calculate() {

        // do nothing
    }
}
