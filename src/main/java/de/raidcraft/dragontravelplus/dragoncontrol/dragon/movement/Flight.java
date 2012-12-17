package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import java.util.HashMap;

public class Flight {

	HashMap<Integer, Waypoint> waypoints = new HashMap<Integer, Waypoint>();
	int currentwp = 0;
	public int wpcreatenum = 0;
	String name;

	/**
	 * Flight object, containing a flight-name and waypoints
	 * 
	 * @param name
	 */
	public Flight(String name) {
		this.name = name;
	}

	/**
	 * Adds a waypoint to the db as a key/keyvalue
	 * 
	 * @param wp
	 */
	public void addWaypoint(Waypoint wp) {
		waypoints.put(wpcreatenum, wp);
		wpcreatenum++;
	}

	/**
	 * Removes the last waypoint
	 */
	public void removeWaypoint() {
		if (wpcreatenum == 0)
			return;
		wpcreatenum--;
		waypoints.get(wpcreatenum).removeMarker();
		waypoints.remove(wpcreatenum);
	}

	/**
	 * Gets the firstwaypoint
	 * 
	 * @return
	 */
	public Waypoint getFirstWaypoint() {
		Waypoint wp = waypoints.get(currentwp);
		currentwp++;
		return wp;
	}

	/**
	 * Gets the next waypoint for this flight
	 */
	public Waypoint getNextWaypoint() {
		Waypoint wp = waypoints.get(currentwp);
		currentwp++;
		return wp;
	}

}
