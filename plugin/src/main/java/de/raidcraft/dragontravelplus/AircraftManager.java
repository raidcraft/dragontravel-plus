package de.raidcraft.dragontravelplus;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.flight.aircraft.Aircraft;
import de.raidcraft.api.flight.passenger.Passenger;
import de.raidcraft.dragontravelplus.aircrafts.CitizensAircraftDragon;
import de.raidcraft.nms.api.EntityRegistry;
import de.raidcraft.util.EnumUtils;
import de.raidcraft.util.ReflectionUtil;
import net.citizensnpcs.Citizens;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Optional;

/**
 * @author Silthus
 */
public final class AircraftManager implements Component {

    public static final String RC_DRAGON_ENTITY_NAME = "rc_dtp_dragon";
    private static final String NMS_PACKAGE = "de.raidcraft.dragontravelplus.aircrafts.nms";

    public enum AircraftType {

        VANILLA,
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
                Class<?> rcDragon = ReflectionUtil.getNmsClass(NMS_PACKAGE, "RCDragon");
                if (rcDragon != null) RaidCraft.getComponent(EntityRegistry.class).registerCustomEntity(RC_DRAGON_ENTITY_NAME, EntityType.ENDER_DRAGON, rcDragon);
                break;
        }
        RaidCraft.registerComponent(AircraftManager.class, this);
    }

    public Aircraft<?> getAircraft(Passenger<?> passenger) {

        switch (type) {

            case CITIZENS:
                return new CitizensAircraftDragon(citizens, plugin.getConfig());
            case VANILLA:
                Optional<Entity> entity = RaidCraft.getComponent(EntityRegistry.class).getEntity(RC_DRAGON_ENTITY_NAME, passenger.getEntity().getWorld());
                if (entity.isPresent() && entity.get() instanceof Aircraft<?>) {
                    ((Aircraft) entity.get()).load(plugin.getConfig().getAircraftConfig());
                    return (Aircraft<?>) entity.get();
                }
        }
        return null;
    }
}