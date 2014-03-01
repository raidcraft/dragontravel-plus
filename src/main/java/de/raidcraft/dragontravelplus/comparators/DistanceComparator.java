package de.raidcraft.dragontravelplus.comparators;

import de.raidcraft.rctravel.api.station.Station;

import java.util.Comparator;

/**
 * @author Silthus
 */
public class DistanceComparator implements Comparator<Station> {

    private Station start;

    public DistanceComparator(Station start) {

        this.start = start;
    }

    @Override
    public int compare(Station o1, Station o2) {

        double d1 = start.getLocation().distance(o1.getLocation());
        double d2 = start.getLocation().distance(o2.getLocation());
        if(d1 > d2) return 1;
        if(d2 > d1) return -1;
        return 0;
    }
}
