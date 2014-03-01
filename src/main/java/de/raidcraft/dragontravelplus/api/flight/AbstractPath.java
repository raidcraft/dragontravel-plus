package de.raidcraft.dragontravelplus.api.flight;

import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractPath implements Path {

    private final List<Waypoint> waypoints = new LinkedList<>();
    private final Location start;
    private final Location end;

    public AbstractPath(Location start, Location end) {

        this.start = start;
        this.end = end;
    }

    public Location getStart() {

        return start;
    }

    public Location getEnd() {

        return end;
    }

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

    protected void clearWaypoints() {

        waypoints.clear();
    }

    @Override
    public int getWaypointAmount() {

        return waypoints.size();
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

        return waypoints;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractPath)) return false;

        AbstractPath that = (AbstractPath) o;

        if (!end.equals(that.end)) return false;
        if (!start.equals(that.start)) return false;
        if (!waypoints.equals(that.waypoints)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = waypoints.hashCode();
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
