package de.raidcraft.dragontravelplus.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "flight_waypoints")
public class TWaypoint implements Comparable<TWaypoint> {

    @Id
    private int id;
    @ManyToOne
    private TPath path;
    private int index;
    private String world;
    private double x;
    private double y;
    private double z;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPath getPath() {

        return path;
    }

    public void setPath(TPath path) {

        this.path = path;
    }

    public int getIndex() {

        return index;
    }

    public void setIndex(int index) {

        this.index = index;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public double getX() {

        return x;
    }

    public void setX(double x) {

        this.x = x;
    }

    public double getY() {

        return y;
    }

    public void setY(double y) {

        this.y = y;
    }

    public double getZ() {

        return z;
    }

    public void setZ(double z) {

        this.z = z;
    }

    @Override
    public int compareTo(TWaypoint o) {

        if (getIndex() < o.getIndex()) return -1;
        if (getIndex() > o.getIndex()) return 1;
        return 0;
    }
}
