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

    public WayPoint clone() {
        return new WayPoint(world, x, y, z);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WayPoint wayPoint = (WayPoint) o;

        if (Double.compare(wayPoint.x, x) != 0) return false;
        if (Double.compare(wayPoint.y, y) != 0) return false;
        if (Double.compare(wayPoint.z, z) != 0) return false;
        if (!world.equals(wayPoint.world)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + world.hashCode();
        return result;
    }
}
