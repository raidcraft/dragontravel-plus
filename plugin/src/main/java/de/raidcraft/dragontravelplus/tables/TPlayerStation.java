package de.raidcraft.dragontravelplus.tables;

import de.raidcraft.util.UUIDUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author Silthus
 */
@Entity
@Table(name = "flight_player_stations")
public class TPlayerStation {

    @Setter
    @Getter
    @Id
    private int id;
    @Setter
    @Getter
    @ManyToOne
    private TStation station;
    @Setter
    private UUID playerId;
    private String player;
    @Setter
    @Getter
    private Timestamp discovered;

    @Deprecated
    public String getPlayer() {

        return player;
    }

    @Deprecated
    public void setPlayer(String player) {

        this.player = player;
    }

    public UUID getPlayerId() {
        if(playerId == null) {
            playerId = UUIDUtil.convertPlayer(getPlayer());
        }
        return playerId;
    }
}
