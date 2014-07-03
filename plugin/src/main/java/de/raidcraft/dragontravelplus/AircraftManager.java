package de.raidcraft.dragontravelplus;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.aircrafts.CitizensAircraftDragon;
import de.raidcraft.dragontravelplus.aircrafts.RemoteAircraftDragon;
import de.raidcraft.util.EntityUtil;
import de.raidcraft.util.EnumUtils;
import de.raidcraft.util.ReflectionUtil;
import net.citizensnpcs.Citizens;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Silthus
 */
public final class AircraftManager implements Component {

    public enum AircraftType {

        VANILLA,
        REMOTE_ENTITIES,
        CITIZENS;

        public static AircraftType fromString(String name) {

            return EnumUtils.getEnumFromString(AircraftType.class, name);
        }
    }

    private final DragonTravelPlusPlugin plugin;
    private final AircraftType type;

    private EntityManager remoteEntityManager;
    private Citizens citizens;

    protected AircraftManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        this.type = AircraftType.fromString(plugin.getConfig().aircraftType);
        if (type == null) {
            plugin.getLogger().severe("Invalid aircraft type (" + plugin.getConfig().aircraftType + ") specified! " +
                    "Shutting down plugin...");
            plugin.disable();
            return;
        }
        switch (type) {
            case REMOTE_ENTITIES:
                if (Bukkit.getPluginManager().getPlugin("RemoteEntities") != null) {
                    this.remoteEntityManager = RemoteEntities.createManager(plugin, true);
                } else {
                    plugin.getLogger().severe("RemoteEntites as aircraft type, but plugin was not found! Disabling...");
                    plugin.disable();
                }
                break;
            case CITIZENS:
                if (Bukkit.getPluginManager().getPlugin("Citizens") != null) {
                    this.citizens = Citizens.getPlugin(Citizens.class);
                } else {
                    plugin.getLogger().severe("Citizens as aircraft type, but plugin was not found! Disabling...");
                    plugin.disable();
                }
                break;
            case VANILLA:
                // we need to register the custom entity matching the correct version
                EntityUtil.registerEntity("EnderDragon", 63, ReflectionUtil.getNmsClass("de.raidcraft.dragontravelplus.aircrafts.nms", "RCDragon"));
                break;
        }
        RaidCraft.registerComponent(AircraftManager.class, this);
    }

    public Aircraft<?> getAircraft(Passenger<?> passenger) {

        switch (type) {

            case REMOTE_ENTITIES:
                return new RemoteAircraftDragon(remoteEntityManager);
            case CITIZENS:
                return new CitizensAircraftDragon(citizens);
            case VANILLA:
                try {
                    Class<?> clazz = ReflectionUtil.getNmsClass("de.raidcraft.dragontravelplus.aircrafts.nms", "RCDragon");
                    return (Aircraft<?>) clazz.getConstructor(World.class, double.class).newInstance(passenger.getEntity().getWorld(), plugin.getConfig().flightSpeed);
                } catch (Exception e) {
                    plugin.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
        }
        return null;
    }
}