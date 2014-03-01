package de.raidcraft.dragontravelplus.tables;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Table;
import de.raidcraft.dragontravelplus.DragonTravelPlusPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Author: Philip
 * Date: 28.11.12 - 20:41
 * Description:
 */
public class PlayerStationsTable extends Table {

    public PlayerStationsTable() {

        super("players", "dragontp_");
    }

    @Override
    public void createTable() {

        try {
            executeUpdate(
                    "CREATE TABLE `" + getTableName() + "` (" +
                            "`id` INT NOT NULL AUTO_INCREMENT, " +
                            "`player` VARCHAR( 32 ) NOT NULL, " +
                            "`station_name` VARCHAR( 32 ) NOT NULL, " +
                            "`discovered` VARCHAR ( 32 ) NOT NULL, " +
                            "PRIMARY KEY ( `id` )" +
                            ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void migrate() {

        try {
            EbeanServer database = RaidCraft.getComponent(DragonTravelPlusPlugin.class).getDatabase();
            ResultSet resultSet = executeQuery("SELECT * FROM `" + getTableName() + "` WHERE 1");

            int cnt = 0;
            while (resultSet.next()) {
                TPlayerStation station = new TPlayerStation();
                station.setPlayer(resultSet.getString("player"));
                station.setStation(database.find(TStation.class).where().eq("name", resultSet.getString("station_name")).findUnique());
                station.setDiscovered(Timestamp.valueOf(resultSet.getString("discovered")));
                database.save(station);
                cnt++;
            }
            RaidCraft.LOGGER.info("Migrated " + cnt + " DTP player stations!");
        } catch (SQLException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
}
