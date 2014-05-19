package de.raidcraft.dragontravelplus.aircrafts;

import de.raidcraft.api.flight.aircraft.AbstractAircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.FlightException;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;


/**
 * @author Silthus
 */
public class CitizensAircraftDragon extends AbstractAircraft<NPC> {

    private final NPC npc;

    public CitizensAircraftDragon(Citizens citizens) {

        this.npc = citizens.getNPCRegistry().createNPC(EntityType.ENDER_DRAGON, "Flying Dragon");
        npc.setFlyable(true);
        npc.setProtected(true);
    }
    
    @Override
    public Entity getBukkitEntity() {
        
        return getEntity().getEntity();
    }

    @Override
    public NPC getEntity() {

        return npc;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, 1);
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius) {

        return LocationUtil.isWithinRadius(waypoint.getLocation(), getEntity().getEntity().getLocation(), radius);
    }

    @Override
    public Location getCurrentLocation() {

        return getEntity().getEntity().getLocation();
    }

    @Override
    public boolean isSpawned() {

        return getEntity().isSpawned();
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

        getEntity().getNavigator().setTarget(waypoint.getLocation());
    }

    @Override
    public void stopMoving() {

        getEntity().getNavigator().cancelNavigation();
    }

    @Override
    public NPC spawn(Location location) {

        getEntity().spawn(location);
        return getEntity();
    }

    @Override
    public void despawn() {

        getEntity().despawn(DespawnReason.REMOVAL);
    }

    @Override
    public void mountPassenger(Flight flight) throws FlightException {

        getEntity().getEntity().setPassenger(flight.getPassenger().getEntity());
    }

    @Override
    public void unmountPassenger(Flight flight) {

        getEntity().getEntity().setPassenger(null);
    }
}