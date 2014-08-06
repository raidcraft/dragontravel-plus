package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.aircrafts.CitizensAircraftDragon;
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

            case CITIZENS:
                return new CitizensAircraftDragon(citizens, plugin.getConfig());
            case VANILLA:
                try {
                    Class<?> clazz = ReflectionUtil.getNmsClass("de.raidcraft.dragontravelplus.aircrafts.nms", "RCDragon");
                    DTPConfig config = plugin.getConfig();
                    return (Aircraft<?>) clazz.getConstructor(World.class,
                            double.class, double.class, double.class, int.class, float.class)
                            .newInstance(passenger.getEntity().getWorld(), config.speedX, config.speedY, config.speedZ,
                                    config.waypointRadius, config.playerPitch);
                } catch (Exception e) {
                    plugin.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
        }
        return null;
    }
}