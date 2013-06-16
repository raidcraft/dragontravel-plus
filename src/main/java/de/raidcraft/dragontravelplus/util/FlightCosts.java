package de.raidcraft.dragontravelplus.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.station.DragonStation;

/**
 * Author: Philip
 * Date: 13.12.12 - 21:20
 * Description:
 */
public class FlightCosts {

    public static double getPrice(DragonStation start, DragonStation destination) {

        if (destination.isEmergencyTarget() || (start.getCostLevel() == 0 && destination.getCostLevel() == 0)) {
            return 0;
        }
        int costLevel;
        if (destination.getCostLevel() == 0) {
            costLevel = start.getCostLevel();
        } else {
            costLevel = destination.getCostLevel();
        }
        return Math.abs(Math.round(costLevel * start.getLocation().distance(destination.getLocation()) * RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().pricePerBlock * 100.) / 100.);
    }

}
