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
import de.raidcraft.rctravel.api.station.Chargeable;
import de.raidcraft.rctravel.api.station.Station;
import de.raidcraft.util.LocationUtil;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class DragonStationFlight extends RestrictedFlight {

    private final Station startStation;
    private final Station endStation;

    public DragonStationFlight(Station startStation, Station endStation, Aircraft<?> aircraft, Path path) {

        super(aircraft, path, startStation.getLocation());
        this.startStation = startStation;
        this.endStation = endStation;
        setEndLocation(endStation.getLocation());
    }

    public Station getStartStation() {

        return startStation;
    }

    public Station getEndStation() {

        return endStation;
    }

    private double getPrice() {

        double price = 0.0;
        if (getStartStation() instanceof DragonStation && getEndStation() instanceof DragonStation) {
            price = ((DragonStation) startStation).getPrice((DragonStation) endStation);
        } else if (getStartStation() instanceof Chargeable) {
            price = ((Chargeable) getStartStation()).getPrice(
                    LocationUtil.getBlockDistance(getStartStation().getLocation(), getEndStation().getLocation()));
        }
        return price;
    }

    @Override
    public void startFlight() throws FlightException {

        Economy economy = RaidCraft.getEconomy();
        if (!economy.hasEnough(getAircraft().getPassenger().getName(), getPrice())) {
            throw new FlightException(Translator.tr(DragonTravelPlusPlugin.class, (Player) getAircraft().getPassenger().getEntity(),
                    "flight.no-money", "You dont have enough money to complete this flight!"));
        }
        super.startFlight();
    }

    @Override
    public void endFlight() throws FlightException {

        // lets substract the flight cost
        Economy economy = RaidCraft.getEconomy();
        double price = getPrice();
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
