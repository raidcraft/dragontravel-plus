package de.raidcraft.dragontravelplus.util;

import de.raidcraft.dragontravelplus.DragonTravelPlusModule;
import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * Author: Philip
 * Date: 13.12.12 - 21:20
 * Description:
 */
public class FlightCosts {

    public static double getPrice(DragonStation start, DragonStation destination) {
        return Math.round(start.getCostLevel() * start.getLocation().distance(destination.getLocation()) * DragonTravelPlusModule.inst.config.pricePerBlock * 100.) / 100.;
    }

}
