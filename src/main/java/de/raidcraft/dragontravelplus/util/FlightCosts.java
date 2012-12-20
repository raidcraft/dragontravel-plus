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
        if(destination.isEmergencyTarget() || (start.getCostLevel() == 0 && destination.getCostLevel() == 0)) {
            return 0;
        }
        int costLevel = 1;
        if(destination.getCostLevel() == 0) {
            costLevel = destination.getCostLevel();
        }
        else {
            costLevel = start.getCostLevel();
        }
        return Math.round(costLevel * start.getLocation().distance(destination.getLocation()) * DragonTravelPlusModule.inst.config.pricePerBlock * 100.) / 100.;
    }

}
