package de.raidcraft.dragontravelplus.aircrafts;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.aircraft.AbstractAircraft;
import de.raidcraft.api.flight.flight.Flight;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.dragontravelplus.DTPConfig;
import de.raidcraft.util.LocationUtil;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.MobType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;


/**
 * @author Silthus
 */
public class CitizensAircraftDragon extends AbstractAircraft<NPC> {

    private final NPC npc;
    private NPCRegistry register;
    private DTPConfig config;

    public CitizensAircraftDragon(Citizens citizens, DTPConfig config) {

        this.config = config;
        RaidCraft.LOGGER.warning("create CitizensAircraftDragon");
        register = NPC_Manager.getInstance().getNonPersistentRegistry();
        this.npc = register.createNPC(EntityType.ENDER_DRAGON, "Flying Dragon");
        npc.setFlyable(true);
        npc.setProtected(true);
        npc.getTrait(MobType.class).setType(EntityType.ENDER_DRAGON);
    }

    @Override
    public Entity getBukkitEntity() {

        return npc.getEntity();
    }

    @Override
    public NPC getEntity() {

        return npc;
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint) {

        return hasReachedWaypoint(waypoint, config.waypointRadius);
    }

    @Override
    public boolean hasReachedWaypoint(Waypoint waypoint, int radius) {

        return LocationUtil.isWithinRadius(waypoint.getLocation(), getCurrentLocation(), radius);
    }

    @Override
    public Location getCurrentLocation() {

        return npc.getStoredLocation();
    }

    @Override
    public boolean isSpawned() {

        return npc.isSpawned();
    }

    @Override
    public void move(Flight flight, Waypoint waypoint) {

        RaidCraft.LOGGER.warning("move CitizensAircraftDragon");
        npc.getNavigator().setTarget(waypoint.getLocation());
    }

    @Override
    public void startNavigation(Flight flight) {

        RaidCraft.LOGGER.warning("startNavigation CitizensAircraftDragon");
        npc.getNavigator().setTarget(flight.getEndLocation());
        npc.getNavigator().getLocalParameters().useNewPathfinder(true);
        npc.getNavigator().getLocalParameters().range((float) 999999.0f);
        npc.getNavigator().getLocalParameters().baseSpeed((float) config.flightSpeed);
        npc.getNavigator().getLocalParameters().speedModifier(1.0F);
        npc.getNavigator().setTarget(flight.getEndLocation());
    }

    @Override
    public void stopNavigation(Flight flight) {

        RaidCraft.LOGGER.warning("stopNavigation CitizensAircraftDragon");
        npc.getNavigator().cancelNavigation();
    }

    @Override
    public NPC spawn(Location location) {

        RaidCraft.LOGGER.warning("spawn CitizensAircraftDragon");
        npc.spawn(location);
        return getEntity();
    }

    @Override
    public void despawn() {

        RaidCraft.LOGGER.warning("despawn CitizensAircraftDragon");
        npc.despawn(DespawnReason.REMOVAL);
        register.deregister(npc);
    }

    @Override
    public void mountPassenger(Flight flight) {

        RaidCraft.LOGGER.warning("mount CitizensAircraftDragon");
        npc.getEntity().setPassenger(flight.getPassenger().getEntity());
    }

    @Override
    public void unmountPassenger(Flight flight) {

        RaidCraft.LOGGER.warning("unmount CitizensAircraftDragon");
        getEntity().getEntity().setPassenger(null);
        despawn();
    }
}