package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.AbstractPath;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.dragontravelplus.DTPConfig;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
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
    public void calculate() {

        clearWaypoints();

        World world = getStartLocation().getWorld();
        boolean unloadChunk;

        // create unit vector to get the way point direction
        int xDif = getEndLocation().getBlockX() - getStartLocation().getBlockX();
        int yDif = getEndLocation().getBlockY() - getStartLocation().getBlockY();
        int zDif = getEndLocation().getBlockZ() - getStartLocation().getBlockZ();
        Vector unitVector = new Vector(xDif, yDif, zDif).normalize();

        // here we simply calculate what points are between the start and the end location
        // we always add the defined flight height to the next waypoint
        DTPConfig config = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig();
        int wayPointDistance = config.wayPointDistance;
        int wayPointCount = LocationUtil.getBlockDistance(getStartLocation(), getEndLocation()) / wayPointDistance;
        double lastY = -1;
        double minGroundDiff = config.fligthMinGroundDistance;
        double maxGroundDiff = config.fligthMaxGroundDistance;
        double flighHeight = (maxGroundDiff - minGroundDiff) / 2;
        Location lastUnsafeStart = getStartLocation().clone();
        for (int i = 1; i < wayPointCount; i++) {
            // calculate unsafe start point
            Location wpLocation = lastUnsafeStart.clone();
            Vector unitVectorCopy = unitVector.clone();
            wpLocation.add(unitVectorCopy.multiply(i * wayPointDistance));

            // lets remember if we need to unload the chunk
            unloadChunk = !wpLocation.getChunk().isLoaded();
            // load chunk and find heighest Block
            double heighestBlockY = getHeighestBlock(lastUnsafeStart, unitVector.clone(), wayPointDistance);
            // save current unsafe start
            lastUnsafeStart = wpLocation.clone();
            // lets unload the chunk if needed to avoid memory leaking
            if (unloadChunk) wpLocation.getChunk().unload();

            // try to flight on the same height
            double y = lastY;
            if (heighestBlockY + minGroundDiff > y) {   // if new location is to low
                y = heighestBlockY + flighHeight;
            } else if (y > heighestBlockY + maxGroundDiff) { // if new location is to hight
                y = heighestBlockY + flighHeight;
            }
            lastY = y;
            wpLocation.setY(y);

            // dont allow waypoints above max world height
            if (wpLocation.getY() > world.getMaxHeight()) wpLocation.setY(world.getMaxHeight());
            addWaypoint(new Waypoint(wpLocation));
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
