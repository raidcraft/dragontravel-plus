package de.raidcraft.dragontravelplus.tables;

import io.ebean.Finder;

public class TWaypointFinder extends Finder<Long, TWaypoint> {

    public TWaypointFinder() {
        super(TWaypoint.class);
    }
}
