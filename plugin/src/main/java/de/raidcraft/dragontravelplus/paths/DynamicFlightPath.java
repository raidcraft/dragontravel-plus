package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import de.raidcraft.api.flight.flight.AbstractPath;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 * @author Silthus
 */
public class DynamicFlightPath extends AbstractPath {

    public DynamicFlightPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public synchronized void calculate() {

        clearWaypoints();

        World world = getStartLocation().getWorld();
        boolean unloadChunk;

        // create unit vector to get the way point direction
        int xDif = getEndLocation().getBlockX() - getStartLocation().getBlockX();
        int zDif = getEndLocation().getBlockZ() - getStartLocation().getBlockZ();
        Vector unitVector = new Vector(xDif, 0, zDif).normalize();

        // here we simply calculate what points are between the start and the end location
        // we always add the defined flight height to the next waypoint
        int wayPointCount = LocationUtil.getBlockDistance(getStartLocation(), getEndLocation()) / RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().wayPointDistance;
        for (int i = 1; i < wayPointCount; i++) {
            Location wpLocation = getStartLocation().clone();
            Vector unitVectorCopy = unitVector.clone();
            wpLocation.add(unitVectorCopy.multiply(i * RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().wayPointDistance));

            // lets remember if we need to unload the chunk
            unloadChunk = !wpLocation.getChunk().isLoaded();
            // we are getting a block, this will load the chunk
            wpLocation = world.getHighestBlockAt(wpLocation).getLocation();
            // lets unload the chunk if needed to avoid memory leaking
            if (unloadChunk) wpLocation.getChunk().unload();

            wpLocation.setY(wpLocation.getY() + RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig().flightHeight);
            // dont allow waypoints above max world height
            if (wpLocation.getY() > world.getMaxHeight()) wpLocation.setY(world.getMaxHeight());

            addWaypoint(new Waypoint(wpLocation));
        }
    }
}
