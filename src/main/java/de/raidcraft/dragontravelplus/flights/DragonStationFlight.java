package de.raidcraft.dragontravelplus.flights;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.economy.Economy;
import de.raidcraft.api.language.Translator;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import de.raidcraft.dragontravelplus.station.DragonStation;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class DragonStationFlight extends RestrictedFlight {

    private final DragonStation startStation;
    private final DragonStation endStation;

    public DragonStationFlight(DragonStation startStation, DragonStation endStation, Aircraft<?> aircraft, Path path) {

        super(aircraft, path);
        this.startStation = startStation;
        setStartLocation(startStation.getLocation());
        this.endStation = endStation;
        setEndLocation(endStation.getLocation());
    }

    public DragonStation getStartStation() {

        return startStation;
    }

    public DragonStation getEndStation() {

        return endStation;
    }

    @Override
    public void startFlight() throws FlightException {

        Economy economy = RaidCraft.getEconomy();
        double price = startStation.getPrice(startStation.getDistance(endStation));
        if (!economy.hasEnough(getAircraft().getPassenger().getName(), price)) {
            throw new FlightException(Translator.tr(DragonTravelPlusPlugin.class, (Player) getAircraft().getPassenger().getEntity(),
                    "flight.no-money", "You dont have enough money to complete this flight!"));
        }
        super.startFlight();
    }

    @Override
    public void endFlight() throws FlightException {

        // lets substract the flight cost
        Economy economy = RaidCraft.getEconomy();
        double price = startStation.getPrice(startStation.getDistance(endStation));
        Passenger passenger = getAircraft().getPassenger();
        if (passenger.getEntity() instanceof Player) {
            if (!economy.hasEnough(passenger.getName(), price)) {
                // abort the flight
                abortFlight();
                throw new FlightException(Translator.tr(DragonTravelPlusPlugin.class, (Player) passenger.getEntity(),
                        "flight.no-money", "You dont have enough money to complete this flight!"));
            }
            economy.substract(passenger.getName(), price);
        }
        super.endFlight();
    }
}
