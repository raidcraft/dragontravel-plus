package de.raidcraft.dragontravelplus;

import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.RemoteEntities;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dragontravelplus.aircrafts.RemoteAircraftDragon;
import de.raidcraft.dragontravelplus.api.aircraft.Aircraft;
import de.raidcraft.util.EnumUtils;
import org.bukkit.Bukkit;

/**
 * @author Silthus
 */
public final class AircraftManager implements Component {

    public enum AircraftType {

        REMOTE_ENTITY;

        public static AircraftType fromString(String name) {

            return EnumUtils.getEnumFromString(AircraftType.class, name);
        }
    }

    private final DragonTravelPlusPlugin plugin;
    private final AircraftType type;
    private EntityManager entityManager;

    public AircraftManager(DragonTravelPlusPlugin plugin) {

        this.plugin = plugin;
        this.type = AircraftType.fromString(plugin.getConfig().aircraftType);
        if (type == null) {
            plugin.getLogger().severe("Invalid aircraft type specified! Shutting down plugin...");
            plugin.disable();
            return;
        }
        if (type == AircraftType.REMOTE_ENTITY) {
            if (Bukkit.getPluginManager().getPlugin("RemoteEntities") != null) {
                this.entityManager = RemoteEntities.createManager(plugin, true);
            } else {
                plugin.getLogger().severe("RemoteEntites as aircraft type, but plugin was not found! Disabling...");
                plugin.disable();
            }
        }
        RaidCraft.registerComponent(AircraftManager.class, this);
    }

    public Aircraft<?> getAircraft() {

        switch (type) {

            case REMOTE_ENTITY:
                return new RemoteAircraftDragon(entityManager);
        }
        return null;
    }
}
