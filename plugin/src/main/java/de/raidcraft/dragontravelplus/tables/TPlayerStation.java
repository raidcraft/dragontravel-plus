package de.raidcraft.dragontravelplus.tables;

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
@Setter
@Getter
@Entity
@Table(name = "flight_player_stations")
public class TPlayerStation {

    @Id
    private int id;
    @ManyToOne
    private TStation station;
    private String player;
    private UUID playerId;
    private Timestamp discovered;
}
