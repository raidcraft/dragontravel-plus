package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Path;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import de.raidcraft.dragontravelplus.flights.FreePathFlight;
import de.raidcraft.dragontravelplus.passengers.BukkitPlayerPassenger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class FlightManager implements Component {

    private final DragonTravelPlusPlugin plugin;
    private final Map<Passenger, Flight> activeFlights = new HashMap<>();

    public FlightManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(FlightManager.class, this);
    }

    public Passenger getPassenger(Player player) {

        return new BukkitPlayerPassenger(player);
    }

    public List<Flight> getActiveFlights() {

        return new ArrayList<>(activeFlights.values());
    }

    public void registerFlight(Flight flight) {

        activeFlights.put(flight.getPassenger(), flight);
    }

    public void unregisterFlight(Flight flight) {

        activeFlights.remove(flight.getPassenger());
    }

    public Flight getFlight(Passenger passenger) throws FlightException {

        if (passenger.getFlight() == null && activeFlights.containsKey(passenger)) {
            passenger.setFlight(activeFlights.get(passenger));
        } else if (passenger.getFlight() != null && !activeFlights.containsKey(passenger)) {
            activeFlights.put(passenger, passenger.getFlight());
        }
        if (passenger.getFlight() == null) {
            throw new FlightException("The passenger " + passenger.getName() + " has no active flight!");
        }
        return passenger.getFlight();
    }

    public Flight createFlight(Passenger passenger, Path path) {

        FreePathFlight flight = new FreePathFlight(plugin.getAircraftManager().getAircraft(), path, passenger.getEntity().getLocation());
        flight.setPassenger(passenger);
        return flight;
    }
}
