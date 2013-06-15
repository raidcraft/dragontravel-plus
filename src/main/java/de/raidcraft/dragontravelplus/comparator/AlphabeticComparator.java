package de.raidcraft.dragontravelplus.comparator;

import de.raidcraft.dragontravelplus.station.DragonStation;

import java.util.Comparator;

/**
 * @author Philip Urban
 */
public class AlphabeticComparator implements Comparator<DragonStation> {

    @Override
    public int compare(DragonStation o1, DragonStation o2) {

        return o1.getName().compareTo(o2.getName());
    }
}
