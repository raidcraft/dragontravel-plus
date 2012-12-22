package de.raidcraft.dragontravelplus.util;

import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * Author: Philip
 * Date: 22.12.12 - 19:55
 * Description:
 */
public class FlightDistance {

    public static String getPrintDistance(DragonStation start, DragonStation destination) {
        double distance = start.getLocation().distance(destination.getLocation());
        String distancePrint;
        if(distance >= 1000) {
            distancePrint = (Math.round((distance/1000.) * 100.) / 100) + "km";
        }
        else {
            distancePrint = (Math.round(distance * 100.) / 100) + "m";
        }
        return distancePrint;
    }
}
