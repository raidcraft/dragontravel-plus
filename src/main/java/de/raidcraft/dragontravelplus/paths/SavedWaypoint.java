package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.dragontravelplus.api.flight.Waypoint;
import de.raidcraft.dragontravelplus.tables.TWaypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * @author Silthus
 */
public class SavedWaypoint extends Waypoint {

    public SavedWaypoint(Location location) {

        super(location);
    }

    public SavedWaypoint(TWaypoint waypoint) {

        this(new Location(Bukkit.getWorld(waypoint.getWorld()), waypoint.getX(), waypoint.getY(), waypoint.getZ()));
    }

    public SavedWaypoint(World world, double x, double y, double z) {

        super(world, x, y, z);
    }

}
