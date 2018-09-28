package de.raidcraft.dragontravelplus.tables;

import io.ebean.Finder;

public class TPathFinder extends Finder<Long, TPath> {

    public TPathFinder() {
        super(TPath.class);
    }
}
