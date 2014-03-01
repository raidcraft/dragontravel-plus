package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractAircraft<T> implements Aircraft<T> {

    private boolean flying = false;
    private BukkitTask task;

    @Override
    public boolean isFlying() {

        return flying;
    }

    @Override
    public void takeoff(Flight flight) {

        if (!flying) {
            try {
                this.flying = true;
                if (!isSpawned()) spawn(flight.getStartLocation());
                mountPassenger(flight);
                move(flight.getPath().getFirstWaypoint());
                // lets start the task that moves the aircraft around from waypoint to waypoint
                task = Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(DragonTravelPlusPlugin.class),
                        new AircraftMoverTask(this, flight), 20L, 20L);
            } catch (FlightException ignored) {
            }
        }
    }

    @Override
    public void abortFlight(Flight flight) {

        if (flying) {
            this.flying = false;
            stopMoving();
            unmountPassenger(flight);
            task.cancel();
            if (isSpawned()) despawn();
        }
    }

    @Override
    public void land(Flight flight) {

        if (flying) {
            this.flying = false;
            stopMoving();
            unmountPassenger(flight);
            task.cancel();
            if (flight.getEndLocation() == null) flight.setEndLocation(getCurrentLocation());
            if (isSpawned()) despawn();
        }
    }
}
