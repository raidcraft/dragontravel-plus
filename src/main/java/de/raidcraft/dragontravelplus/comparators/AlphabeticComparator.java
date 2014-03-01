package de.raidcraft.dragontravelplus.comparators;

import de.raidcraft.rctravel.api.station.Station;

import java.util.Comparator;

/**
 * @author Silthus
 */
public class AlphabeticComparator implements Comparator<Station> {

    @Override
    public int compare(Station o1, Station o2) {

        return o1.getPlainName().compareTo(o2.getPlainName());
    }
}
