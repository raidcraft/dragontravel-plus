package de.raidcraft.dragontravelplus.aircrafts;

import de.kumpelblase2.remoteentities.CreateEntityContext;
import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.DespawnReason;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.entities.RemoteEnderDragon;
import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.dragontravelplus.api.aircraft.AbstractAircraft;
import de.raidcraft.dragontravelplus.api.flight.Flight;
import de.raidcraft.dragontravelplus.api.flight.FlightException;
import de.raidcraft.dragontravelplus.api.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public class RemoteAircraftDragon extends AbstractAircraft<RemoteEnderDragon> {

    private final CreateEntityContext prepareEntity;
    private RemoteEnderDragon spawnedEntity;
    private double flightSpeed;

    public RemoteAircraftDragon(EntityManager entityManager) {

        prepareEntity = entityManager.prepareEntity(RemoteEntityType.EnderDragon);
        prepareEntity.asPushable(false);
        prepareEntity.asStationary(false);
        flightSpeed = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightSpeed;
        prepareEntity.withSpeed(flightSpeed);
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

        if (!isSpawned()) return;
        if (getEntity().getBukkitEntity().getPassenger() == null) {
            abortFlight(flight);
            return;
        }
        getEntity().move(waypoint.getLocation(), flightSpeed);
    }

    @Override
    public void stopMoving() {

        if (!isSpawned()) return;
        getEntity().stopMoving();
    }

    @Override
    public RemoteEnderDragon getEntity() {

        return spawnedEntity;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().waypointRadius);
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius) {

        return LocationUtil.isWithinRadius(getCurrentLocation(), waypoint.getLocation(), radius);
    }

    @Override
    public Location getCurrentLocation() {

        if (isSpawned()) {
            return getEntity().getBukkitEntity().getLocation();
        }
        return null;
    }

    @Override
    public boolean isSpawned() {

        return getEntity() != null && getEntity().getBukkitEntity() != null;
    }

    @Override
    public RemoteEnderDragon spawn(Location location) {

        if (isSpawned()) return getEntity();
        prepareEntity.atLocation(location);
        spawnedEntity = (RemoteEnderDragon) prepareEntity.create();
        spawnedEntity.shouldDestroyBlocks(false);
        spawnedEntity.shouldNormallyFly(true);
        return getEntity();
    }

    @Override
    public void despawn() {

        if (!isSpawned()) return;
        getEntity().despawn(DespawnReason.CUSTOM);
    }

    @Override
    public void mountPassenger(Flight flight) throws FlightException {

        if (!isSpawned()) return;
        getEntity().getBukkitEntity().setPassenger(flight.getPassenger().getEntity());
    }

    @Override
    public void unmountPassenger(Flight flight) {

        if (!isSpawned()) return;
        getEntity().getBukkitEntity().setPassenger(null);
    }
}
