package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.Waypoint;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class DynamicInterpolatedFlightPath extends DynamicFlightPath {

    public DynamicInterpolatedFlightPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public synchronized void calculate() {

        super.calculate();

        // now we have our basic set of waypoints and need to smooth things by interpolating between the gathered data
        for (int i = 0; i < getWaypointAmount(); i++) {
            interpolateSection(i);
        }
    }

    private void interpolateSection(int currentIndex) {

        // don't optimize first or last way point
        if(currentIndex == 0 || currentIndex == getWaypointAmount() - 1) {
            return;
        }

        Waypoint currentLocation = getWaypoint(currentIndex);
        Waypoint preLocation = getWaypoint(currentIndex - 1);
        Waypoint nextLocation = getWaypoint(currentIndex + 1);

        double currentY = currentLocation.getY();
        double newY = currentLocation.getY();
        double preDiff = (int)(currentLocation.getY() - preLocation.getY());
        double nextDiff = (int)(currentLocation.getY() - nextLocation.getY());
        // limit route diff to 20 blocks high
        if(Math.abs(preDiff) > 20) {
            if(preDiff < 0) {
                newY = preLocation.getY() - 20;
            }
            else {
                newY = preLocation.getY() + 20;
            }
        }
        // set the updated waypoint
        currentLocation.setY(newY);
        setWaypoint(currentIndex, currentLocation);
    }
}
