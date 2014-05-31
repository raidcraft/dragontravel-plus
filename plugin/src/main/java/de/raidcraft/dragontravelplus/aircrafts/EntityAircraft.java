package de.raidcraft.dragontravelplus.aircrafts;

import de.raidcraft.api.flight.aircraft.AbstractAircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * @author Silthus
 */
public class EntityAircraft extends AbstractAircraft<Entity> {

    private final Entity entity;

    public EntityAircraft(Entity entity) {

        this.entity = entity;
    }

    @Override
    public Entity getBukkitEntity() {

        return entity;
    }

    @Override
    public Entity getEntity() {

        return entity;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, 1);
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius) {

        return LocationUtil.isWithinRadius(waypoint.getLocation(), getCurrentLocation(), radius);
    }

    @Override
    public Location getCurrentLocation() {

        return getEntity().getLocation();
    }

    @Override
    public boolean isSpawned() {

        return getEntity().isValid() && !getEntity().isDead();
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

        getEntity().teleport(waypoint.getLocation());
    }

    @Override
    public void stopMoving() {


    }

    @Override
    public Entity spawn(Location location) {

        return getEntity();
    }

    @Override
    public void despawn() {

        getEntity().remove();
    }

    @Override
    public void mountPassenger(Flight flight) {


    }

    @Override
    public void unmountPassenger(Flight flight) {


    }
}
