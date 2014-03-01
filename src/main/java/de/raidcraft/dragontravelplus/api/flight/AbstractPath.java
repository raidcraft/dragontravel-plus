package de.raidcraft.dragontravelplus.api.flight;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractPath implements Path {

    private final List<Waypoint> waypoints = new LinkedList<>();
    private int currentIndex = 0;

    @Override
    public Waypoint getFirstWaypoint() {

        if (!waypoints.isEmpty()) {
            return waypoints.get(0);
        }
        return null;
    }

    @Override
    public Waypoint getLastWaypoint() {

        if (!waypoints.isEmpty()) {
            return waypoints.get(waypoints.size() - 1);
        }
        return null;
    }

    @Override
    public Waypoint getWaypoint(int index) {

        return waypoints.get(index);
    }

    @Override
    public Waypoint removeWaypoint(int index) {

        return waypoints.remove(index);
    }

    @Override
    public Waypoint removeWaypoint(Waypoint waypoint) {

        if (waypoints.remove(waypoint)) {
            return waypoint;
        }
        return null;
    }

    @Override
    public void addWaypoint(Waypoint waypoint) {

        waypoints.add(waypoint);
    }

    @Override
    public void addWaypoint(int index, Waypoint waypoint) {

        if (index > waypoints.size()) {
            waypoints.add(waypoint);
        } else {
            waypoints.add(index, waypoint);
        }
    }

    @Override
    public Waypoint setWaypoint(int index, Waypoint waypoint) {

        if (index > waypoints.size()) {
            waypoints.add(waypoint);
            return waypoint;
        } else {
            return waypoints.set(index, waypoint);
        }
    }

    @Override
    public List<Waypoint> getWaypoints() {

        return new LinkedList<>(waypoints);
    }

    @Override
    public boolean hasNext() {

        return currentIndex + 1 < waypoints.size();
    }

    @Override
    public Waypoint next() {

        return getWaypoint(++currentIndex);
    }

    @Override
    public void remove() {

        removeWaypoint(currentIndex);
    }
}
