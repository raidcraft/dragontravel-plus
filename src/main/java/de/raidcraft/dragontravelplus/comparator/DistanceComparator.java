package de.raidcraft.dragontravelplus.comparator;

import de.raidcraft.dragontravelplus.station.DragonStation;

import java.util.Comparator;

/**
 * @author Philip Urban
 */
public class DistanceComparator implements Comparator<DragonStation> {

    private DragonStation start;

    public DistanceComparator(DragonStation start) {

        this.start = start;
    }

    @Override
    public int compare(DragonStation o1, DragonStation o2) {

        double d1 = start.getLocation().distance(o1.getLocation());
        double d2 = start.getLocation().distance(o2.getLocation());
        if(d1 > d2) return 1;
        if(d2 > d1) return -1;
        return 0;
    }
}
