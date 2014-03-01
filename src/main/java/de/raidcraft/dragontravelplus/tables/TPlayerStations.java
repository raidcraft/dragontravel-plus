package de.raidcraft.dragontravelplus.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Entity
@Table(name = "flight_player_stations")
public class TPlayerStations {

    @Id
    private int id;
    @ManyToOne
    private TStation station;
    private String player;
    private Timestamp discovered;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TStation getStation() {

        return station;
    }

    public void setStation(TStation station) {

        this.station = station;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public Timestamp getDiscovered() {

        return discovered;
    }

    public void setDiscovered(Timestamp discovered) {

        this.discovered = discovered;
    }
}
