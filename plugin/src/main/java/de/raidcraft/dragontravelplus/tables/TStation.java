package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ebean.BaseModel;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import io.ebean.EbeanServer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Data
@Entity
@Table(name = "rc_flight_stations")
public class TStation extends BaseModel {

    public static final TStationFinder find = new TStationFinder();

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
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "station")
    private List<TPlayerStation> playerStations = new ArrayList<>();

    public Location getLocation() {

        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
    }

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(DragonTravelPlusPlugin.class);
    }
}
