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

    public RemoteAircraftDragon(EntityManager entityManager) {

        prepareEntity = entityManager.prepareEntity(RemoteEntityType.EnderDragon);
        prepareEntity.asPushable(false);
        prepareEntity.asStationary(false);
        prepareEntity.withSpeed(RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightSpeed);
    }

    @Override
    public void move(Waypoint waypoint) {

        if (!isSpawned()) return;
        getEntity().move(waypoint.getLocation());
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

        return LocationUtil.isWithinRadius(getCurrentLocation(), waypoint.getLocation(),
                RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().wayPointRadius);
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

        return getEntity() != null;
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
