package de.raidcraft.dragontravelplus.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "flight_stations")
public class TStation {

    @Id
    private int id;
    @Column(unique = true)
    private String name;
    private String displayName;
    private String world;
    private int x;
    private int y;
    private int z;
    private double costMultiplier = 1.0;
    private boolean mainStation = false;
    private boolean emergencyStation = false;
    @OneToMany(mappedBy = "start_station_id")
    private List<TPath> paths = new ArrayList<>();

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

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public String getWorld() {

        return world;
    }

    public void setWorld(String world) {

        this.world = world;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getZ() {

        return z;
    }

    public void setZ(int z) {

        this.z = z;
    }

    public double getCostMultiplier() {

        return costMultiplier;
    }

    public void setCostMultiplier(double costMultiplier) {

        this.costMultiplier = costMultiplier;
    }

    public boolean isMainStation() {

        return mainStation;
    }

    public void setMainStation(boolean mainStation) {

        this.mainStation = mainStation;
    }

    public boolean isEmergencyStation() {

        return emergencyStation;
    }

    public void setEmergencyStation(boolean emergencyStation) {

        this.emergencyStation = emergencyStation;
    }

    public List<TPath> getPaths() {

        return paths;
    }

    public void setPaths(List<TPath> paths) {

        this.paths = paths;
    }
}
