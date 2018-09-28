package de.raidcraft.dragontravelplus.tables;

import io.ebean.Finder;

public class TStationFinder extends Finder<Long, TStation> {

    public TStationFinder() {
        super(TStation.class);
    }
}
