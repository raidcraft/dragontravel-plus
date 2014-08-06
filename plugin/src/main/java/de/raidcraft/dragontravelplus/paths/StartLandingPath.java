package de.raidcraft.dragontravelplus.paths;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.flight.flight.AbstractPath;
import de.raidcraft.api.flight.flight.Waypoint;
import de.raidcraft.dragontravelplus.DTPConfig;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dragonfire
 */
public class StartLandingPath extends AbstractPath {

    public StartLandingPath(Location start, Location end) {

        super(start, end);
    }

    @Override
    public void calculate() {

        clearWaypoints();

        World world = getStartLocation().getWorld();
        boolean unloadChunk;

        DTPConfig config = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getConfig();
        int wayPointDistance = config.wayPointDistance;
        int max = world.getMaxHeight();
        int diff = wayPointDistance;

        // start path
        Location last = getStartLocation();
        while (last.getBlockY() + diff < max) {
            // calculate unsafe start point
            Location wpLocation = last.clone();
            wpLocation.add(diff, diff, 0);
            addWaypoint(new Waypoint(wpLocation));
            last = wpLocation;
        }

        // landing oath
        List<Waypoint> landing = new ArrayList<>();
        last = getEndLocation();
        while (last.getBlockY() + diff < max) {
            // calculate unsafe start point
            Location wpLocation = last.clone();
            wpLocation.add(-diff, diff, 0);
            landing.add(new Waypoint(wpLocation));
            last = wpLocation;
        }

        for (int i = landing.size() - 1; i >= 0; i--) {
            addWaypoint(landing.get(i));
        }
        // at landing point
        addWaypoint(new Waypoint(getEndLocation()));
    }
}
