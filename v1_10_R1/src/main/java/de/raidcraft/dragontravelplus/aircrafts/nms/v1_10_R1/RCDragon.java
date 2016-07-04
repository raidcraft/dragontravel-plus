package de.raidcraft.dragontravelplus.aircrafts.nms.v1_10_R1;

import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_10_R1.EntityEnderDragon;
import net.minecraft.server.v1_10_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Silthus
 */
public class RCDragon extends EntityEnderDragon implements Aircraft<RCDragon> {

    @Getter
    @Setter
    private BukkitTask aircraftMoverTask;
    @Getter
    @Setter
    boolean flying;


    double XperTick = 0.5;
    double YperTick = 0.5;
    double ZperTick = 0.5;

    float playerPitch;
    int waypointRadius;
    double toX;
    double toY;
    double toZ;
    boolean moving = false;

    public RCDragon(org.bukkit.World world,
                    double speedX, double speedY, double speedZ,
                    int waypointRadius, float playerPitch) {

        this(((CraftWorld) world).getHandle(), speedX, speedY, speedZ, waypointRadius, playerPitch);
    }

    public RCDragon(World world,
                    double speedX, double speedY, double speedZ,
                    int waypointRadius, float playerPitch) {

        super(world);
        XperTick = speedX;
        ZperTick = speedZ;
        YperTick = speedY;
        this.waypointRadius = waypointRadius;
        this.playerPitch = playerPitch;
    }

    @Override
    public RCDragon getEntity() {

        return this;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, waypointRadius);
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

        this.moving = true;
        toX = waypoint.getX();
        toY = waypoint.getY();
        toZ = waypoint.getZ();
        // set yaw
        this.yaw = lookAtIgnoreY(locX, locZ, toX, toZ);
    }

    @Override
    public void startNavigation(Flight flight) {

        //        RaidCraft.LOGGER.info("startNavigation RCDragon");
    }

    @Override
    public void stopNavigation(Flight flight) {

        //        RaidCraft.LOGGER.info("stopNavigation RCDragon");
        Location currentLocation = getCurrentLocation();
        setLocation(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ(), 180F, 90F);
    }

    @Override
    public RCDragon spawn(Location location) {

        //        RaidCraft.LOGGER.info("spawn RCDragon");
        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
//        cT().a(DragonControllerPhase.a); //TODO: This will prevent dragons from only hovering
        ((CraftWorld) location.getWorld()).getHandle().addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return this;
    }

    @Override
    public void despawn() {

        //        RaidCraft.LOGGER.info("despawn RCDragon");
        getBukkitEntity().remove();
    }

    @Override
    public void mountPassenger(Flight flight) {

        //        RaidCraft.LOGGER.info("mount RCDragon");

        Entity passaenger = flight.getPassenger().getEntity();
        passaenger.teleport(getPlayerYaw(flight.getStartLocation(),
                flight.getEndLocation()));
        getBukkitEntity().setPassenger(passaenger);
    }

    @Override
    public void unmountPassenger(Flight flight) {

        //        RaidCraft.LOGGER.info("unmount RCDragon");
        //mount(null);
    }

    private double getNewInterpolatedCoordinate(double from, double to, double maxDiff) {

        if ((int) from != (int) to) {
            if (from < to) {
                if((to - from) < maxDiff) {
                    from += (to - from);
                } else {
                    from += maxDiff;
                }
            } else {
                if((from - to) < maxDiff) {
                    from -= (from - to);
                } else {
                    from -= maxDiff;
                }
            }
        }
        return from;
    }

    @Override
    public void m() {

        if (!moving) {
            return;
        }

        double myX = locX;
        double myY = locY;
        double myZ = locZ;

        myX = getNewInterpolatedCoordinate(myX, toX, XperTick);
        myY = getNewInterpolatedCoordinate(myY, toY, YperTick);
        myZ = getNewInterpolatedCoordinate(myZ, toZ, ZperTick);

        setPosition(myX, myY, myZ);
    }

    /**
     * Gets the correct yaw for this specific path
     */

    private float lookAtIgnoreY(double x, double z, double lookAtX, double lookAtZ) {

        // Values of change in distance (make it relative)
        double dx = x - lookAtX;
        double dz = z - lookAtZ;

        double myyaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                myyaw = 1.5 * Math.PI;
            } else {
                myyaw = 0.5 * Math.PI;
            }
            myyaw = myyaw - Math.atan(dz / dx);
        } else if (dz < 0) {
            myyaw = Math.PI;
        }

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        return (float) (-myyaw * 180f / Math.PI);
    }

    public Location getPlayerYaw(Location loc, Location lookat) {
        //Clone the loc to prevent applied changes to the input loc
        loc = loc.clone();

        // Values of change in distance (make it relative)
        double dx = lookat.getX() - loc.getX();
        double dz = lookat.getZ() - loc.getZ();

        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }

        // Set pitch
        loc.setPitch(playerPitch);

        // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);

        return loc;
    }
}