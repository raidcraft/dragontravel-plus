package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.api.ebean.BaseModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Data
@Entity
@Table(name = "flight_waypoints")
public class TWaypoint extends BaseModel implements Comparable<TWaypoint> {

    @ManyToOne
    private TPath path;
    private int waypointIndex;
    private String world;
    private double x;
    private double y;
    private double z;

    @Override
    public int compareTo(TWaypoint o) {

        if (getWaypointIndex() < o.getWaypointIndex()) return -1;
        if (getWaypointIndex() > o.getWaypointIndex()) return 1;
        return 0;
    }
}
