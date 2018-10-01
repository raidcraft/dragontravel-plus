package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.api.ebean.BaseModel;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Data
@Entity
@Table(name = "rc_flight_paths")
public class TPath extends BaseModel {

    public static final TPathFinder find = new TPathFinder();

    @Column(unique = true)
    private String name;
    @ManyToOne
    @Column(name = "start_station_id")
    private TStation startStation;
    @ManyToOne
    @Column(name = "end_station_id")
    private TStation endStation;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "path_id")
    private List<TWaypoint> waypoints = new ArrayList<>();
}

