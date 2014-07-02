package de.raidcraft.dragontravelplus.aircrafts.nms.v1_7_R3;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R3.EntityEnderDragon;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public class RCDragon extends EntityEnderDragon implements Aircraft<RCDragon> {

    @Getter
    @Setter
    private boolean flying;
    @Getter
    @Setter
    private BukkitTask aircraftMoverTask;

    public RCDragon(org.bukkit.World world) {

        this(((CraftWorld) world).getHandle());
    }

    public RCDragon(World world) {

        super(world);
    }

    @Override
    public RCDragon getEntity() {

        return this;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, 1);
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius) {

        return LocationUtil.isWithinRadius(
                waypoint.getLocation(),
                getCurrentLocation(),
                radius);
    }

    @Override
    public Location getCurrentLocation() {

        return new Location(this.world.getWorld(), this.locX, this.locY, this.locZ);
    }

    @Override
    public boolean isSpawned() {

        return getBukkitEntity() != null && getBukkitEntity().isValid() && !getBukkitEntity().isDead();
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

        move(waypoint.getX(), waypoint.getY(), waypoint.getZ());
    }

    @Override
    public void startNavigation(Flight flight) {


    }

    @Override
    public void stopNavigation(Flight flight) {

        Location currentLocation = getCurrentLocation();
        move(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
    }

    @Override
    public RCDragon spawn(Location location) {

        if (!isSpawned()) {
            ((CraftWorld) location.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
            setLocation(location.getX(), location.getY(), location.getZ(), 0F, 0F);
            RaidCraft.LOGGER.info("spawned RCDragon");
        }
        return this;
    }

    @Override
    public void despawn() {

        if (isSpawned()) {
            getBukkitEntity().remove();
        }
    }

    @Override
    public void mountPassenger(Flight flight) {

        if (flight.getPassenger().getEntity() instanceof CraftLivingEntity) {
            mount(((CraftLivingEntity) flight.getPassenger().getEntity()).getHandle());
        }
    }

    @Override
    public void unmountPassenger(Flight flight) {

        mount(null);
    }
}