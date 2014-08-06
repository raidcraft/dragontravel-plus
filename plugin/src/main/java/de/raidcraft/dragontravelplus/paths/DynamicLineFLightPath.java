package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.AbstractPath;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.dragontravelplus.DTPConfig;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * for testing
 * @author Dragonfire
 */
public class DynamicLineFlightPath extends AbstractPath {

    public DynamicLineFlightPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public void calculate() {

        clearWaypoints();

        World world = getStartLocation().getWorld();
        boolean unloadChunk;

        int y = 105;

        DTPConfig config = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig();

        Location lastUnsafeStart = getStartLocation().clone();
        for (int i = 1; i < 100; i++) {
            // calculate unsafe start point
            Location wpLocation = lastUnsafeStart.clone();
            wpLocation.add(config.wayPointDistance, 0, 0);
            wpLocation.setY(y);

            // dont allow waypoints above max world height
            if (wpLocation.getY() > world.getMaxHeight()) wpLocation.setY(world.getMaxHeight());
            addWaypoint(new Waypoint(wpLocation));

            lastUnsafeStart = wpLocation;
        }
    }

    public int getHeighestBlock(Location start, Vector unitVector, int distance) {

        int heighest = -1;
        World w = start.getWorld();
        for (int i = 1; i <= distance; i++) {
            Vector unitVectorCopy = unitVector.clone();
            Location tmp = start.clone();
            tmp.add(unitVectorCopy.multiply(i));
            int blockHeight = w.getHighestBlockYAt(tmp);
            if (blockHeight > heighest) {
                heighest = blockHeight;
            }
        }
        return heighest;
    }
}
