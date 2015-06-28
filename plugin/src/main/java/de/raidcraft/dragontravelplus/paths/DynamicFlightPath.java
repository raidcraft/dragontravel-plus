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

import java.util.List;

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
        double xDif = getEndLocation().getX() - getStartLocation().getX();
        double yDif = getEndLocation().getY() - getStartLocation().getY();
        double zDif = getEndLocation().getZ() - getStartLocation().getZ();
        Vector unitVector = new Vector(xDif, yDif, zDif).normalize();

        // here we simply calculate what points are between the start and the end location
        // we always add the defined flight height to the next waypoint
        DTPConfig config = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig();
        int wayPointDistance = config.wayPointDistance;
        double distance = LocationUtil.getRealDistance(getStartLocation().getX(), getStartLocation().getZ(),
                getEndLocation().getX(), getEndLocation().getZ());
        double wayPointCount = distance / wayPointDistance;
        double lastY = -1;
        double minGroundDiff = config.fligthMinGroundDistance;
        double maxGroundDiff = config.fligthMaxGroundDistance;
        double flighHeight = (maxGroundDiff - minGroundDiff) / 2;
        Location lastUnsafeStart = getStartLocation().clone();
        for (int i = 1; i < wayPointCount; i++) {
            // calculate unsafe start point
            Location wpLocation = getStartLocation().clone();
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

            /*
             * Calculate Y coordinate for a smooth flight
             */
            // use last y for start of calculation
            double y = lastY - config.flightDragonFalling;
            // check if minimum ground difference is reached
            if((y - heighestBlockY) < minGroundDiff) {
                y += (minGroundDiff - (y - heighestBlockY));
            }
            // check if maximum ground difference is exceeded
            if((y - heighestBlockY) > maxGroundDiff) {
                y -= (maxGroundDiff - (y - heighestBlockY));
            }

            lastY = y;
            wpLocation.setY(y);

            // dont allow waypoints above max world height
            if (wpLocation.getY() > world.getMaxHeight()) wpLocation.setY(world.getMaxHeight());
            addWaypoint(new Waypoint(wpLocation));
        }

        // add "H_H" check #1313
        List<Waypoint> waypoints = getWaypoints();
        double y = -1;
        double nextY = -1;
        int forwardIteration = config.flightGapIteration;
        for (int i = 1; i < waypoints.size() - 1; i++) {
            lastY = waypoints.get(i - 1).getY();
            y = waypoints.get(i).getY();
            nextY = waypoints.get(i + 1).getY();
            // iter over it, e.g. to find "H__H"
            for (int iter = 0; iter < forwardIteration; iter++) {
                int nextIndex = i + 1 + iter;
                // if at the end
                if (nextIndex >= waypoints.size()) {
                    break;
                }
                nextY = Math.max(nextY, waypoints.get(nextIndex).getY());
            }
            // if we are flying into a gap
            if (lastY <= y || nextY <= y) {
                continue;
            }
            // use the y of next or last
            waypoints.get(i).setY(Math.max(lastY, nextY));
        }

        // add "_H" check #1313
        double dy = -1;
        // TODO: maybe iter from behind to increase performance
        for (int i = 1; i < waypoints.size(); i++) {
            // if the y-delte is to much, lift up
            dy = waypoints.get(i).getY() - waypoints.get(i - 1).getY();
            if (dy < config.flightLiftUpDelta) {
                continue;
            }
            // lift up
            Waypoint liftUpWaypoint = new Waypoint(waypoints.get(i - 1).getLocation().clone());
            liftUpWaypoint.setY(waypoints.get(i).getY());
            waypoints.add(i, liftUpWaypoint);
            // increase index of new added waypoint
            i++;
        }

        // add end point
        addWaypoint(new Waypoint(getEndLocation().clone()));
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
