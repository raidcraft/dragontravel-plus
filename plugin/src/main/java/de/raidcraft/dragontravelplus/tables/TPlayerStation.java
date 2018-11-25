package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.ebean.BaseModel;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;
import io.ebean.EbeanServer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Silthus
 */
@Setter
@Getter
@Entity
@Table(name = "rc_flight_player_stations")
public class TPlayerStation extends BaseModel {

    public static final TPlayerStationFinder find = new TPlayerStationFinder();

    @ManyToOne
    private TStation station;
    private String player;
    private UUID playerId;
    private Timestamp discovered;

    @Override
    protected EbeanServer database() {
        return RaidCraft.getDatabase(DragonTravelPlusPlugin.class);
    }
}