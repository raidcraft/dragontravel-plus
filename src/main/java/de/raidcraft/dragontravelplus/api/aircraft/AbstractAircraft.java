package de.raidcraft.dragontravelplus.api.aircraft;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.passenger.Passenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public abstract class AbstractAircraft<T> implements Aircraft<T> {

    private Passenger<?> passenger;
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
                mountPassenger();
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
            unmountPassenger();
            task.cancel();
            if (isSpawned()) despawn();
        }
    }

    @Override
    public void land(Flight flight) {

        if (flying) {
            this.flying = false;
            stopMoving();
            unmountPassenger();
            task.cancel();
            if (flight.getEndLocation() == null) flight.setEndLocation(getCurrentLocation());
            if (isSpawned()) despawn();
        }
    }

    @Override
    public boolean containsPassenger(LivingEntity entity) {

        return getPassenger() != null && getPassenger().getEntity().equals(entity);
    }

    @Override
    public Passenger<?> getPassenger() {

        return passenger;
    }

    @Override
    public Passenger<?> removePassenger() {

        Passenger<?> passenger = this.passenger;
        this.passenger = null;
        return passenger;
    }

    @Override
    public void setPassenger(Passenger<?> passenger) {

        this.passenger = passenger;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractAircraft)) return false;

        AbstractAircraft that = (AbstractAircraft) o;

        if (!passenger.equals(that.passenger)) return false;
        if (!getEntity().equals(that.getEntity())) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = getEntity().hashCode();
        result = 31 * result + passenger.hashCode();
        return result;
    }
}
