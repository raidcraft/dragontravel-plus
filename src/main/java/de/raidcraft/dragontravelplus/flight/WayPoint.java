package de.raidcraft.dragontravelplus.flight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Defines location out of coordinates x,y,z
 */
public class WayPoint {

    private double x;
    private double y;
    private double z;
    private String world;
    private Block marker;

    public WayPoint(String world, double x, double y, double z) {

        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WayPoint(Location location) {
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location getLocation() {
        World bukkitWorld = Bukkit.getWorld(world);
        if(bukkitWorld == null) return null;

        return new Location(bukkitWorld, x, y, z);
    }

    public void setX(double x) {

        this.x = x;
    }

    public double getX() {

        return this.x;
    }

    public void setY(double y) {

        this.y = y - 2;
    }

    public double getY() {

        return this.y;
    }

    public void setZ(double z) {

        this.z = z;
    }

    public double getZ() {

        return this.z;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    @Override
    public WayPoint clone() {
        return new WayPoint(world, x, y, z);
    }

    public boolean sameBlockLocation(Location location) {

        Location thisLocation = getLocation();
        return location.getBlockX() == thisLocation.getBlockX()
                && location.getBlockY() == thisLocation.getBlockY()
                && location.getBlockZ() == thisLocation.getBlockZ();
    }
}
