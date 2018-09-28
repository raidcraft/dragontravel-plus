package de.raidcraft.dragontravelplus.tables;

import io.ebean.Finder;

public class TPlayerStationFinder extends Finder<Long, TPlayerStation> {

    public TPlayerStationFinder() {
        super(TPlayerStation.class);
    }
}
