package de.raidcraft.dragontravelplus.aircrafts;

import de.raidcraft.api.flight.aircraft.AbstractAircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.MobType;
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
        npc.getTrait(MobType.class).setType(EntityType.ENDER_DRAGON);
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

        return isSpawned() && LocationUtil.isWithinRadius(waypoint.getLocation(), getEntity().getEntity().getLocation(), radius);
    }

    @Override
    public Location getCurrentLocation() {

        if (isSpawned()) {
            return getEntity().getEntity().getLocation();
        } else {
            return getEntity().getStoredLocation();
        }
    }

    @Override
    public boolean isSpawned() {

        return getEntity().isSpawned();
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

    }

    @Override
    public void startNavigation(Flight flight) {

        if (!isSpawned()) spawn(flight.getStartLocation());
        getEntity().getNavigator().setTarget(flight.getEndLocation());
        getEntity().getNavigator().getLocalParameters().useNewPathfinder(true);
    }

    @Override
    public void stopNavigation(Flight flight) {

        if (isSpawned()) {
            getEntity().getNavigator().cancelNavigation();
        }
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
    public void mountPassenger(Flight flight) {

        if (isSpawned()) {
            getEntity().getEntity().setPassenger(flight.getPassenger().getEntity());
        }
    }

    @Override
    public void unmountPassenger(Flight flight) {

        if (isSpawned()) {
            getEntity().getEntity().setPassenger(null);
        }
    }
}