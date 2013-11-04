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

        if (destination.isEmergencyTarget() || (start.getPrice() == 0 && destination.getPrice() == 0)) {
            return 0;
        }
        int costLevel;
        if (destination.getPrice() == 0) {
            costLevel = (int)start.getPrice();
        } else {
            costLevel = (int)destination.getPrice();
        }
        return Math.abs(Math.round(costLevel * start.getLocation().distance(destination.getLocation()) * RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().pricePerBlock * 100.) / 100.);
    }

}
