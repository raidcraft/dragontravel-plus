package de.raidcraft.dragontravelplus.tables;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "flight_paths")
public class TPath {

    @Id
    private int id;
    @Column(unique = true)
    private String name;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @Column(name = "start_station_id")
    private TStation startStation;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @Column(name = "end_station_id")
    private TStation endStation;
    @OneToMany
    @JoinColumn(name = "path_id")
    private List<TWaypoint> waypoints = new ArrayList<>();

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public TStation getStartStation() {

        return startStation;
    }

    public void setStartStation(TStation startStation) {

        this.startStation = startStation;
    }

    public TStation getEndStation() {

        return endStation;
    }

    public void setEndStation(TStation endStation) {

        this.endStation = endStation;
    }

    public List<TWaypoint> getWaypoints() {

        List<TWaypoint> list = new ArrayList<>(waypoints);
        Collections.sort(list);
        return list;
    }

    public void setWaypoints(List<TWaypoint> waypoints) {

        this.waypoints = waypoints;
    }
}
