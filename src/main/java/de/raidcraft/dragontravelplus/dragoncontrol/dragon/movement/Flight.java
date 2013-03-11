package de.raidcraft.dragontravelplus.dragoncontrol.dragon.movement;

import de.raidcraft.RaidCraft;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Flight {

    HashMap<Integer, WayPoint> waypoints = new HashMap<Integer, WayPoint>();
    List<Block> markerBlocks = new ArrayList<>();
    int currentwp = 0;
    public int wpcreatenum = 0;
    String name;

    /**
     * Flight object, containing a flight-name and waypoints
     *
     * @param name
     */
    public Flight(String name) {

        this.name = name;
    }

    /**
     * Adds a waypoint to the db as a key/keyvalue
     *
     * @param wp
     */
    public void addWaypoint(WayPoint wp) {

        waypoints.put(wpcreatenum, wp);
        wpcreatenum++;
    }

    public void addWaypoint(Location location) {

        addWaypoint(new WayPoint(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }

    /**
     * Removes the last waypoint
     */
    public void removeWaypoint() {

        if (wpcreatenum == 0)
            return;
        wpcreatenum--;
        waypoints.get(wpcreatenum).removeMarker();
        waypoints.remove(wpcreatenum);
    }

    /**
     * Gets the firstwaypoint
     *
     * @return
     */
    public WayPoint getFirstWaypoint() {

        WayPoint wp = waypoints.get(currentwp);
        currentwp++;
        return wp;
    }

    /**
     * Gets the next waypoint for this flight
     */
    public WayPoint getNextWaypoint() {

        WayPoint wp = waypoints.get(currentwp);
        currentwp++;
        if (wp == null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(RaidCraft.getComponent(DragonTravelPlusPlugin.class), new Runnable() {
                @Override
                public void run() {

                    for (Block block : markerBlocks) {
                        block.setType(Material.AIR);
                    }
                }
            }, RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.markerDuration * 20);
            return null;
        }
        if (RaidCraft.getComponent(DragonTravelPlusPlugin.class).config.useVisibleWaypoints) {
            Block markerBlock = Bukkit.getWorld(wp.getWorld()).getBlockAt((int) wp.getX(), (int) wp.getY(), (int) wp.getZ());
            markerBlock.setType(Material.GLOWSTONE);
            markerBlocks.add(markerBlock);
        }
        return wp;
    }

}
