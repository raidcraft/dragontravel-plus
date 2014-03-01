package de.raidcraft.dragontravelplus.api.flight;

import java.util.Iterator;
import java.util.List;

/**
 * @author Silthus
 */
public interface Path extends Iterator<Waypoint> {

    /**
     * Gets the starting waypoint in this path.
     * @return starting waypoint
     */
    public Waypoint getFirstWaypoint();

    /**
     * Gets the last waypoint in this path.
     * @return last waypoint
     */
    public Waypoint getLastWaypoint();

    /**
     * Gets the waypoint at the current iteration index.
     * @return current waypoint of the iteration
     */
    public Waypoint getCurrentWaypoint();

    /**
     * Gets the waypoint at the specified index.
     * @param index to get waypoint for
     * @return waypoint at index or throws {@link java.lang.IndexOutOfBoundsException}
     */
    public Waypoint getWaypoint(int index);

    /**
     * Removes the waypoint at the given index of the path.
     * @param index to remove waypoint at
     * @return null if index is invalid or no waypoint was found
     */
    public Waypoint removeWaypoint(int index);

    /**
     * Removes all matching waypoints from the path.
     * @param waypoint to remove
     * @return null if no waypoint was found in the path
     */
    public Waypoint removeWaypoint(Waypoint waypoint);

    /**
     * Adds a waypoint to the end of the path.
     * @param waypoint to add
     */
    public void addWaypoint(Waypoint waypoint);

    /**
     * Adds a waypoint at the given position of the path.
     * Will add the waypoint at the end of the list of index > size.
     * See {@link java.util.List#add(int, Object)}
     * @param index to insert waypoint at
     * @param waypoint to add
     */
    public void addWaypoint(int index, Waypoint waypoint);

    /**
     * Sets the waypoint at the given position overriding the existing waypoint.
     * Will add the waypoint at the end of the list of index > size.
     * See {@link java.util.List#set(int, Object)}
     * @param index to set waypoint at
     * @param waypoint to set
     * @return the previous waypoint if replaced or the new one if added at the end
     */
    public Waypoint setWaypoint(int index, Waypoint waypoint);

    /**
     * Gets a copy of all the waypoints in the path.
     * @return list of all waypoints
     */
    public List<Waypoint> getWaypoints();
}
